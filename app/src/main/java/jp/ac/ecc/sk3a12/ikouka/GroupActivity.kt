package jp.ac.ecc.sk3a12.ikouka

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.widget.TextView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class GroupActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var mGroupPager: ViewPager
    private lateinit var groupTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        mToolbar = findViewById(R.id.groupToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Group Dashboard"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //--------------ViewPager-------------------
        mGroupPager = findViewById(R.id.groupPager)
        //initialize PagerAdapter
        val mGroupPagerAdapter : GroupPagerAdapter = GroupPagerAdapter(supportFragmentManager)
        //set MainPager Adapter
        mGroupPager!!.adapter = mGroupPagerAdapter
        //link TabBar to MainPager
        val groupTabBar : TabLayout = findViewById(R.id.groupTabBar)
        groupTabBar.setupWithViewPager(mGroupPager)
        //-----------------------------------------

        groupTitle = findViewById(R.id.groupview_groupTitle)
    }

    override fun onStart() {
        super.onStart()

        var group: Group = intent.getParcelableExtra("group")
        var groupString = "Group id: " + group.groupId
        groupString += System.lineSeparator() + "Group title: " + group.title
        groupString += System.lineSeparator() + "Group description: " +group.description
        groupString += System.lineSeparator() + "Group owner: " +group.owner
        groupString += System.lineSeparator() + "Group image: " +group.image
        groupString += System.lineSeparator() + "Events: "
        for (event in group.events) {
            groupString += System.lineSeparator() + " Event id:" + event.eventId
            groupString += System.lineSeparator() + "    Event title:" + event.title
            groupString += System.lineSeparator() + "    Event owner:" + event.owner
            groupString += System.lineSeparator() + "    Event description:" + event.description
            groupString += System.lineSeparator() + "    Event start and end:" + event.start + "," + event.end
            groupString += System.lineSeparator() + ""
        }


        groupTitle!!.text = groupString

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
            //signout menu item
            R.id.ownerMenu -> {
                Toast.makeText(this, "jump to owner menu", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }
}
