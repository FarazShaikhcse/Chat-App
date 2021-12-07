package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.ui.*
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import android.view.Menu
import android.view.MenuItem


class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        observeNavigation()
        SharedPref.initSharedPref(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        val homeFragment = HomeFragment()
//        val groupChatFragment = GroupChatFragment()
//        val fm = supportFragmentManager
//        var active: Fragment = homeFragment
//        fm.beginTransaction().add(R.id.flFragment, groupChatFragment, "2").hide(groupChatFragment)
//            .commit();
////        fm.beginTransaction().add(R.id.flFragment, homeFragment, "1").commit();

    }

    private fun observeNavigation() {
        sharedViewModel.gotoHomePageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePage(HomeFragment())
            }
        })

        sharedViewModel.gotoOTPPageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePage(OtpLoginPageFragment())
            }
        })

        sharedViewModel.gotoWelcomePageStatus.observe(this@MainActivity,
            {
                if (it) {
                    navigatePage(WelcomePageFragment())
                }
            })

        sharedViewModel.gotoUserDetailsPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    navigatePage(UserDetailsFragment())
                }
            })

        sharedViewModel.gotoEditProfilePageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePage(EditProfileFragment())
            }
        })

        sharedViewModel.gotoChatDetailsPageStatus.observe(this@MainActivity, {
            if (it) {
                navigatePage(ChatDetailsFragment())
            }
        })
    }

    private fun navigatePage(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
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