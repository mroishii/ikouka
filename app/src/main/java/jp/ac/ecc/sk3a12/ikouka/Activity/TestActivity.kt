package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupCalendarFragment
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupChatFragment
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupDashboardFragment
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {
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
                        .addToBackStack("GROUP_DASHBOARD")
                        .commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.group_calendar -> {
                val fragment = GroupCalendarFragment()
                fragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack("GROUP_CALENDAR")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.group_chat -> {
                val fragment = GroupChatFragment()
                fragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack("GROUP_CHAT")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }

            R.id.group_anketo -> {

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
        setContentView(R.layout.activity_test)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mAuth = FirebaseAuth.getInstance()

        //Firestore
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings

        // Setup toolbar
        mToolbar = findViewById(R.id.groupToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = intent.getStringExtra("groupTitle")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Get current group from Db
        val groupId = intent.getStringExtra("groupId")
        args.putString("groupId", groupId)

        navigation.selectedItemId = R.id.group_dashboard
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
//
            }
        }

        return true
    }
}
