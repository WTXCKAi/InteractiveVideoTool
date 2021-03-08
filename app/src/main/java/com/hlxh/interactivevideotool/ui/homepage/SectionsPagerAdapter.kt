package com.hlxh.interactivevideotool.ui.homepage

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hlxh.interactivevideotool.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        lateinit var fragment: Fragment
        when(position) {
            0 -> fragment = AllSceneFrag()
            1 -> fragment = VictimSceneFrag()
            2 -> {
                fragment = HackerSceneFrag()
                val mainActivity = fragment.activity
                Log.d("tab", "getActivity = $mainActivity")
            }
        }
        //return PlaceholderFragment.newInstance(position + 1)
        return fragment
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return TAB_TITLES.size
    }
}