package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Adapter.AnketoListAdapter
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer
import jp.ac.ecc.sk3a12.ikouka.R

class AnketoListActivity : AppCompatActivity() {
    private val TAG = "AnkLstActi"

    //Anketo List Map
    private var anketosList: ArrayList<Anketo> = ArrayList()

    //Action bar
    private lateinit var mToolbar: Toolbar

    //Firestore
    private lateinit var mDb: FirebaseFirestore

    //RecyclerView + adapter + linear layout manager
    private var anketo_listview: RecyclerView? = null
    private lateinit var anketoAdapter: AnketoListAdapter
    private lateinit var linearLayout: LinearLayoutManager

    //Group Users Map
    private lateinit var usersMap: HashMap<String, HashMap<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anketo_list)

        //ActionBar
        mToolbar = findViewById(R.id.anketo_list_actionbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "アンケート一覧"
        supportActionBar!!.subtitle = intent.getStringExtra("groupTitle")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Firestore
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings

        //User Maps
        usersMap = intent.getSerializableExtra("groupUsers") as HashMap<String, HashMap<String, String>>

        //Recycler View
        anketo_listview = findViewById(R.id.anketo_listview)
        linearLayout = LinearLayoutManager(this)
        anketo_listview!!.setHasFixedSize(true)
        anketo_listview!!.layoutManager = linearLayout
        //set adapter
        anketoAdapter = AnketoListAdapter(this, anketosList, usersMap)
        anketo_listview!!.adapter = anketoAdapter

        //Access Group Database
        var currentGroupId = intent.getStringExtra("groupId")
        mDb.collection("Groups")
                .document(currentGroupId)
                .get()
                .addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        if (task.result == null) {
                            Log.d(TAG, "FIRESTORE -> CANNOT FIND THIS GROUP DOCUMENT -> $currentGroupId")
                        } else {
                            Log.d(TAG, "FIRESTORE -> CURRENT GROUP DOCUMENT: " + task.result)
                            doneGetGroup(task.result)
                        }
                    } else {
                        Log.d(TAG, "FIRESTORE -> GET FAILED WITH ", task.exception)
                    }
                }



    }

    fun doneGetGroup(groupDs: DocumentSnapshot?) {
        var anketosMap: HashMap<String, Any> = groupDs!!.get("anketo") as HashMap<String, Any>
        Log.d(TAG, "anketoMap -> $anketosMap")
        for (key in anketosMap.keys) {
            val anketoMap: HashMap<String, Any> = anketosMap.get(key)  as HashMap<String, Any>
            val anketo = Anketo(key ,
                    anketoMap.get("title") as String,
                    anketoMap.get("description") as String,
                    anketoMap.get("owner") as String )

            val answersMap: HashMap<String, Any> = anketoMap.get("answers") as HashMap<String, Any>
            for(key in answersMap.keys) {
                val answerMap: HashMap<String, Any> = answersMap.get(key) as HashMap<String, Any>
                Log.d(TAG, "AnswerMap created -> $answerMap")
                anketo.putAnswer(key, answerMap)
            }

            Log.d(TAG, "Anketo Object Created -> $anketo")
            anketosList.add(anketo)
            anketoAdapter.notifyDataSetChanged()
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
