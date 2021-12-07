package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


class OtpLoginPageFragment : Fragment() {


    lateinit var auth: FirebaseAuth
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.fragment_otp_login_page, container, false)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        // fill otp and call the on click on button
        view.findViewById<Button>(R.id.otpsubmitBtn).setOnClickListener {
            val otp = view.findViewById<EditText>(R.id.otp).text.trim().toString()
            if (otp.isNotEmpty()) {

                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    SharedPref.get("storedVerificationId").toString(), otp
                )
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if(task.result?.additionalUserInfo?.isNewUser == true)
                        sharedViewModel.setGotoUserDetailsPageStatus(true)
                    else
                        sharedViewModel.setGotoHomePageStatus(true)
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}