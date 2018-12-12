package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Model.Event
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.Adapter.GroupPagerAdapter
import jp.ac.ecc.sk3a12.ikouka.R

class GroupActivity : AppCompatActivity() {
    private var TAG = "GroupActivity"
    //Toolbar
    private lateinit var mToolbar: Toolbar
    //View Pager
    private lateinit var mGroupPager: ViewPager
    //Current group
    private lateinit var currentGroup: Group
    //Auth
    private lateinit var auth: FirebaseAuth
    //Firestore
    private lateinit var mDb: FirebaseFirestore

    private val mContext: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        auth = FirebaseAuth.getInstance()

        //Firestore
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings

        // Get current group
        if (savedInstanceState != null) {
            currentGroup = savedInstanceState!!.getParcelable("group")
        }

        // Setup toolbar
        mToolbar = findViewById(R.id.groupToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = intent.getStringExtra("groupTitle")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Get current group from Db
        val groupId = intent.getStringExtra("groupId")
        startGetGroup(groupId)


    }

    fun startGetGroup(groupId: String) {
        mDb.collection("Groups").document(groupId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result == null) {
                            Log.d(TAG, "FIRESTORE -> CANNOT FIND THIS GROUP DOCUMENT -> $groupId")
                        } else {
                            Log.d(TAG, "FIRESTORE -> CURRENT GROUP DOCUMENT: " + task.result)
                            doneGetGroup(task.result)
                        }
                    } else {
                        Log.d(TAG, "FIRESTORE -> GET FAILED WITH ", task.exception)
                    }
                }
    }

    fun doneGetGroup(groupDs: DocumentSnapshot?) {
        //--------START BUILDING CURRENT GROUP OBJECT-----------------------------------------------------------
        currentGroup = Group(groupDs!!.id,
                groupDs!!.getString("title"),
                groupDs!!.getString("description"),
                groupDs!!.getString("owner"),
                groupDs!!.getString("image"))
        if (groupDs!!.get("events") != null) {
            var eventsMap: ArrayList<Map<String, Any>> = groupDs!!.get("events") as ArrayList<Map<String, Any>>

            for ((index, e) in eventsMap.withIndex()) {
                val start = java.sql.Timestamp((e.get("start") as Timestamp).seconds * 1000)
                val end = java.sql.Timestamp((e.get("end") as Timestamp).seconds * 1000)
                var event = Event(index.toString(),
                        e.get("title") as String,
                        e.get("description") as String,
                        start,
                        end,
                        e.get("owner") as String)

                Log.d(TAG, "Event object created -> " + event)
                currentGroup.addEvent(event)
            }
        }
        currentGroup.buildUserMap(groupDs.get("users") as Map<String, Any>)

        currentGroup.anketosId = groupDs.get("anketos") as ArrayList<String>

        //---------END BUILDING----------------------------------------------------------------------------------

        //--------------ViewPager-------------------
        mGroupPager = findViewById(R.id.groupPager)
        //initialize PagerAdapter
        val mGroupPagerAdapter = GroupPagerAdapter(supportFragmentManager, currentGroup)
        //set MainPager Adapter
        mGroupPager!!.adapter = mGroupPagerAdapter
        //link TabBar to MainPager
        val groupTabBar : TabLayout = findViewById(R.id.groupTabBar)
        groupTabBar.setupWithViewPager(mGroupPager)
        //-----------------------------------------


//        var bundle = Bundle()
//        bundle.putParcelable("currentGroup", currentGroup)
//        var frag: GroupCalendarFragment = supportFragmentManager!!.findFragmentByTag("groupCalendarFragment") as GroupCalendarFragment
//        frag.arguments = bundle


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

            //owner menu
            R.id.ownerMenu -> {
                Toast.makeText(this, "jump to owner menu", Toast.LENGTH_SHORT).show()
            }
            //invite
            R.id.invite -> {
                val currentUserId = auth.currentUser!!.uid
                if (currentGroup.owner != currentUserId) {
                    var alert: AlertDialog.Builder = AlertDialog.Builder(this)
                    alert.setMessage("この機能はグループの所有者のみ利用できます。")
                    alert.setTitle("ユーザ招待")
                    alert.show()
                } else {
                    var alert: AlertDialog.Builder = AlertDialog.Builder(this)
                    alert.setMessage("メールアドレスを入力してください")
                    alert.setTitle("ユーザ招待")

                    var mailInput: EditText = EditText(this)
                    var lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT)
                    mailInput.layoutParams = lp
                    alert.setView(mailInput)

                    alert.setPositiveButton("招待", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            var mail = mailInput.getText().toString()

                            mDb.collection("Users")
                                    .whereEqualTo("email", mail)
                                    .get()
                                    .addOnSuccessListener {
                                        //get invited user's id from query result
                                        var invitedId = it.documents.get(0).id
                                        var invitedName = it.documents.get(0).get("userName") as String
                                        Log.d(TAG, "FOUND USER $mail -> $invitedId , $invitedName")

                                        //update group users field
                                        var userMap: HashMap<String, Any> = HashMap()
                                        userMap.put("displayName", invitedName)
                                        userMap.put("image", "default")
                                        userMap.put("roles", arrayListOf("member"))

                                        var usersMap: HashMap<String, Any> = currentGroup.users as HashMap<String, Any>
                                        usersMap.put(invitedId, userMap)
                                        Log.d(TAG, "usersMap -> $usersMap")
                                        mDb.collection("Groups")
                                                .document(currentGroup.groupId)
                                                .update("users", usersMap)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "UPDATE GROUP USERS FIELD SUCCESSFULLY")
                                                    //update invited user groups
                                                    mDb.collection("Users")
                                                            .document(invitedId)
                                                            .update("groups", FieldValue.arrayUnion(currentGroup.groupId))
                                                            .addOnSuccessListener {
                                                                Log.d(TAG, "UPDATE INVITED USER's GROUPS FIELD SUCCESSFULLY")
                                                                Toast.makeText(mContext, "招待が完了しました！", Toast.LENGTH_LONG)
                                                            }
                                                }


                                    }
                        }
                    })

                    alert.setNegativeButton("キャンセル", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            dialog.cancel()
                        }
                    })

                    alert.show()
                }
            }
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("group", currentGroup)
    }
}
