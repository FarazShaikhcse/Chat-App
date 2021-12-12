package com.example.chatapp.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.wrapper.ChatUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class ChatAdapter(
    var users: MutableList<ChatUser?>, val context: Context
) : RecyclerView.Adapter<ChatAdapter.ChatsViewHolder>() {

    inner class ChatsViewHolder(itemview: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemview) {
        init {
            itemview.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var mListner: OnItemClickListener
    var userList: ArrayList<ChatUser> = ArrayList()

    init {
        userList = users as ArrayList<ChatUser>

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListner = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chats_layout, parent, false)
        return ChatsViewHolder(view, mListner)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val username = holder.itemView.findViewById<TextView>(R.id.chatUsernameTV)
        val message = holder.itemView.findViewById<TextView>(R.id.aboutTV)
        val pfp = holder.itemView.findViewById<CircleImageView>(R.id.chatProfilePFP)

        holder.itemView.apply {
            username.text = userList[position].userName
            val imagePattern = Regex("^https://firebasestorage.googleapis.com")
            if (imagePattern.containsMatchIn(userList[position].recentMsg) )
                message.text = "Image"
            else
                message.text = userList[position].recentMsg
            Log.d("uri", userList[position].pfpUri)
            if(userList[position].pfpUri != "") {
                Glide.with(context)
                    .load(Uri.parse(userList[position].pfpUri))
                    .into(pfp)
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }


}