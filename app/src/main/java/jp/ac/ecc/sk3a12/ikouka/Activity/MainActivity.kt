package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Adapter.MainPagerAdapder
import jp.ac.ecc.sk3a12.ikouka.Model.User
import jp.ac.ecc.sk3a12.ikouka.R
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //Toolbar
    private var mToolbar: Toolbar? = null
    //ViewPager
    private var mMainPager: ViewPager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Firebase Auth
        auth = FirebaseAuth.getInstance()
        //set Toolbar
        mToolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "ikouka!"


        //Check if logged in
        //if not loged in, go back to home
        if (auth.currentUser == null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "ようこそ！", Toast.LENGTH_SHORT).show()
            //--------------ViewPager-------------------
            mMainPager = findViewById(R.id.mainPager)
            //initialize PagerAdapter
            val mMainPagerAdapter = MainPagerAdapder(supportFragmentManager)
            //set MainPager Adapter
            mMainPager!!.adapter = mMainPagerAdapter
            //link TabBar to MainPager
            val mainTabBar: TabLayout = findViewById(R.id.mainTabBar)
            mainTabBar.setupWithViewPager(mMainPager)
            //-----------------------------------------
        }

    }


    //up-right corner menu button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    //up-right corner menu button -> item click event
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        //check itemid
        when (item!!.itemId) {
            //signout menu item
            R.id.signout -> {
                auth.signOut()
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            //account menu item
            R.id.account -> {
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }

            R.id.createGroup -> {
                val intent = Intent(this, CreateGroupActivity::class.java)
                startActivity(intent)
            }

            R.id.testMenu -> {
                val intent = Intent(this, AllGroupsActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

}
