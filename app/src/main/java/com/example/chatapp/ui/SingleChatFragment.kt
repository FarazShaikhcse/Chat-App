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
import com.example.chatapp.util.ChatAdapter
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
        adapter = ChatAdapter(userList)
        recyclerView = view.findViewById(R.id.chatRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                saveClickedChatDetails(position)
                val args = Bundle()
                args.putSerializable("clicked_chat", userList[position])
                val newFragment = ChatDetailsFragment()
                newFragment.arguments = args
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, newFragment)
                    addToBackStack(null)
                    commit()
                }
//                sharedViewModel.setGoToChatDetailsPageStatus(true)
//                chatFragmentHostListener?.onChatItemClicked(position)
            }
        })
//        singleChatViewModel.getChatsFromDB(1)
        singleChatViewModel.getAllUsers()
        singleChatViewModel.userchatsFromDb.observe(viewLifecycleOwner){


//            for (i in 0 until it.size) {
//                chatList.add(it[i])
//                adapter.notifyItemInserted(i)
//            }
        }
        singleChatViewModel.readUsersFromDb.observe(viewLifecycleOwner) {
            Log.d("checkback", "entered")
            userList.clear()
            userList.addAll(it)
            adapter.notifyDataSetChanged()
        }
        return view
    }

    private fun saveClickedChatDetails(position: Int) {
//        if ( userList[position].participants[0] == SharedPref.get(Constants.FUID)) {
//            SharedPref.addString(Constants.USERID, userList[position].userId)
//        } else {
            SharedPref.addString(Constants.USERID, userList[position].userId)
//        }
    }

    interface ChatFragmentHostListener {
        fun onChatItemClicked(position: Int)
    }
}