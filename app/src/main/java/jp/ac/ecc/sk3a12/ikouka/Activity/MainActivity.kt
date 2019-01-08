package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupsListFragment
import jp.ac.ecc.sk3a12.ikouka.R

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //Toolbar
    private var mToolbar: Toolbar? = null
    //Drawer
    private lateinit var mDrawerLayout: DrawerLayout
    //Firestore
    private lateinit var mDb: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Firebase Auth
        auth = FirebaseAuth.getInstance()

        //Check if logged in
        //if not loged in, go back to home
        if (auth.currentUser == null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Firestore
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings

        //DrawerLayout
        mDrawerLayout = findViewById(R.id.drawer_layout)

        //set Toolbar
        mToolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.apply {
            title = "ikouka!"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)

        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)

        val drawerHeader: View = layoutInflater.inflate(R.layout.nav_header, null)
        mDb.collection("Users")
                .document(auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    drawerHeader.findViewById<TextView>(R.id.drawer_header_username).text = it.getString("userName")
                    if (it.getString("image") != "default") {
                        Glide.with(applicationContext)
                                .load(it.getString("image"))
                                .into(drawerHeader.findViewById(R.id.drawer_header_image))
                    }

                    navigationView.addHeaderView(drawerHeader)

                    navigationView.setNavigationItemSelectedListener { menuItem ->
                        //Call menu item click event handler
                        onNavigationDrawerMenuClick(menuItem)
                        true
                    }

                    onNavigationDrawerMenuClick(navigationView.menu.findItem(R.id.grouplist))
                    navigationView.setCheckedItem(0)
                }



    }


    fun onNavigationDrawerMenuClick(item: MenuItem) {
        // set item as selected to persist highlight
        item.isChecked = true
        // close drawer when item is tapped
        mDrawerLayout.closeDrawers()
        // Add code here to update the UI based on the item selected
        // For example, swap UI fragments here
        when (item.itemId) {
            R.id.grouplist -> {
                val groupsListFragment: GroupsListFragment = GroupsListFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, groupsListFragment)
                        .addToBackStack(null)
                        .commit()
            }

            R.id.requestlist -> {

            }

            R.id.setting -> {

            }

            R.id.logout -> {
                var alert: AlertDialog.Builder = AlertDialog.Builder(this)
                alert.setMessage("アプリからログアウトしますか？")
                alert.setTitle("ログアウト")


                //YES button
                alert.setPositiveButton("はい") { dialog, which ->
                    auth.signOut()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

                alert.setNegativeButton("いいえ") {dialog, which ->
                    dialog.dismiss()
                }

                alert.show()
            }
        }
    }
    //up-right corner menu button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    //up-right corner menu button -> item click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        //check itemid
        when (item!!.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
            }

            //signout menu item
            R.id.signout -> {
                var alert: AlertDialog.Builder = AlertDialog.Builder(this)
                alert.setMessage("アプリからログアウトしますか？")
                alert.setTitle("ログアウト")
                alert.show()

                //YES button
                alert.setPositiveButton("はい") { dialog, which ->
                    auth.signOut()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

                alert.setNegativeButton("いいえ") {dialog, which ->
                    dialog.dismiss()
                }

            }
            //account menu item
            R.id.grouplist -> {
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
