package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import jp.ac.ecc.sk3a12.ikouka.Model.Activity
import jp.ac.ecc.sk3a12.ikouka.Model.Request
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.requestlist_item.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RequestFragment : Fragment() {
    //Firebase auth
    private val TAG = "RequestListFrag"

    //RecycleView
    private lateinit var  mRecyclerView: RecyclerView
    //Database Reference
    private lateinit var mDatabase: FirebaseFirestore
    //Firebase auth
    private lateinit var mAuth: FirebaseAuth

    private var mContext = this
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //init firebase
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //RecycleView
        mRecyclerView = view.findViewById(R.id.requestslist)
        mRecyclerView.setHasFixedSize(true)
//        mRecyclerView.layoutManager = GridLayoutManager(context, 2)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        var query: Query = mDatabase.collection("Request")
                .whereEqualTo("to", mAuth.currentUser!!.uid)
                .whereEqualTo("status", Request.WAITING)

        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                //エラーメッセ―ジを表示する
                return@addSnapshotListener
            }

            Log.d("snapshot", snapshot!!.documents.toString())
        }

        val options = FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, object : SnapshotParser<Request> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): Request {
                        return Request(snapshot.id,
                                snapshot.getString("from"),
                                snapshot.getString("to"),
                                snapshot.getString("groupId"),
                                snapshot.get("timestamp") as Timestamp,
                                snapshot.getString("status"))
                    }
                })
                .build()

        val adapter: FirestoreRecyclerAdapter<Request, RequestViewHolder> = object : FirestoreRecyclerAdapter<Request, RequestViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
                return RequestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.requestlist_item, parent, false))
            }

            override fun onBindViewHolder(holder: RequestViewHolder, position: Int, model: Request) {
                holder.groupImage.setClipToOutline(true)

                mDatabase.collection("Groups")
                        .document(model.groupId)
                        .get()
                        .addOnSuccessListener {
                            holder.groupTitle.text =it.getString("title")
                            holder.groupDescription.text = it.getString("description")
                            if (it.getString("image") != "default") {
                                Glide.with(mContext)
                                        .load(it.getString("image"))
                                        .into(holder.groupImage)
                            }
                        }

                holder.acceptBtn.setOnClickListener {
                    holder.acceptBtn.isEnabled = false
                    holder.denyBtn.isEnabled = false
                    //update request status to accepted
                    mDatabase.collection("Request")
                            .document(model.id)
                            .update("status", Request.ACCEPTED)
                            .addOnSuccessListener {
                                //then add this user to target group usersId array
                                mDatabase.collection("Groups")
                                        .document(model.groupId)
                                        .update("usersId", FieldValue.arrayUnion(model.to))
                                        .addOnSuccessListener {
                                            //update Group Users collection
                                            var userMap = HashMap<String, Any>()
                                            userMap.put("status", "active")
                                            userMap.put("roles", listOf("member"))
                                            mDatabase.collection("Groups/${model.groupId}/Users")
                                                    .document(model.to)
                                                    .set(userMap)
                                                    .addOnSuccessListener {
                                                        //Write new activity to notify group member
                                                        var activityMap = HashMap<String, Any>().apply {
                                                            put("userId", mAuth.currentUser!!.uid)
                                                            put("action", Activity.JOINED_GROUP)
                                                            put("timestamp", Timestamp.now())
                                                            put("reference", Activity.NOREF)
                                                        }

                                                        mDatabase.collection("Groups/${model.groupId}/Activities")
                                                                .add(activityMap)
                                                                .addOnSuccessListener {
                                                                    Toast.makeText(context, "招待を承認しました。", Toast.LENGTH_SHORT)
                                                                }
                                                                .addOnFailureListener{
                                                                    holder.acceptBtn.isEnabled = true
                                                                    holder.denyBtn.isEnabled = true
                                                                    Log.d(TAG, "DATABASE ERROR => ${it.message}")
                                                                }

                                                    }
                                                    .addOnFailureListener{
                                                        holder.acceptBtn.isEnabled = true
                                                        holder.denyBtn.isEnabled = true
                                                        Log.d(TAG, "DATABASE ERROR => ${it.message}")
                                                    }
                                        }
                                        .addOnFailureListener {
                                            holder.acceptBtn.isEnabled = true
                                            holder.denyBtn.isEnabled = true
                                            Log.d(TAG, "DATABASE ERROR => ${it.message}")
                                        }
                            }
                            .addOnFailureListener {
                                holder.acceptBtn.isEnabled = true
                                holder.denyBtn.isEnabled = true
                                Log.d(TAG, "DATABASE ERROR => ${it.message}")
                            }
                }

                holder.denyBtn.setOnClickListener {
                    holder.acceptBtn.isEnabled = false
                    holder.denyBtn.isEnabled = false
                    //update request status to accepted
                    mDatabase.collection("Request")
                            .document(model.id)
                            .update("status", Request.DENIED)
                            .addOnFailureListener {
                                holder.acceptBtn.isEnabled = true
                                holder.denyBtn.isEnabled = true
                                Log.d(TAG, "DATABASE ERROR => ${it.message}")
                            }
                }
            }
        }

        mRecyclerView.adapter = adapter
        adapter.startListening()
    }


    class RequestViewHolder(view: View, var viewType: Int? = 0): RecyclerView.ViewHolder(view) {
        var groupImage: ImageView = view.findViewById(R.id.grouplist_item_image)
        var groupTitle: TextView = view.findViewById(R.id.grouplist_item_title)
        var groupDescription: TextView = view.findViewById(R.id.grouplist_item_description)
        var acceptBtn = view.findViewById<Button>(R.id.acceptBtn)
        var denyBtn = view.findViewById<Button>(R.id.denyBtn)
    }




}
