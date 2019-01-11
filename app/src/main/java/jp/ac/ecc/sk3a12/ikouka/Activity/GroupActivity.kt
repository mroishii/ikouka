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
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firestore.admin.v1beta1.Progress
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
    private var groupId = ""
    //Auth
    private lateinit var mAuth: FirebaseAuth
    //Firestore
    private lateinit var mDb: FirebaseFirestore

    private val mContext: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

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
        //--------------ViewPager-------------------
        mGroupPager = findViewById(R.id.groupPager)
        //initialize PagerAdapter
        val mGroupPagerAdapter = GroupPagerAdapter(supportFragmentManager, groupId)
        //set MainPager Adapter
        mGroupPager!!.adapter = mGroupPagerAdapter
        //link TabBar to MainPager
        val groupTabBar : TabLayout = findViewById(R.id.groupTabBar)
        groupTabBar.setupWithViewPager(mGroupPager)
        //-----------------------------------------
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
