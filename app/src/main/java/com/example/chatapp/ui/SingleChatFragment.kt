package com.example.chatapp.ui

import android.os.Bundle
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


class SingleChatFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView

    var chatList = mutableListOf<Chat>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_single_chat, container, false)
        adapter = ChatAdapter(chatList)
        recyclerView = view.findViewById(R.id.chatRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                saveClickedChatDetails(position)
                sharedViewModel.setGoToChatDetailsPageStatus(true)
            }
        })
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        sharedViewModel.getChatsFromDB()
        sharedViewModel.userchatsFromDb.observe(viewLifecycleOwner){
            for (i in 0 until it.size) {
                chatList.add(it[i])
                adapter.notifyItemInserted(i)
            }
        }
        return view
    }

    private fun saveClickedChatDetails(position: Int) {
        SharedPref.addString(Constants.CHAT_POSITION, position.toString())
    }


}