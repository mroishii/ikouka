package jp.ac.ecc.sk3a12.ikouka


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupNotificationFragment : Fragment() {
    private lateinit var currentGroup: Group

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentGroup = activity!!.intent.getParcelableExtra("group")

        val menuList: ArrayList<String> = ArrayList<String>(Arrays.asList("メンバー", "投票・アンケート", "タスク", "チャット"))
        val menuIcon: ArrayList<Int> = ArrayList<Int>(Arrays.asList(R.drawable.menu_members, R.drawable.menu_vote, R.drawable.menu_tasks, R.drawable.menu_chat))

        val groupMenuList: ListView = view.findViewById(R.id.groupMenuList)
        val adapter: GroupMenuListAdapter = GroupMenuListAdapter(menuList, menuIcon, currentGroup, context)
        groupMenuList.adapter = adapter
    }


}
