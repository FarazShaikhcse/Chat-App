package com.example.chatapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.wrapper.Message
import android.content.Context
import java.text.DateFormat


class CustomAdapter(context: Context, list: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context: Context
    var list: ArrayList<Message>

    private inner class MessageInViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        fun bind(position: Int) {
            val messageModel: Message = list[position]
            messageTV.text = messageModel.text
            dateTV.setText(
                DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.sentTime)
            )
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
        }
    }

    private inner class MessageOutViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        fun bind(position: Int) {
            val messageModel: Message = list[position]
            messageTV.setText(messageModel.text)
            dateTV.setText(
                DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.sentTime)
            )
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_TYPE_IN) {
            MessageInViewHolder(
                LayoutInflater.from(context).inflate(R.layout.in_messages_layout, parent, false)
            )
        } else MessageOutViewHolder(
            LayoutInflater.from(context).inflate(R.layout.out_messages_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].senderId != AuthenticationService.getUserID()) {
            (holder as MessageInViewHolder).bind(position)
        } else {
            (holder as MessageOutViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId != AuthenticationService.getUserID()) {
            MESSAGE_TYPE_IN
        } else
            MESSAGE_TYPE_OUT
    }

    companion object {
        const val MESSAGE_TYPE_IN = 1
        const val MESSAGE_TYPE_OUT = 2
    }

    init { // you can pass other parameters in constructor
        this.context = context
        this.list = list
    }
}