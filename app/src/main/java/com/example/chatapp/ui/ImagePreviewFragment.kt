package com.example.chatapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.FragmentImagePreviewBinding
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodel.ChatDetailViewModel
import com.example.chatapp.viewmodel.ChatDetailViewModelFactory
import com.example.chatapp.viewmodel.ImageViewModel
import com.example.chatapp.viewmodel.ImageViewModelFactory
import com.example.chatapp.wrapper.ChatUser
import com.example.chatapp.wrapper.GroupChat


class ImagePreviewFragment : Fragment() {

    private lateinit var binding: FragmentImagePreviewBinding
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var chatDetailViewModel: ChatDetailViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImagePreviewBinding.inflate(layoutInflater)
        val view = binding.root
        val uri = arguments?.get(Constants.IMAGE_URI) as String
        uri?.let {
            Glide.with(view)
                .load(Uri.parse(it))
                .into(binding.preview)
        }
        imageViewModel = ViewModelProvider(
            requireActivity(),
            ImageViewModelFactory()
        )[ImageViewModel::class.java]
        chatDetailViewModel = ViewModelProvider(
            requireActivity(),
            ChatDetailViewModelFactory()
        )[ChatDetailViewModel::class.java]
        binding.imageSentPB.visibility = View.GONE
        binding.sendImageFAB.setOnClickListener {
            imageViewModel.uploadImageToStorage(Uri.parse(uri))
            binding.imageSentPB.visibility = View.VISIBLE
        }
        binding.closePreview.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        imageViewModel.imageUploadedStatus.observe(viewLifecycleOwner) {
            if (arguments?.getString(Constants.CHAT_TYPE) == Constants.CHATS) {
                val selectedChat: ChatUser = arguments?.getSerializable("chat") as ChatUser
                chatDetailViewModel.sendMsgToUser(
                    it.toString(),
                    selectedChat.userId,
                    Constants.IMAGE,
                    selectedChat.msgToken
                )
            } else {
                val selectedChat: GroupChat =
                    arguments?.getSerializable("chat") as GroupChat
                val tokenList = arguments?.getStringArrayList(Constants.TOKEN)
                chatDetailViewModel.sendMsgToGroup(
                    selectedChat.groupId,
                    it.toString(),
                    Constants.IMAGE,
                    tokenList!!.toList()
                )
            }
            binding.imageSentPB.visibility = View.GONE
        }
        chatDetailViewModel.messageSentStatus.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return view
    }


}