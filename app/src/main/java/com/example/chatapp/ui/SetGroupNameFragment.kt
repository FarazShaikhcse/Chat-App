package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodel.SetGroupViewModel
import com.example.chatapp.viewmodel.SetGroupViewModelFactory
import com.example.chatapp.viewmodel.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SetGroupNameFragment : Fragment() {
    lateinit var saveGrpfab: FloatingActionButton
    lateinit var grpName: EditText
    lateinit var sharedViewModel: SharedViewModel
    lateinit var grpNameVM: SetGroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_group_name, container, false)
        grpNameVM =
            ViewModelProvider(this, SetGroupViewModelFactory())[SetGroupViewModel::class.java]
        val selectedList = arguments?.getStringArrayList(Constants.PARTICIPANTS)
        grpName = view.findViewById(R.id.grpNameET)
        saveGrpfab = view.findViewById(R.id.saveGrpFAB)
        saveGrpfab.setOnClickListener {
            if(grpName.text.isNotEmpty() && selectedList != null){
                grpNameVM.createGrp(grpName.text.toString(),selectedList)
            }
        }
        observe()
        return view
    }

    private fun observe() {
        grpNameVM.grpCreatedStatus.observe(viewLifecycleOwner){
            if(it){
                activity?.run {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment,HomeFragment())
                        .commit()
                }
            }
        }
    }


}