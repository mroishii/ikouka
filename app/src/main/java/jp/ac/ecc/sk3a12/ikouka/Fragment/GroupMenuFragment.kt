package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.ac.ecc.sk3a12.ikouka.Adapter.GroupMenuListAdapter
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.R
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupMenuFragment : Fragment() {
    private var groupId = ""

    private var mAuth = FirebaseAuth.getInstance()
    private var mDb = FirebaseFirestore.getInstance()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupId = arguments!!.getString("groupId")

        val menuList: ArrayList<String> = ArrayList<String>(Arrays.asList("メンバー", "投票・アンケート", "タスク", "チャット"))
        val menuIcon: ArrayList<Int> = ArrayList<Int>(Arrays.asList(R.drawable.menu_members, R.drawable.menu_vote, R.drawable.menu_tasks, R.drawable.menu_chat))

        val groupMenuList: ListView = view.findViewById(R.id.groupMenuList)
        val adapter = GroupMenuListAdapter(menuList, menuIcon, groupId, context)
        groupMenuList.adapter = adapter

        val groupImage: ImageView = view.findViewById(R.id.groupMenuImage)
        mDb.collection("Groups")
                .document(groupId)
                .get()
                .addOnSuccessListener {
                    this!!.context?.let { it1 ->
                        Glide.with(it1)
                                .load(it.getString("image"))
                                .into(groupImage)
                    }
                }

    }


}
