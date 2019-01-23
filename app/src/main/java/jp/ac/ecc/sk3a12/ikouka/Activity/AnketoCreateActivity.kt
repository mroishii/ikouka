package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import jp.ac.ecc.sk3a12.ikouka.R

class AnketoCreateActivity : AppCompatActivity() {
    private val TAG = "AktCreateActiv"

    private var currentGroupId: String = ""

    //Toolbar
    private var mToolbar: Toolbar? = null

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore

    //Activity elements
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var answersContainer: LinearLayout
    private lateinit var answerAddButton: Button
    private lateinit var createButton: Button

    //Answers view array
    private var answersViews: ArrayList<View> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anketo_create)

        //Toolbar
        mToolbar = findViewById(R.id.anketo_create_actionbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "アンケート作成"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //elements
        title = findViewById(R.id.createAnketoTitle)
        description = findViewById(R.id.createAnketoDescription)
        answersContainer = findViewById(R.id.createAnketoAnswersContainer)
        answerAddButton = findViewById(R.id.createAnketoAddAnswerButton)
        createButton = findViewById(R.id.createAnketoCreateButton)

        answerAddButton.setOnClickListener {
            var view = layoutInflater.inflate(R.layout.anketo_create_answer_item, null)
            answersViews.add(view)
            answersContainer.addView(view)
        }

        createButton.setOnClickListener {
            if (isInputOk()) {
                doCreateAnketo()
            } else {

            }
        }

        //Firebase
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()

        //Current Group Id
        currentGroupId = intent.getStringExtra("groupId")

    }

    override fun onStart() {
        super.onStart()

        //first add at least 2 answers
        answerAddButton.performClick()
        answerAddButton.performClick()

    }

    fun isInputOk(): Boolean {
        for (view in answersViews) {
            val answerInput: EditText = view.findViewById(R.id.answer_input)
            if (TextUtils.isEmpty(answerInput.text.toString())) {
                Toast.makeText(this, "全フィールドを入力してください", Toast.LENGTH_LONG)
                return false
            }
        }

        if (TextUtils.isEmpty(title.text.toString()) || TextUtils.isEmpty(description.text.toString())) {
            Toast.makeText(this, "全フィールドを入力してください", Toast.LENGTH_LONG)
            return false
        }

        return true
    }

    fun doCreateAnketo() {
        var anketoTitle = title.text.toString()
        var anketoDescription = description.text.toString()
        var anketoOwner = mAuth.currentUser!!.uid
        var anketoCreated = Timestamp.now()
        var anketoDue = Timestamp(anketoCreated.seconds + 60*24*10, 0)
        var anketoType = "multiple"

        var anketoMap = HashMap<String, Any>()
        anketoMap.put("title", anketoTitle)
        anketoMap.put("created", anketoCreated)
        anketoMap.put("due", anketoDue)
        anketoMap.put("description", anketoDescription)
        anketoMap.put("owner", anketoOwner)
        anketoMap.put("type", anketoType)

        mDb.collection("Groups/$currentGroupId/Anketos")
                .add(anketoMap as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d(TAG, "ANKETO SUCCESSFULLY CREATED")
                    var createdId = it.id

                    var batch = mDb.batch()
                    for((index, value) in answersViews.withIndex()) {
                        var answerMap = HashMap<String, Any>()
                        answerMap.put("description", value.findViewById<EditText>(R.id.answer_input).text.toString())
                        answerMap.put("answered", object: ArrayList<String>() {})

                        val docRef = mDb.document("Groups/$currentGroupId/Anketos/$createdId/Answers/$index")
                        batch.set(docRef, answerMap)
                    }


                    batch.commit()
                            .addOnSuccessListener {
                                Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "FAILED AT => ${it.message}", Toast.LENGTH_LONG)
                            }
                }

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
