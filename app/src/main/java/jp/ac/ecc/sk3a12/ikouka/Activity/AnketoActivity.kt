package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Adapter.AnketoMultipleAnswerListAdapter
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer
import jp.ac.ecc.sk3a12.ikouka.Model.MyBuilder
import jp.ac.ecc.sk3a12.ikouka.R
import java.util.*

class AnketoActivity : AppCompatActivity() {
    private val TAG = "AktActi"

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore

    //Builder
    private var myBuilder = MyBuilder(TAG)

    //Toolbar
    private var mToolbar: Toolbar? = null

    //Current Anketo
    private lateinit var currentAnketo: Anketo

    //Users map
    private lateinit var usersMap: HashMap<String, Any>

    //ACtivity elements
    private var image: CircleImageView? = null
    private var title: TextView? = null
    private var due: TextView? = null
    private var description: TextView? = null

    //RecyclerView + adapter + linear layout manager
    private var anketoAnswers: ArrayList<AnketoAnswer> = ArrayList()
    private var anketoAnswer_listview: RecyclerView? = null
    private lateinit var anketoAnswer_adapter: AnketoMultipleAnswerListAdapter
    private lateinit var linearLayout: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anketo)

//        //Firebase
//        mAuth = FirebaseAuth.getInstance()
//        mDb = FirebaseFirestore.getInstance()
//        val settings = FirebaseFirestoreSettings.Builder()
//                .setTimestampsInSnapshotsEnabled(true)
//                .build()
//        mDb.firestoreSettings = settings
//
//        //Toolbar
//        mToolbar = findViewById(R.id.anketo_actionbar)
//        setSupportActionBar(mToolbar)
//        supportActionBar!!.setTitle("アンケート回答")
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//
//        //Activity elements
//        image = findViewById(R.id.anketo_owner_image)
//        title = findViewById(R.id.anketo_title)
//        due = findViewById(R.id.anketo_due)
//        description = findViewById(R.id.anketo_description)
//        //RecyclerVIew
//        anketoAnswer_listview = findViewById(R.id.anketo_answers)
//        linearLayout = LinearLayoutManager(this)
//        anketoAnswer_listview!!.setHasFixedSize(true)
//        anketoAnswer_listview!!.layoutManager = linearLayout
//
//        //get usersmaps
//        usersMap = intent.getSerializableExtra("users") as HashMap<String, Any>
//
//        //get current anketo
//        var anketoId = intent.getStringExtra("anketoId")
//        mDb.collection("Anketo")
//                .document(anketoId)
//                .get()
//                .addOnCompleteListener {task ->
//                    if (task.isSuccessful()) {
//                        val anketo = task.getResult()
//                        if (anketo!!.exists()) {
//                            Log.d(TAG, "GOT ANKETO -> " + anketo!!)
//                            currentAnketo = myBuilder.buildAnketoObject(anketo)
//
//                            title!!.text = currentAnketo.title
//                            val dueDate: Date = Date(currentAnketo.due)
//                            due!!.text = "締切：${dueDate.toString()}"
//                            description!!.text = currentAnketo.description
//
//                            for (key in currentAnketo.answers.keys) {
//                                var answerMap = currentAnketo.answers.get(key) as HashMap<String, Any>
//                                var answer = AnketoAnswer(key,
//                                        answerMap.get("description") as String,
//                                        answerMap.get("answered") as HashMap<String, Boolean>)
//                                anketoAnswers.add(answer)
//
//                            }
//
//                            anketoAnswer_adapter = AnketoMultipleAnswerListAdapter(this, anketoId,anketoAnswers, usersMap, mAuth.currentUser!!.uid)
//                            anketoAnswer_listview!!.adapter = anketoAnswer_adapter
//                        } else {
//                            Log.d(TAG, "ANKETO NOT EXISTED")
//                        }
//                    } else {
//                        Log.d(TAG, "GET FAILED AT -> " + task.getException()!!)
//                    }
//                }



    }

//    fun moveAnketo () {
//        var anketoMap: HashMap<String, Any> = HashMap()
//        var answersMap: HashMap<String, Any> = HashMap()
//        for (key in currentAnketo.answers.keys) {
//            var answerMap = currentAnketo.answers.get(key) as HashMap<String, Any>;
//            answersMap.put(key, answerMap)
//        }
//
//        anketoMap.put("title", currentAnketo.title)
//        anketoMap.put("type", currentAnketo.type)
//        anketoMap.put("owner", currentAnketo.owner)
//        anketoMap.put("created", currentAnketo.created)
//        anketoMap.put("due", currentAnketo.due)
//        anketoMap.put("description", currentAnketo.description)
//        anketoMap.put("answers", answersMap)
//
//
//        mDb.collection("Anketo")
//                .add(anketoMap)
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        Log.d(TAG, "MOVED COMPLETE. DATA -> " + it.result)
//                    } else {
//                        Log.d(TAG, "MOVED FAILED -> " + it.exception)
//                    }
//                }
//    }

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
