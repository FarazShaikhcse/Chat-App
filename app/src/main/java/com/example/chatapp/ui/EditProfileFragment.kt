package com.example.chatapp.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.FragmentEditProfileBinding
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.viewmodel.UserDetailsViewModel
import com.example.chatapp.viewmodel.UserDetailsViewModelFactory
import com.example.chatapp.wrapper.User


class EditProfileFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var binding: FragmentEditProfileBinding
    lateinit var getImage: ActivityResultLauncher<String>
    var uri: Uri? = null

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(
            requireActivity(),
            UserDetailsViewModelFactory()
        )[UserDetailsViewModel::class.java]
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
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
        clickListeners()
        userDetailsViewModel.getProfilePic()
        userDetailsViewModel.readUserDetails()
        observeData()
        return view
    }

    private fun clickListeners() {
        binding.profileImage.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.profileButton.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.saveBtn.setOnClickListener {
            userDetailsViewModel.addUserDetails(
                User(
                    binding.username.editText?.text.toString(),
                    binding.about.editText?.text.toString(),
                    AuthenticationService.getUserID().toString()
                )
            )
        }
    }

    private fun observeData() {

        userDetailsViewModel.userDetailAddedStatus.observe(viewLifecycleOwner) {
            if (it)
                sharedViewModel.setGotoHomePageStatus(true)
            else
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
        }

        userDetailsViewModel.userPFPfetchedStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                SharedPref.addString("uri", it.toString())
                Glide.with(requireContext())
                    .load(it)
                    .into(binding.profileImage)
                userDetailsViewModel.updatePfpUri(it)
            }

        }
        userDetailsViewModel.userDetailFetchedStatus.observe(viewLifecycleOwner) {
            binding.username.editText?.setText(it.userName)
            binding.about.editText?.setText(it.about)
        }
    }

}