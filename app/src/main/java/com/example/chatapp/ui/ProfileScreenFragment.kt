package com.example.chatapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.FragmentProfileScreenBinding
import com.example.chatapp.util.Constants
import com.example.chatapp.wrapper.ChatUser
import com.example.chatapp.wrapper.GroupChat


class ProfileScreenFragment : Fragment() {

    private lateinit var binding: FragmentProfileScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileScreenBinding.inflate(layoutInflater)
        val view = binding.root
        binding.backToChatBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        if (arguments?.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
            val selectedChat: ChatUser = arguments?.getSerializable("chat") as ChatUser
            Glide.with(view)
                .load(Uri.parse(selectedChat.pfpUri))
                .into(binding.pfpImage)
            binding.usernameTV.text = selectedChat.userName

        } else {
            val selectedChat: GroupChat =
                arguments?.getSerializable("chat") as GroupChat
            Glide.with(view)
                .load(Uri.parse(selectedChat.pfpUri))
                .into(binding.pfpImage)
            binding.usernameTV.text = selectedChat.groupName
        }
        return view
    }

}