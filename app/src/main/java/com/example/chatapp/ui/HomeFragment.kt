package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.util.Chat
import com.example.chatapp.util.ChatAdapter
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    var chatList = mutableListOf<Chat>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.navMenu)
        navBar.isVisible = true
        adapter = ChatAdapter(chatList)
        recyclerView = view.findViewById(R.id.chatRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        sharedViewModel.getChatsFromDB()
        sharedViewModel.userchatsFromDb.observe(viewLifecycleOwner){
            for (i in 0 until it.size) {
                chatList.add(it[i])
                adapter.notifyItemInserted(i)
            }
        }
        return view
    }
}