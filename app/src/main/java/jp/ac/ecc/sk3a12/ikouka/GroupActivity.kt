package jp.ac.ecc.sk3a12.ikouka

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.widget.TextView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

class GroupActivity : AppCompatActivity() {
    //Toolbar
    private lateinit var mToolbar: Toolbar
    //View Pager
    private lateinit var mGroupPager: ViewPager
    lateinit var currentGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        // Get current group
        if (savedInstanceState != null) {
            currentGroup = savedInstanceState!!.getParcelable("group")
        }

        // Setup toolbar
        mToolbar = findViewById(R.id.groupToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = intent.getStringExtra("groupTitle")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //--------------ViewPager-------------------
        mGroupPager = findViewById(R.id.groupPager)
        //initialize PagerAdapter
        val mGroupPagerAdapter = GroupPagerAdapter(supportFragmentManager)
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
                var alert: AlertDialog.Builder = AlertDialog.Builder(this)
                alert.setMessage("ユーザIDを入力してください")
                alert.setTitle("ユーザ招待")

                var uidInput: EditText = EditText(this)
                var lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                uidInput.layoutParams = lp
                alert.setView(uidInput)

                alert.setPositiveButton("招待", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        var uid = uidInput.getText().toString()
                        Log.d("invite", uid)
                    }
                })

                alert.setNegativeButton("キャンセル", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.cancel()
                    }
                })

                alert.show()


            }
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("group", currentGroup)
    }
}
