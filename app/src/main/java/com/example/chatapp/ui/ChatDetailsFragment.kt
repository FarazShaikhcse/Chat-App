package com.example.chatapp.ui


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
import androidx.fragment.app.FragmentActivity





class ChatDetailsFragment : Fragment() {

    private lateinit var binding: FragmentChatDetailsBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var chatDetailViewModel: ChatDetailViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var bundle: Bundle
    private lateinit var adapter: CustomAdapter
    private var offset = Long.MAX_VALUE
    var list = mutableListOf<Message>()
    var isLoading = false
    var currentItem: Int = 0
    var totalItem: Int = 0
    var scrolledOutItems: Int = 0
    var messagesDocid = ""
    var convType = ""

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
        adapter = CustomAdapter(requireContext(), list as ArrayList<Message>)
        val linearLayout = LinearLayoutManager(requireContext())
        linearLayout.reverseLayout = true
        recyclerView.layoutManager = linearLayout
        recyclerView.adapter = adapter
        bundle = requireArguments()
        bundle.getString(Constants.CHAT_TYPE)?.let { SharedPref.addString(Constants.CHAT_TYPE, it) }
        if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
            val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
            messagesDocid = selectedChat.userId
            convType = Constants.CHATS
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
            convType = Constants.GROUPS
            messagesDocid = selectedChat.groupId
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
        recyclerViewScrollListener()
        return view
    }

    private fun observeData() {

        chatDetailViewModel.userchatsFromDb.observe(viewLifecycleOwner) {
            isLoading = false
            Log.d("pagination", it.size.toString())
            Log.d("inside single chat", "{$it}")
            if (it.size != 0) {
                for (i in it) {
                    Log.d("pagination", i.toString())
                    offset = i.sentTime
                    list.add(i)
                    adapter.notifyItemInserted(list.size - 1)
                }
            } else {
                offset = 0L
            }
        }
        chatDetailViewModel.groupuserchatsFromDb.observe(viewLifecycleOwner) {
            isLoading = false
            Log.d("pagination", it.size.toString())
            Log.d("inside group chat", "{$it}")
            if (it.size != 0) {
                for (i in it) {
                    Log.d("pagination", i.toString())
                    offset = i.sentTime
                    list.add(i)
                    adapter.notifyItemInserted(list.size - 1)
                }
            } else {
                offset = 0L
            }
        }
        chatDetailViewModel.messageSentStatus.observe(viewLifecycleOwner) {
            binding.msgeditText.setText("")
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
        chatDetailViewModel.newChatsFromDb.observe(viewLifecycleOwner) {
            val msg = it
            Log.d("inside new chat", "$it")
            if (msg != null) {
                list.add(0, msg)
                adapter.notifyItemInserted(0)
                recyclerView.smoothScrollToPosition(0)
                offset = list[list.size - 1].sentTime
            }

        }
        chatDetailViewModel.newGroupChatsFromDb.observe(viewLifecycleOwner) {
            val msg = it
            Log.d("inside group new chat", "$it")
            if (msg != null) {
                list.add(0, msg)
                adapter.notifyItemInserted(0)
                recyclerView.smoothScrollToPosition(0)
                offset = list[list.size - 1].sentTime
            }
        }
    }

    private fun clickListeners() {
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.profileImage.setOnClickListener {
            val newbundle = Bundle()
            if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
                val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
                newbundle.putSerializable("chat", selectedChat)
            } else {
                val selectedChat: GroupChat =
                    bundle.getSerializable("clicked_chat") as GroupChat
                newbundle.putSerializable("chat", selectedChat)
            }
            newbundle.putString(Constants.CHAT_TYPE, bundle.getString(Constants.CHAT_TYPE))
            val fragment = ProfileScreenFragment()
            fragment.arguments = newbundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.sendMsgBtn.setOnClickListener {
            val text = binding.msgeditText.text.toString()
            Log.d("Sendmessagecalled", "inside")
            if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
                val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
                chatDetailViewModel.sendMsgToUser(
                    text, selectedChat.userId,
                    Constants.TEXT, selectedChat.msgToken
                )
            } else {
                val selectedChat: GroupChat = bundle.getSerializable("clicked_chat") as GroupChat
                chatDetailViewModel.sendMsgToGroup(
                    selectedChat.groupId,
                    text,
                    Constants.TEXT,
                    tokenList
                )
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

    private fun recyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItem = (recyclerView.layoutManager as LinearLayoutManager).childCount
                totalItem = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                scrolledOutItems = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()
                if (!isLoading) {
                    if ((currentItem + scrolledOutItems) == totalItem && scrolledOutItems >= 0) {
                        isLoading = true
                        if (offset != 0L) {
                            Log.d("pagination", "scrolled")
                            loadNextTenChats()
                        }
                    }
                }
            }
        })
    }

    private fun loadNextTenChats() {
        Log.d("chatsloaded", "entered")
        chatDetailViewModel.loadNextTenChats(messagesDocid, offset, convType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val selectedImagePath = data?.data
        val bundle1 = Bundle()
        bundle1.putString(Constants.IMAGE_URI, selectedImagePath.toString())
        bundle1.putString(Constants.CHAT_TYPE, bundle.getString(Constants.CHAT_TYPE))
        bundle1.putStringArrayList(Constants.TOKEN, ArrayList(tokenList))
        if (bundle.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
            val selectedChat: ChatUser = bundle.getSerializable("clicked_chat") as ChatUser
            bundle1.putSerializable("chat", selectedChat)
        } else {
            val selectedChat: GroupChat =
                bundle.getSerializable("clicked_chat") as GroupChat
            bundle1.putSerializable("chat", selectedChat)
        }

        val fragment = ImagePreviewFragment()
        fragment.arguments = bundle1
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.flFragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}