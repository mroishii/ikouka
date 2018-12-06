package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.R

class AnketoListActivity : AppCompatActivity() {
    private val TAG = "AnkLstActi"

    //Anketo List Map
    private var anketoIds: ArrayList<String> = ArrayList()

    //Group User Map


    //Action bar
    private lateinit var mToolbar: Toolbar

    //Firestore
    private lateinit var mDb: FirebaseFirestore

    //Current Group

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
        var anketoMap: HashMap<String, Any> = groupDs!!.get("anketo") as HashMap<String, Any>
        Log.d(TAG, "anketoMap -> $anketoMap")
        for (id in anketoMap.keys) {
            anketoIds.add(id)
        }
        Log.d(TAG, "anketoIds -> $anketoIds")
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
