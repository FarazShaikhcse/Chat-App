package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.chatapp.R
import com.example.chatapp.util.Chat
import com.example.chatapp.util.ChatAdapter
import com.example.chatapp.util.ViewPagerAdapter
import com.example.chatapp.viewmodel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    var chatList = mutableListOf<Chat>()
    lateinit var viewPager: ViewPager
    private lateinit var tablayout: com.google.android.material.tabs.TabLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        tablayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewpager)
        tablayout.setupWithViewPager(viewPager)
        val vpadapter = ViewPagerAdapter(requireActivity().supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        vpadapter.addFragment(SingleChatFragment(), getString(R.string.chat))
        vpadapter.addFragment(GroupChatFragment(), getString(R.string.group_chat))
        viewPager.adapter = vpadapter
        return view
    }
}