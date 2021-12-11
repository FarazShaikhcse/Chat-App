package com.example.chatapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
            if (selectedChat.pfpUri != "") {
                context?.let {
                    Glide.with(it)
                        .load(Uri.parse(selectedChat.pfpUri))
                        .into(binding.profileImage)
                }
            }
        } else if (bundle.getString(Constants.CHAT_TYPE) == Constants.GROUPS) {
            val selectedChat: GroupChat = bundle.getSerializable("clicked_chat") as GroupChat
            chatDetailViewModel.getGroupMessages(selectedChat.groupId)
            binding.usernameTV.text = selectedChat.groupName
            if (selectedChat.pfpUri != "") {
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

        chatDetailViewModel.imageUploadedStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
                    val selectedChat: User = bundle.getSerializable("clicked_chat") as User
                    chatDetailViewModel.sendMsgToUser(
                        it.toString(),
                        selectedChat.userId,
                        Constants.IMAGE
                    )
                } else {
                    val selectedChat: GroupChat =
                        bundle.getSerializable("clicked_chat") as GroupChat
                    chatDetailViewModel.sendMsgToGroup(
                        selectedChat.groupId,
                        it.toString(),
                        Constants.IMAGE
                    )
                }
            }
        }
    }

    private fun clickListeners() {
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, HomeFragment())
                commit()
            }
        }
        binding.sendMsgBtn.setOnClickListener {
            val text = binding.msgeditText.text.toString()
            if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
                val selectedChat: User = bundle.getSerializable("clicked_chat") as User
                chatDetailViewModel.sendMsgToUser(text, selectedChat.userId, Constants.TEXT)
            } else {
                val selectedChat: GroupChat = bundle.getSerializable("clicked_chat") as GroupChat
                chatDetailViewModel.sendMsgToGroup(selectedChat.groupId, text, Constants.TEXT)
            }
        }
        binding.sendImageBtn.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                Constants.RC_SELECT_IMAGE
            )

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("image", "Inside onActivityresult")
        if (requestCode == Constants.RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            chatDetailViewModel.uploadImageToStorage(selectedImagePath)
        }
    }
}