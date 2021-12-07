package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentChatDetailsBinding
import com.example.chatapp.databinding.FragmentSingleChatBinding
import com.example.chatapp.databinding.FragmentUserDetailsBinding
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory

class ChatDetailsFragment : Fragment() {

    private lateinit var binding: FragmentChatDetailsBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        binding = FragmentChatDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        binding.backButton.setOnClickListener {
            sharedViewModel.setGotoHomePageStatus(true)
        }
        return view
    }
}