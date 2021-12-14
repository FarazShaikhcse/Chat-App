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
import com.example.chatapp.wrapper.GroupChat
import de.hdodenhof.circleimageview.CircleImageView

class GroupChatAdapter(
    var groups: MutableList<GroupChat>, val context: Context
) : RecyclerView.Adapter<GroupChatAdapter.ChatsViewHolder>() {

    inner class ChatsViewHolder(itemview: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemview) {
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
    var groupList: ArrayList<GroupChat> = ArrayList()

    init {
        groupList = groups as ArrayList<GroupChat>

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
        val message = holder.itemView.findViewById<TextView>(R.id.recentMsgTV)
        val pfp = holder.itemView.findViewById<CircleImageView>(R.id.chatProfilePFP)

        holder.itemView.apply {
            username.text = groupList[position].groupName
//            message.text = userList[position].message[0].text
            Log.d("uri", groupList[position].pfpUri)
            if (groupList[position].pfpUri != "") {
                Glide.with(context)
                    .load(Uri.parse(groupList[position].pfpUri))
                    .into(pfp)
            }
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }
}

