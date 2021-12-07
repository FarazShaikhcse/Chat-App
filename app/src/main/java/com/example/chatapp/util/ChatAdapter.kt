package com.example.chatapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.AuthenticationService
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(
    var chats: MutableList<Chat>
) : RecyclerView.Adapter<ChatAdapter.ChatsViewHolder>() {

    inner class ChatsViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    var chatList: ArrayList<Chat> = ArrayList()

    init {
        chatList = chats as ArrayList<Chat>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chats_layout, parent, false)
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val username = holder.itemView.findViewById<TextView>(R.id.chatUsernameTV)
        val message = holder.itemView.findViewById<TextView>(R.id.messageTV)
        holder.itemView.apply {
            if(chatList[position].participants[0] != AuthenticationService.getUserID())
                username.text = chatList[position].participants[0]
            else
                username.text = chatList[position].participants[1]
            message.text = chatList[position].message[0].text
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }


}