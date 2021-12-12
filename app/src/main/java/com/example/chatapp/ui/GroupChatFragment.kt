package com.example.chatapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapter.GroupChatAdapter
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodel.GroupChatViewModel
import com.example.chatapp.viewmodel.GroupChatViewModelFactory
import com.example.chatapp.wrapper.GroupChat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class GroupChatFragment : Fragment() {

    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var adapter: GroupChatAdapter
    private lateinit var recyclerView: RecyclerView
    var groupList = mutableListOf<GroupChat>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_chat, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        groupChatViewModel = ViewModelProvider(
            requireActivity(),
            GroupChatViewModelFactory()
        )[GroupChatViewModel::class.java]
        groupChatViewModel.getGroupsFromDb()
        adapter = GroupChatAdapter(groupList, requireContext())
        recyclerView = view.findViewById(R.id.chatRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : GroupChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val args = Bundle()
                args.putSerializable("clicked_chat", groupList[position])
                args.putString(Constants.CHAT_TYPE, Constants.GROUPS)
                val newFragment = ChatDetailsFragment()
                newFragment.arguments = args
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, newFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        })
        view.findViewById<FloatingActionButton>(R.id.createGroupFB).setOnClickListener {
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flFragment, SelectGroupUsersFragment())
                    .commit()
            }
        }
        groupChatViewModel.readGroupsFromDb.observe(viewLifecycleOwner) {
            groupList.clear()
            groupList.addAll(it)
            adapter.notifyDataSetChanged()
        }
        return view
    }

}