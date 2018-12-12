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

    //Users Map
    private var usersMap = HashMap<String, Any>()
    //Answered Map
    private var answeredMap: HashMap<String, Boolean> = HashMap()


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

        //Access Group Database
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
        usersMap = groupDs!!.get("users") as HashMap<String, Any>
        var usersId = ArrayList<String>()
        for (key in usersMap.keys) {
            usersId.add(key)
        }
        for (id in usersId) {
            answeredMap.put(id, false)
        }
        Log.d(TAG, "USERSMAP CREATED -> $answeredMap")
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
        var anketoAnswers = HashMap<String, Any>()

        for((index, value) in answersViews.withIndex()) {
            var answerMap = HashMap<String, Any>()
            answerMap.put("description", value.findViewById<EditText>(R.id.answer_input).text.toString())
            answerMap.put("answered", answeredMap)
            anketoAnswers.put((index + 1).toString(), answerMap)
        }

        var anketoMap = HashMap<String, Any>()
        anketoMap.put("title", anketoTitle)
        anketoMap.put("created", anketoCreated)
        anketoMap.put("due", anketoDue)
        anketoMap.put("description", anketoDescription)
        anketoMap.put("owner", anketoOwner)
        anketoMap.put("type", anketoType)
        anketoMap.put("answers", anketoAnswers)

        Log.d(TAG, "ANKETOMAP CREATED -> $anketoMap")

        mDb.collection("Anketo")
                .add(anketoMap as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d(TAG, "ANKETO SUCCESSFULLY CREATED")
                    var createdId = it.id
                    mDb.collection("Groups")
                            .document(currentGroupId)
                            .update("anketos", FieldValue.arrayUnion(createdId))
                            .addOnSuccessListener {
                                Log.d(TAG, "CREATED ANKETO ADDED TO GROUP")
                                var intent = Intent(this, AnketoActivity::class.java)
                                intent.putExtra("anketoId", createdId)
                                intent.putExtra("users", usersMap)
                                startActivity(intent)
                                finish()
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
