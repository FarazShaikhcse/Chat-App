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
import com.example.chatapp.databinding.FragmentEditProfileBinding
import com.example.chatapp.databinding.FragmentSingleChatBinding
import com.example.chatapp.util.Chat
import com.example.chatapp.util.ChatAdapter
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.viewmodel.SingleChatViewModel
import com.example.chatapp.viewmodel.SingleChatViewModelFactory


class SingleChatFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var singleChatViewModel: SingleChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    var chatFragmentHostListener: ChatFragmentHostListener? = null

    var chatList = mutableListOf<Chat>()
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
        adapter = ChatAdapter(chatList)
        recyclerView = view.findViewById(R.id.chatRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                saveClickedChatDetails(position)
                sharedViewModel.setGoToChatDetailsPageStatus(true)
//                chatFragmentHostListener?.onChatItemClicked(position)
            }
        })
        singleChatViewModel.getChatsFromDB()
        singleChatViewModel.userchatsFromDb.observe(viewLifecycleOwner){
            adapter.notifyItemRangeRemoved(0, chatList.size-1)
            chatList.clear()
            chatList.addAll(it)
            Log.d("checkback", "entered")
            adapter.notifyItemRangeInserted(0, chatList.size-1)
//            for (i in 0 until it.size) {
//                chatList.add(it[i])
//                adapter.notifyItemInserted(i)
//            }
        }
        return view
    }

    private fun saveClickedChatDetails(position: Int) {
        if ( chatList[position].participants[0] == SharedPref.get(Constants.FUID)) {
            SharedPref.addString(Constants.USERID, chatList[position].participants[1])
        } else {
            SharedPref.addString(Constants.USERID, chatList[position].participants[0])
        }
    }

    interface ChatFragmentHostListener {
        fun onChatItemClicked(position: Int)
    }
}