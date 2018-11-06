package jp.ac.ecc.sk3a12.ikouka

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.res.TypedArrayUtils.getString

class MainPagerAdapder(fm: FragmentManager?) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                val groupsFragment: GroupsFragment = GroupsFragment()
                return groupsFragment
            }

            1 -> {
                val requestFragment: RequestFragment = RequestFragment()
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
