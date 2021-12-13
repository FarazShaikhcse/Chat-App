package com.example.chatapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.viewmodel.SendOTPViewModel
import com.example.chatapp.viewmodel.SendOTPViewModelFactory
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory

class WelcomePageFragment : Fragment() {

    var number: String = ""
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var sendOTPViewModel: SendOTPViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome_page, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        sendOTPViewModel = ViewModelProvider(
            requireActivity(),
            SendOTPViewModelFactory()
        )[SendOTPViewModel::class.java]
        view.findViewById<Button>(R.id.submitBtn).setOnClickListener {
            login()
        }
        sendOTPViewModel.otpSentStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGoToOTPageStatus(true)
            }
        }
        return view
    }

    private fun login() {
        number = view?.findViewById<EditText>(R.id.phoneNumber)?.text?.trim().toString()
        if (number.isNotEmpty()) {
            number = "+91$number"
            sendOTPViewModel.sendVerificationCode(number, requireActivity())
        } else {
            Toast.makeText(requireContext(), "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }
}