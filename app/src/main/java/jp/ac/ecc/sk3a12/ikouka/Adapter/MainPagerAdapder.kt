package jp.ac.ecc.sk3a12.ikouka.Adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupsListFragment
import jp.ac.ecc.sk3a12.ikouka.Fragment.RequestFragment
import jp.ac.ecc.sk3a12.ikouka.Model.User

class MainPagerAdapder(fm: FragmentManager?, var currentUser: User) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                val groupsListFragment: GroupsListFragment = GroupsListFragment()
                val args = Bundle()
                args.putParcelable("currentUser", currentUser)
                groupsListFragment.arguments = args
                return groupsListFragment
            }

            1 -> {
                val requestFragment: RequestFragment = RequestFragment()
                val args = Bundle()
                args.putParcelable("currentUser", currentUser)
                requestFragment.arguments = args
                return requestFragment
            }

            else -> {
                val nullFragment: Fragment = Fragment()
                return nullFragment
            }
        }


    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int) : CharSequence {
        when (position) {
            0 -> {
                return "グループ"
            }
            1 -> {
                return "リクエスト"
            }
        }
        return ""
    }

}
