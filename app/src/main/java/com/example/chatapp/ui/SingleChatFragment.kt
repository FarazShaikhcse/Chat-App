package com.example.chatapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapter.ChatAdapter
import com.example.chatapp.databinding.FragmentSingleChatBinding
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodel.SharedViewModel
import com.example.chatapp.viewmodel.SharedViewModelFactory
import com.example.chatapp.viewmodel.SingleChatViewModel
import com.example.chatapp.viewmodel.SingleChatViewModelFactory
import com.example.chatapp.wrapper.ChatUser
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SingleChatFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var singleChatViewModel: SingleChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentSingleChatBinding

    var userList = mutableListOf<ChatUser?>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        singleChatViewModel = ViewModelProvider(
            requireActivity(),
            SingleChatViewModelFactory()
        )[SingleChatViewModel::class.java]
        binding = FragmentSingleChatBinding.inflate(layoutInflater)
        val view = binding.root
        binding.progressBar.visibility = View.VISIBLE
        adapter = ChatAdapter(userList, requireContext())
        recyclerView = view.findViewById(R.id.chatRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val args = Bundle()
                args.putString(Constants.CHAT_TYPE, Constants.CHATS)
                args.putSerializable("clicked_chat", userList[position])
                val newFragment = ChatDetailsFragment()
                newFragment.arguments = args
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, newFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        })
        view.findViewById<FloatingActionButton>(R.id.newChatButton).setOnClickListener {
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flFragment, NewChatFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        singleChatViewModel.userchatsFromDb.observe(viewLifecycleOwner) {
            Log.d("checkback", "entered")
            userList.clear()
            userList.addAll(it)
            adapter.notifyDataSetChanged()
            binding.progressBar.visibility = View.GONE
        }
        return view
    }
    override fun onResume() {
        super.onResume()
        singleChatViewModel.getChatsFromDB(1)
    }
}