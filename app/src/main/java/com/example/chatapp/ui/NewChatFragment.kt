package com.example.chatapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.adapter.SelectChatAdapter
import com.example.chatapp.databinding.FragmentNewChatBinding
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodel.NewChatViewModel
import com.example.chatapp.viewmodel.NewChatViewModelFactory
import com.example.chatapp.wrapper.ChatUser
import com.example.chatapp.wrapper.User


class NewChatFragment : Fragment() {

    private lateinit var binding: FragmentNewChatBinding
    private lateinit var newChatViewModel: NewChatViewModel
    private lateinit var adapter: SelectChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        newChatViewModel = ViewModelProvider(
            this,
            NewChatViewModelFactory()
        )[NewChatViewModel::class.java]
        var userList = ArrayList<User>()
        binding = FragmentNewChatBinding.inflate(layoutInflater)
        val recyclerView = binding.userListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SelectChatAdapter(userList, requireContext())
        adapter.setOnItemClickListener(object : SelectChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val args = Bundle()
                val chatUser = ChatUser(userList[position].userName, userList[position].userId,
                    pfpUri = userList[position].pfpUri, msgToken = userList[position].msgToken)
                args.putSerializable("clicked_chat", chatUser)
                args.putString(Constants.CHAT_TYPE, Constants.CHATS)
                args.putString(Constants.IS_NEW_USER, "true")
                val newFragment = ChatDetailsFragment()
                newFragment.arguments = args
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, newFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        newChatViewModel.getUserListStatus.observe(viewLifecycleOwner) {
            userList.addAll(it)
            Log.d("selectchatuserFR", it.size.toString())
            adapter.notifyDataSetChanged()
        }
        binding.backButton.setOnClickListener {
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flFragment, HomeFragment())
                    .commit()
            }
        }
        newChatViewModel.getUserListFromDb()
        return binding.root
    }


}