package net.muliba.fancyfilepickerlibrary.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by fancylou on 10/23/17.
 */


class FilePickerViewPagerAdapter(fm: FragmentManager, val tabs:Array<String>, val fragments:ArrayList<Fragment>) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment?  = if(position < fragments.size)
    {
        fragments[position]
    }else {
        null
    }

    override fun getCount(): Int  = tabs.size

    override fun getPageTitle(position: Int): CharSequence = if (position < tabs.size)
    {
        tabs[position]
    }else{
        ""
    }

}