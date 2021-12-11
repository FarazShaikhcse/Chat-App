package com.example.chatapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapter.ChatAdapter
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.viewmodel.SingleChatViewModel
import com.example.chatapp.viewmodel.SingleChatViewModelFactory
import com.example.chatapp.wrapper.User


class SingleChatFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var singleChatViewModel: SingleChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    var chatFragmentHostListener: ChatFragmentHostListener? = null

    var userList = mutableListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_single_chat, container, false)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        singleChatViewModel = ViewModelProvider(
            requireActivity(),
            SingleChatViewModelFactory()
        )[SingleChatViewModel::class.java]
        adapter = ChatAdapter(userList, requireContext())
        recyclerView = view.findViewById(R.id.chatRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val args = Bundle()
                args.putString(Constants.CHAT_TYPE, Constants.CHATS)
                args.putSerializable("clicked_chat", userList[position])
                val newFragment = ChatDetailsFragment()
                newFragment.arguments = args
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, newFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        })
        singleChatViewModel.getAllUsers()
        singleChatViewModel.readUsersFromDb.observe(viewLifecycleOwner) {
            Log.d("checkback", "entered")
            userList.clear()
            userList.addAll(it)
            adapter.notifyDataSetChanged()
        }
        return view
    }

    interface ChatFragmentHostListener {
        fun onChatItemClicked(position: Int)
    }
}