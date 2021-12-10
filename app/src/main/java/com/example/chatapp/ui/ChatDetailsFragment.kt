package com.example.chatapp.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentChatDetailsBinding
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.wrapper.Chat


import com.example.chatapp.util.CustomAdapter
import com.example.chatapp.wrapper.GroupChat
import com.example.chatapp.wrapper.Message
import com.example.chatapp.wrapper.User


class ChatDetailsFragment : Fragment() {

    private lateinit var binding: FragmentChatDetailsBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var chatDetailViewModel: ChatDetailViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var bundle: Bundle
    private lateinit var adapter: CustomAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        chatDetailViewModel = ViewModelProvider(
            requireActivity(),
            ChatDetailViewModelFactory()
        )[ChatDetailViewModel::class.java]
        binding = FragmentChatDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        recyclerView = binding.chatsRV
//        SharedPref.get(Constants.USERID)?.let { chatDetailViewModel.getChatsFromDB(it, 999) }
        bundle = requireArguments()
        bundle.getString(Constants.CHAT_TYPE)?.let { SharedPref.addString(Constants.CHAT_TYPE, it) }
        if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
            val selectedChat: User = bundle.getSerializable("clicked_chat") as User
            chatDetailViewModel.updateMessages(selectedChat.userId)
            binding.usernameTV.text = selectedChat.userName
            if(selectedChat.pfpUri != "") {
                context?.let {
                    Glide.with(it)
                        .load(Uri.parse(selectedChat.pfpUri))
                        .into(binding.profileImage)
                }
            }
        }
        else if (bundle.getString(Constants.CHAT_TYPE) == Constants.GROUPS) {
            val selectedChat: GroupChat = bundle.getSerializable("clicked_chat") as GroupChat
            chatDetailViewModel.getGroupMessages(selectedChat.groupId)
            binding.usernameTV.text = selectedChat.groupName
            if(selectedChat.pfpUri != "") {
                context?.let {
                    Glide.with(it)
                        .load(Uri.parse(selectedChat.pfpUri))
                        .into(binding.profileImage)
                }
            }
        }
//        binding.msgeditText.setOnClickListener {
//            recyclerView.scrollToPosition(adapter.itemCount - 1)
//        }
        observeData()
        clickListeners()
        return view
    }

    private fun observeData() {

        chatDetailViewModel.userchatsFromDb.observe(viewLifecycleOwner) {
            val messageList = it
            adapter = CustomAdapter(requireContext(), messageList as ArrayList<Message>)
            val linearLayout = LinearLayoutManager(requireContext())
            linearLayout.reverseLayout = true
            recyclerView.layoutManager = linearLayout
            recyclerView.adapter = adapter
//            recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
        chatDetailViewModel.messageSentStatus.observe(viewLifecycleOwner) {
            binding.msgeditText.setText("")
        }
    }

    private fun clickListeners() {
        binding.backButton.setOnClickListener {
            SharedPref.addString(Constants.USERID, "")
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, HomeFragment())
                commit()
            }
        }
        binding.sendMsgBtn.setOnClickListener {
            val text = binding.msgeditText.text.toString()
            if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
                val selectedChat: User = bundle.getSerializable("clicked_chat") as User
                chatDetailViewModel.sendMsgToUser(text, selectedChat.userId)
            } else {
                val selectedChat: GroupChat = bundle.getSerializable("clicked_chat") as GroupChat
                chatDetailViewModel.sendMsgToGroup(selectedChat.groupId, text)
            }
        }
    }
}