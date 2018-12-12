package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Adapter.AnketoListAdapter
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer
import jp.ac.ecc.sk3a12.ikouka.R

class AnketoListActivity : AppCompatActivity() {
    private val TAG = "AnkLstActi"

    //Anketos Id List
    private var anketosId: ArrayList<String> = ArrayList()

    //Action bar
    private lateinit var mToolbar: Toolbar

    //FirebaseAuth
    private lateinit var mAuth: FirebaseAuth

    //Firestore
    private lateinit var mDb: FirebaseFirestore

    //RecyclerView + adapter + linear layout manager
    private var anketo_listview: RecyclerView? = null
    private lateinit var anketoAdapter: AnketoListAdapter
    private lateinit var linearLayout: LinearLayoutManager

    //Group Users Map
    private lateinit var usersMap: HashMap<String, Any>
    //Current Group Id
    private var currentGroupId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anketo_list)

        //ActionBar
        mToolbar = findViewById(R.id.anketo_list_actionbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "アンケート一覧"
        supportActionBar!!.subtitle = intent.getStringExtra("groupTitle")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        //Firestore
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings

        //User Maps
        usersMap = intent.getSerializableExtra("groupUsers") as HashMap<String, Any>

        //Recycler View
        anketo_listview = findViewById(R.id.anketo_listview)
        linearLayout = LinearLayoutManager(this)
        anketo_listview!!.setHasFixedSize(true)
        anketo_listview!!.layoutManager = linearLayout
        //set adapter
        anketoAdapter = AnketoListAdapter(this, anketosId, usersMap, mAuth.currentUser!!.uid)
        anketo_listview!!.adapter = anketoAdapter

        //Access Group Database
        currentGroupId = intent.getStringExtra("groupId")
        mDb.collection("Groups")
                .document(currentGroupId)
                .addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@EventListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.data)
                        var groupAnketos = snapshot.get("anketos") as ArrayList<String>
                        for (id in groupAnketos) {
                            if (anketosId.contains(id)) {
                                Log.d(TAG, "ANKETO ALREADY SHOWN")
                            } else {
                                Log.d(TAG, "NEW ANKETO FOUND")
                                anketosId.add(id)
                                anketoAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                })


        //Floating Button
        var fab = findViewById<FloatingActionButton>(R.id.anketo_list_fab)
        fab.setOnClickListener {
            var intent = Intent(this, AnketoCreateActivity::class.java)
            intent.putExtra("groupId", currentGroupId)
            startActivity(intent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
