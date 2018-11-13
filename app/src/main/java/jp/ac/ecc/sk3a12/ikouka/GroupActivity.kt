package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.support.v7.widget.Toolbar

class GroupActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var groupTitle: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        mToolbar = findViewById(R.id.groupToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Group Dashboard"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        groupTitle = findViewById(R.id.groupview_groupTitle)
    }

    override fun onStart() {
        super.onStart()

        var group: Group = intent.getParcelableExtra("group")
        groupTitle!!.text = group.title

    }
}
