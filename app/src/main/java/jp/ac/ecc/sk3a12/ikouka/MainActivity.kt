package jp.ac.ecc.sk3a12.ikouka

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //Database
    private lateinit var mDatabase: DatabaseReference
    //Toolbar
    private var mToolbar: Toolbar? = null
    //ViewPager
    private var mMainPager: ViewPager? = null
    //current user object
    public lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set Toolbar
        mToolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "ikouka"

        //Firebase Auth
        auth = FirebaseAuth.getInstance()
        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference()

        //--------------ViewPager-------------------
        mMainPager = findViewById(R.id.mainPager)
            //initialize PagerAdapter
        val mMainPagerAdapter: MainPagerAdapder = MainPagerAdapder(supportFragmentManager)
            //set MainPager Adapter
        mMainPager!!.adapter = mMainPagerAdapter
            //link TabBar to MainPager
        val mainTabBar : TabLayout = findViewById(R.id.mainTabBar)
        mainTabBar.setupWithViewPager(mMainPager)
        //-----------------------------------------

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        //if not loged in, go back to home
        if (currentUser == null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "signed in", Toast.LENGTH_SHORT).show()

            //create current user object/////////////////////////////////
                //listener
            val listener : ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    user = User(
                            currentUser!!.uid,
                            dataSnapshot.child("userName").value.toString(),
                            currentUser!!.email,
                            dataSnapshot.child("groups").value.toString(),
                            dataSnapshot.child("image").value.toString(),
                            dataSnapshot.child("thumbImage").value.toString()
                    )

                    Log.d("User", user.toString())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("database error", databaseError.message)
                }
            }
                //attatch listener
            mDatabase.child("Users").child(currentUser.uid).addListenerForSingleValueEvent(listener)
            //////////////////////////////////////////////////////
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
                intent.putExtra("uid", auth.currentUser!!.uid)
                startActivity(intent)
            }

            R.id.allGroups -> {
                val intent = Intent(this, AllGroupsActivity::class.java)
                startActivity(intent)
            }

            R.id.createGroup -> {
                val intent = Intent(this, CreateGroupActivity::class.java)
                intent.putExtra("currentUser", user)
                startActivity(intent)
            }
        }

        return true
    }

}
