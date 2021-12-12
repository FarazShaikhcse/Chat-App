package com.example.chatapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class SplashScreenFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private  lateinit var logo: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        logo = view.findViewById(R.id.splashImage)
        logo.alpha = 0f
        logo.animate().setDuration(1500).alpha(1f).withEndAction {
            if (FirebaseAuth.getInstance().currentUser != null) {
                AuthenticationService.getUserID()?.let { SharedPref.addString(Constants.USERID, it) }
                sharedViewModel.setGotoHomePageStatus(true)
            }
            else {
                sharedViewModel.setGoToWelcomePageStatus(true)
            }
        }
        return  view
    }

}