package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Model.Request
import jp.ac.ecc.sk3a12.ikouka.R


class UserInviteFragment : DialogFragment() {
    private val TAG = "UserInviteFrag"

    private var groupId: String? = null

    private val mAuth = FirebaseAuth.getInstance()
    private val mDb = FirebaseFirestore.getInstance()

    //layout
    private lateinit var emailInput: EditText
    private lateinit var userimage: CircleImageView
    private lateinit var username: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultLayout = view.findViewById<ConstraintLayout>(R.id.searchResult)
        resultLayout.visibility = ConstraintLayout.INVISIBLE

        emailInput = view.findViewById(R.id.searchEmailInput)
        userimage = view.findViewById(R.id.userimage)
        username = view.findViewById(R.id.username)

        val notFoundTxt = view.findViewById<TextView>(R.id.notFound)

        var inviteId = ""

        view.findViewById<Button>(R.id.searchBtn).setOnClickListener {
            resultLayout.visibility = ConstraintLayout.INVISIBLE
            notFoundTxt.visibility = TextView.INVISIBLE
            //Search for user with email
            mDb.collection("Users")
                    .whereEqualTo("email", emailInput.text.toString())
                    .get()
                    .addOnSuccessListener {
                        //If user not found
                        if (it.documents.count() == 0) {
                            notFoundTxt.text = "ユーザが見つからなかった。"
                            notFoundTxt.visibility = TextView.VISIBLE
                        } else {
                            val user = it.documents.get(0)
                            //Check if user's already member
                            mDb.collection("Groups")
                                    .document(groupId!!)
                                    .get()
                                    .addOnSuccessListener {
                                        //if already member, show warnint
                                        if ((it.get("usersId") as ArrayList<String>).contains(user.id)) {
                                            notFoundTxt.text = "このユーザはメンバーになりました。"
                                            notFoundTxt.visibility = TextView.VISIBLE
                                        } else {
                                            inviteId = user.id
                                            //show search result
                                            username.text = user.getString("userName")
                                            if (user.getString("image") != "default") {
                                                Glide.with(context!!)
                                                        .load(user.getString("image"))
                                                        .into(userimage)
                                            }
                                            resultLayout.visibility = ConstraintLayout.VISIBLE
                                        }
                                    }

                        }
                    }
        }

        view.findViewById<Button>(R.id.inviteBtn).setOnClickListener {
            var requestMap = HashMap<String, Any>()
            requestMap.put("from", mAuth.currentUser!!.uid)
            requestMap.put("to", inviteId)
            requestMap.put("timestamp", Timestamp.now())
            requestMap.put("groupId", groupId!!)
            requestMap.put("status", Request.WAITING)

            mDb.collection("Request")
                    .add(requestMap)
                    .addOnSuccessListener {
                        Toast.makeText(context, "招待しました。", Toast.LENGTH_SHORT)
                        this.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "招待に失敗しました。", Toast.LENGTH_SHORT)
                        Log.d(TAG, "DATABASE ERROR => ${it.message}")
                    }
        }

        view.findViewById<Button>(R.id.header_exit).setOnClickListener {
            this.dismiss()
        }
    }



    companion object {
        @JvmStatic
        fun newInstance(groupId: String) =
                UserInviteFragment().apply {
                    arguments = Bundle().apply {
                        putString("groupId", groupId)
                    }
                }
    }
}
