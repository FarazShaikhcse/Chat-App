package com.example.chatapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
    var uri: Uri? = null
    lateinit var getImage: ActivityResultLauncher<String>

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
        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if (it != null) {
                    uri = it
                    binding.profileImage.setImageURI(it)
                    userDetailsViewModel.uploadProfilePic(it)
                }
            }
        )
        binding.profileImage.setOnClickListener {
            getImage.launch("image/*")
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