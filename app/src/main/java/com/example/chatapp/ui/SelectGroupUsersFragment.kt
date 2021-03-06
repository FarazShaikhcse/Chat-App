package com.example.chatapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapter.UserListAdapter
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodel.CreateGroupViewModel
import com.example.chatapp.viewmodel.CreateGroupViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.InternalCoroutinesApi

class SelectGroupUsersFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: UserListAdapter
    lateinit var fab: FloatingActionButton
    lateinit var createGroupViewModel: CreateGroupViewModel

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_group_users, container, false)
        fab = view.findViewById(R.id.seletUserFab)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        createGroupViewModel = ViewModelProvider(
            this,
            CreateGroupViewModelFactory()
        )[CreateGroupViewModel::class.java]
        recyclerView = view.findViewById(R.id.user_list_recycler_view)
        adapter = UserListAdapter(createGroupViewModel.userList, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        createGroupViewModel.getUserListFromDb()
        fab.setOnClickListener {
            gotoSettingGrpNamePage()
        }
        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flFragment, HomeFragment())
                    .commit()
            }
        }
        observe()
        return view
    }

    private fun gotoSettingGrpNamePage() {
        val selectedIdList = adapter.getSelectedUserIdList()
        if (selectedIdList.size != 0) {
            val bundle = Bundle()
            bundle.putStringArrayList(Constants.PARTICIPANTS, selectedIdList as ArrayList<String>)
            val grpNameFragment = SetGroupNameFragment()
            grpNameFragment.arguments = bundle
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flFragment, grpNameFragment)
                    .commit()
            }
        }
    }

    private fun observe() {
        createGroupViewModel.getUserListStatus.observe(viewLifecycleOwner) {
            if (it) {
                adapter.notifyDataSetChanged()
            }
        }
    }
}