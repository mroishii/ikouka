package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar

class GroupChatActivity : AppCompatActivity() {
    //Toolbar
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        //Toolbar
        mToolbar = findViewById(R.id.groupChatActionBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Chat"
        supportActionBar!!.subtitle = "ima chatting with my friend"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
