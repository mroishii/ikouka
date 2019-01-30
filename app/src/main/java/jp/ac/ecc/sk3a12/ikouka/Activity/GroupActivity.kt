package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupAnketoFragment
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupCalendarFragment
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupChatFragment
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupDashboardFragment
import jp.ac.ecc.sk3a12.ikouka.Magic
import jp.ac.ecc.sk3a12.ikouka.Model.Activity
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.activity_test.*
import java.util.HashMap

open class GroupActivity : AppCompatActivity() {

    private var TAG = "GroupActivity"
    //Toolbar
    private lateinit var mToolbar: Toolbar
    //Current group
    private var groupId = ""
    //Fragment arguments
    private var args: Bundle = Bundle()
    //Auth
    private lateinit var mAuth: FirebaseAuth
    //Firestore
    private lateinit var mDb: FirebaseFirestore

    private val mContext: Context = this

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.group_dashboard -> {
                val fragment = GroupDashboardFragment()
                fragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.group_calendar -> {
                val fragment = GroupCalendarFragment()
                fragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.group_chat -> {
                val fragment = GroupChatFragment()
                fragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }

            R.id.group_anketo -> {
                val fragment = GroupAnketoFragment()
                fragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }

            R.id.group_task -> {

                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mAuth = FirebaseAuth.getInstance()

        //Firestore
        mDb = Magic.getDbInstance()

        // Setup toolbar
        mToolbar = findViewById(R.id.groupToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = intent.getStringExtra("groupTitle")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Get current group from Db
        groupId = intent.getStringExtra("groupId")
        args.putString("groupId", groupId)

        navigation.selectedItemId = R.id.group_dashboard

        navigation.labelVisibilityMode = 1

//        //--------------ViewPager-------------------
//        mGroupPager = findViewById(R.id.groupPager)
//        //initialize PagerAdapter
//        val mGroupPagerAdapter = GroupPagerAdapter(supportFragmentManager, groupId)
//        //set MainPager Adapter
//        mGroupPager!!.adapter = mGroupPagerAdapter
//        //link TabBar to MainPager
//        val groupTabBar : TabLayout = findViewById(R.id.groupTabBar)
//        groupTabBar.setupWithViewPager(mGroupPager)
//        //-----------------------------------------

    }

    //up-right corner menu button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.group_menu, menu)
        return true
    }

    //up-right corner menu button -> item click event
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        //check itemid
        when (item!!.itemId) {
            //back button
            android.R.id.home -> {
                onBackPressed()
            }
            
            //resign button
            R.id.resign -> {
                mDb.document("Groups/$groupId")
                        .get()
                        .addOnSuccessListener {
                            if (it.getString("owner") != mAuth.currentUser!!.uid ) {
                                showResignDialog()
                            } else {
                                AlertDialog.Builder(this).apply{
                                    setTitle("退会できません！")
                                    setMessage("あなたはグループ所有者です")
                                    setPositiveButton("OK") { dialog, which ->
                                        dialog.dismiss()
                                    }
                                    create().show()

                                }
                            }
                        }
            }
        }

        return true
    }

    private fun showResignDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("本当に退会しますか？")
        alertDialogBuilder.setMessage("あなたが追加した内容（メッセージ、アンケート、イベントなど）が残りますが、このグループにアクセスできなくなります。")
        var resignCheckBox = CheckBox(this)
        resignCheckBox.text = "以上、分かりました。退会したいです。"
        alertDialogBuilder.setView(resignCheckBox)

        alertDialogBuilder.setPositiveButton("退会") { dialog, which ->
            if (resignCheckBox.isChecked) {
                //switch user status in group to "resigned"
                var path ="/Groups/$groupId/Users/${mAuth.currentUser!!.uid}"
                mDb.document(path)
                        .update("status", "resigned")
                        .addOnSuccessListener {
                            //delete this user in group's usersId array
                            mDb.document("Groups/$groupId")
                                    .update("usersId", FieldValue.arrayRemove(mAuth.currentUser!!.uid))
                                    .addOnSuccessListener {
                                        var activityMap = HashMap<String, Any>().apply {
                                            put("userId", mAuth.currentUser!!.uid)
                                            put("action", Activity.LEFT_GROUP)
                                            put("reference", Activity.NOREF)
                                            put("timestamp", Timestamp.now())
                                        }

                                        //Write group activity about the resignation
                                        mDb.collection("Groups/$groupId/Activities")
                                                .add(activityMap)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "退会しました！", Toast.LENGTH_SHORT)
                                                    var intent = Intent(this, MainActivity::class.java)
                                                    startActivity(intent)
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(this, "FAILED AT => ${it.message}", Toast.LENGTH_LONG)
                                                }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "FAILED AT => ${it.message}", Toast.LENGTH_LONG)
                                    }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "FAILED AT => ${it.message}", Toast.LENGTH_LONG)
                        }
            } else {
                Toast.makeText(this, "チェックボックスをチェックしてください", Toast.LENGTH_LONG)
            }
        }

        alertDialogBuilder.setNeutralButton("キャンセル") { dialog, which ->
            dialog.dismiss()
        }

        alertDialogBuilder.setCancelable(true)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setOnShowListener {
            (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            resignCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = isChecked
            }
        }
        alertDialog.show()
    }
}
