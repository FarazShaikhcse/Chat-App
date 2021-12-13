package com.example.chatapp.ui


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentChatDetailsBinding
import com.example.chatapp.util.Constants
import com.example.chatapp.util.CustomAdapter
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.ChatDetailViewModel
import com.example.chatapp.viewmodel.ChatDetailViewModelFactory
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.wrapper.ChatUser
import com.example.chatapp.wrapper.GroupChat
import com.example.chatapp.wrapper.Message
import java.util.function.LongFunction


class ChatDetailsFragment : Fragment() {

    private lateinit var binding: FragmentChatDetailsBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var chatDetailViewModel: ChatDetailViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var bundle: Bundle
    private lateinit var adapter: CustomAdapter
    var tokenList = emptyList<String>()
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
        bundle = requireArguments()
        bundle.getString(Constants.CHAT_TYPE)?.let { SharedPref.addString(Constants.CHAT_TYPE, it) }
        if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
            val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
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
            chatDetailViewModel.getToken(selectedChat.participants)
            binding.usernameTV.text = selectedChat.groupName
            if (selectedChat.pfpUri != "") {
                context?.let {
                    Glide.with(it)
                        .load(Uri.parse(selectedChat.pfpUri))
                        .into(binding.profileImage)
                }
            }
        }
        if (bundle.getString(Constants.IS_NEW_USER) == "true") {
            val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
            chatDetailViewModel.addNewUserChat(selectedChat)
        }
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
        }
        chatDetailViewModel.messageSentStatus.observe(viewLifecycleOwner) {
            binding.msgeditText.setText("")
        }

        chatDetailViewModel.imageUploadedStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
                    val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
                    chatDetailViewModel.sendMsgToUser(
                        it.toString(),
                        selectedChat.userId,
                        Constants.IMAGE,
                        selectedChat.msgToken
                    )
                } else {
                    val selectedChat: GroupChat =
                        bundle.getSerializable("clicked_chat") as GroupChat
                    chatDetailViewModel.sendMsgToGroup(
                        selectedChat.groupId,
                        it.toString(),
                        Constants.IMAGE,
                        tokenList
                    )
                }
            }
        }
        chatDetailViewModel.chatCreatedStatus.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("chatregisterstatus", it.toString())
            }
        }
        chatDetailViewModel.groupMembersTokens.observe(viewLifecycleOwner) {
            tokenList = it
            Log.d("TokenList", "size of list" + it.size.toString())
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
                val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
                chatDetailViewModel.sendMsgToUser(text, selectedChat.userId,
                    Constants.TEXT, selectedChat.msgToken)
            } else {
                val selectedChat: GroupChat = bundle.getSerializable("clicked_chat") as GroupChat
                chatDetailViewModel.sendMsgToGroup(selectedChat.groupId, text, Constants.TEXT, tokenList)
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