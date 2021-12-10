package com.example.chatapp.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.wrapper.User
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(
    private val userList: ArrayList<User>,
    private val context: Context
) : RecyclerView.Adapter<UserListAdapter.GroupChatUserViewHolder>() {

    var selectedUser = mutableListOf<String>()

    class GroupChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_list_layout,
            parent, false
        )
        return GroupChatUserViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: GroupChatUserViewHolder, position: Int) {
        val userName = holder.itemView.findViewById<TextView>(R.id.group_userName)
        val profileImage =
            holder.itemView.findViewById<CircleImageView>(R.id.group_users_pfp)
        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.select_user_cb)

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.d("checkeduserid", userList[position].userId)
                selectedUser.add(userList[position].userId)
            } else if (!isChecked) {
                selectedUser.remove(userList[position].userId)
            }
        }
        holder.itemView.apply {
            userName.text = userList[position].userName
            if (userList[position].pfpUri != "") {
                Glide.with(context).load(Uri.parse(userList[position].pfpUri)).dontAnimate()
                    .into(profileImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun getSelectedList(): MutableList<String> {
        return selectedUser
    }
}