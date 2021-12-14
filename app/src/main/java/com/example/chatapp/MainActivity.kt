package com.example.chatapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.ui.*
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.viewmodel.UserDetailsViewModel
import com.example.chatapp.viewmodel.UserDetailsViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(
            this,
            UserDetailsViewModelFactory()
        )[UserDetailsViewModel::class.java]
        observeNavigation()
        SharedPref.initSharedPref(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        userDetailsViewModel.readUserDetails()
        sharedViewModel.setGotoSplashScreen(true)
    }

    private fun observeNavigation() {
        userDetailsViewModel.userDetailFetchedStatus.observe(this@MainActivity) {
            SharedPref.addString(Constants.USERNAME, it.userName)
            SharedPref.addString(Constants.USER_PFP, it.pfpUri)
        }
        sharedViewModel.gotoHomePageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePageWithoutBackStack(HomeFragment())
            }
        })

        sharedViewModel.gotoOTPPageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePageWithBackStack(OtpLoginPageFragment())
            }
        })

        sharedViewModel.gotoWelcomePageStatus.observe(this@MainActivity,
            {
                if (it) {
                    navigatePageWithBackStack(WelcomePageFragment())
                }
            })

        sharedViewModel.gotoUserDetailsPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    navigatePageWithBackStack(UserDetailsFragment())
                }
            })

        sharedViewModel.gotoEditProfilePageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePageWithBackStack(EditProfileFragment())
            }
        })

        sharedViewModel.gotoChatDetailsPageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePageWithBackStack(ChatDetailsFragment())
            }
        })

        sharedViewModel.gotoSplashPageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePageWithBackStack(SplashScreenFragment())
            }
        })
    }

    fun navigatePageWithBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            addToBackStack(null)
            commit()
        }
    }

    fun navigatePageWithoutBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbarmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                sharedViewModel.setGotoEditProfilePageStatus(true)
                true
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                sharedViewModel.setGoToWelcomePageStatus(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}