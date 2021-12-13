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
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.viewmodel.VerifyOTPViewModel
import com.example.chatapp.viewmodel.VerifyOTPViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


class OtpLoginPageFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var verifyOTPViewModel: VerifyOTPViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.hide()
        val view = inflater.inflate(R.layout.fragment_otp_login_page, container, false)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        verifyOTPViewModel = ViewModelProvider(
            requireActivity(),
            VerifyOTPViewModelFactory()
        )[VerifyOTPViewModel::class.java]

        // fill otp and call the on click on button
        view.findViewById<Button>(R.id.otpsubmitBtn).setOnClickListener {
            val otp = view.findViewById<EditText>(R.id.otp).text.trim().toString()
            if (otp.isNotEmpty()) {
                verifyOTPViewModel.verifyOTP(otp, requireActivity(), requireContext())
            } else {
                Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        verifyOTPViewModel.isNewUserStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoUserDetailsPageStatus(true)
            }
            else {
                sharedViewModel.setGotoHomePageStatus(true)
            }
        }
        return view
    }

}