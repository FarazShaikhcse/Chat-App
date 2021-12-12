package com.example.chatapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.wrapper.GroupChat
import com.example.chatapp.wrapper.User
import de.hdodenhof.circleimageview.CircleImageView

class SelectChatAdapter(
    private val users: ArrayList<User>,
    private val context: Context
) : RecyclerView.Adapter<SelectChatAdapter.SelectChatUserViewHolder>() {

    var selectedUserId = mutableListOf<String>()

    class SelectChatUserViewHolder(itemView: View, listener: OnItemClickListener) :
    RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var mListner: OnItemClickListener
    var userList: ArrayList<User> = ArrayList()

    init {
        userList = users as ArrayList<User>

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListner = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectChatUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.select_chat_layout,
            parent, false
        )
        return SelectChatUserViewHolder(itemView, mListner)

    }

    override fun onBindViewHolder(holder: SelectChatUserViewHolder, position: Int) {
        val userName = holder.itemView.findViewById<TextView>(R.id.chatUsernameTV)
        val profileImage = holder.itemView.findViewById<CircleImageView>(R.id.chatProfilePFP)
        val about = holder.itemView.findViewById<TextView>(R.id.aboutTV)


        holder.itemView.apply {
            userName.text = this@SelectChatAdapter.users[position].userName
            about.text = this@SelectChatAdapter.users[position].about
            if (this@SelectChatAdapter.users[position].pfpUri != "") {
                Glide.with(context).load(Uri.parse(this@SelectChatAdapter.users[position].pfpUri)).dontAnimate()
                    .into(profileImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.users.size
    }

    fun getSelectedUserIdList(): MutableList<String> {
        return selectedUserId
    }

}