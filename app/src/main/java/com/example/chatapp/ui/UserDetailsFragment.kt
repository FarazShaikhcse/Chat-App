package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentUserDetailsBinding
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.viewmodel.UserDetailsViewModel
import com.example.chatapp.viewmodel.UserDetailsViewModelFactory
import com.example.chatapp.wrapper.User


class UserDetailsFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var binding: FragmentUserDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(
            requireActivity(),
            UserDetailsViewModelFactory()
        )[UserDetailsViewModel::class.java]
        binding = FragmentUserDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        binding.saveBtn.setOnClickListener {
            userDetailsViewModel.addUserDetails(User(
                binding.username.editText?.text.toString(),
            binding.about.editText?.text.toString(), AuthenticationService.getUserID().toString()))
        }
        userDetailsViewModel.userDetailAddedStatus.observe(viewLifecycleOwner){
            if(it)
                sharedViewModel.setGotoHomePageStatus(true)
            else
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
        }
        return view
    }


}