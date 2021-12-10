package com.example.chatapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter: FragmentPagerAdapter {
    private val fragmentlist = ArrayList<Fragment>()
    private val fragmentTitlelist = ArrayList<String>()

    constructor(fm: FragmentManager, behavior: Int) : super(fm, behavior)

    override fun getCount(): Int {
        return fragmentlist.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentlist[position]
    }
    fun  addFragment(fragment: Fragment,title: String){
        fragmentlist.add(fragment)
        fragmentTitlelist.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitlelist.get(position)
    }
}