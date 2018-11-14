package jp.ac.ecc.sk3a12.ikouka

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class GroupPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                val groupDashboardFragment: GroupDashboardFragment = GroupDashboardFragment()
                return groupDashboardFragment
            }

            1 -> {
                val groupNotificationFragment: GroupNotificationFragment = GroupNotificationFragment()
                return groupNotificationFragment
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
                return "掲示板"
            }
            1 -> {
                return "通知"
            }
        }
        return ""
    }
}