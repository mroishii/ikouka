package jp.ac.ecc.sk3a12.ikouka.Adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupCalendarFragment
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupDashboardFragment

class GroupPagerAdapter(var fm: FragmentManager?, var groupId: String) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                val groupDashboardFragment: GroupDashboardFragment = GroupDashboardFragment()
                var args = Bundle()
                args.putString("groupId", groupId)
                groupDashboardFragment.arguments = args
                return groupDashboardFragment
            }

            1 -> {
                val groupCalendarFragment: GroupCalendarFragment = GroupCalendarFragment()
                var args = Bundle()
                args.putString("groupId", groupId)
                groupCalendarFragment.arguments = args
                return groupCalendarFragment

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
                return "メニュー"
            }
            1 -> {
                return "カレンダー"
            }
        }
        return ""
    }
}