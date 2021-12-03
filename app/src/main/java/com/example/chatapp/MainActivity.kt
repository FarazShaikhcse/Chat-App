package com.example.chatapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.ui.HomeFragment
import com.example.chatapp.ui.OtpLoginPageFragment
import com.example.chatapp.ui.WelcomePageFragment
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        observeNavigation()
        SharedPref.initSharedPref(this)
    }
    private fun observeNavigation() {
        sharedViewModel.gotoHomePageStatus.observe(this@MainActivity, {
            if (it) {
                gotoHomePage()
            }
        })

        sharedViewModel.gotoOTPPageStatus.observe(this@MainActivity, {
            if (it) {
                gotoOTPPage()
            }
        })

        sharedViewModel.gotoWelcomePageStatus.observe(this@MainActivity,
            {
                if (it) {
                    gotoWelcomePage()
                }
            })
    }

    private fun gotoHomePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, HomeFragment())
            commit()
        }
    }

    private fun gotoOTPPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, OtpLoginPageFragment())
            commit()
        }
    }

    private fun gotoWelcomePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, WelcomePageFragment())
            commit()
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null)
            sharedViewModel.setGotoHomePageStatus(true)
        else
            sharedViewModel.setGoToWelcomePageStatus(true)
    }
}