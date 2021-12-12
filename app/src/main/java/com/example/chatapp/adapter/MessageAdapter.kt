package com.example.chatapp.util

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.wrapper.Message
import java.text.DateFormat


class CustomAdapter(context: Context, list: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context: Context = context
    var list: ArrayList<Message>

    private inner class MessageInViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        var userNameTV: TextView
        var textMessageLayout: ConstraintLayout
        var imageMessageLayout: ConstraintLayout
        var recImage: ImageView
        var imageSenderTextView: TextView
        var imageSentTime: TextView
        fun bind(position: Int) {
            val messageModel: Message = list[position]
            if (messageModel.messageType == Constants.TEXT) {
                textMessageLayout.visibility = View.VISIBLE
                imageMessageLayout.visibility = View.GONE
                messageTV.text = messageModel.text
                dateTV.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.sentTime)
                if (SharedPref.get(Constants.CHAT_TYPE) == Constants.CHATS) {
                    userNameTV.visibility = View.GONE
                } else {
                    userNameTV.visibility = View.VISIBLE
                    userNameTV.text = messageModel.senderName
                }
            }
            else {
                textMessageLayout.visibility = View.GONE
                imageMessageLayout.visibility = View.VISIBLE
                if (SharedPref.get(Constants.CHAT_TYPE) == Constants.CHATS) {
                    imageSenderTextView.visibility = View.GONE
                } else {
                    imageSenderTextView.visibility = View.VISIBLE
                    imageSenderTextView.text = messageModel.senderName
                }
                imageSentTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.sentTime)
                Glide.with(context)
                    .load(Uri.parse(messageModel.text))
                    .dontAnimate()
                    .into(recImage)

            }
        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
            userNameTV = itemView.findViewById(R.id.textSenderNameTV)
            textMessageLayout = itemView.findViewById(R.id.rec_msg_layout)
            imageMessageLayout = itemView.findViewById(R.id.rec_img_layout)
            recImage = itemView.findViewById(R.id.image_message)
            imageSenderTextView = itemView.findViewById(R.id.imagesenderNameTV)
            imageSentTime = itemView.findViewById(R.id.image_date)
        }
    }

    private inner class MessageOutViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView
        var sentMessageLayout: ConstraintLayout
        var sentImageLayout:ConstraintLayout
        var sentTime: TextView
        var sentImage: ImageView
        fun bind(position: Int) {
            val messageModel: Message = list[position]
            if (messageModel.messageType == Constants.TEXT) {
                sentImageLayout.visibility = View.GONE
                sentMessageLayout.visibility = View.VISIBLE
                messageTV.text = messageModel.text
                dateTV.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.sentTime)
            }
            else {
                sentImageLayout.visibility = View.VISIBLE
                sentMessageLayout.visibility = View.INVISIBLE
                sentTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.sentTime)
                Glide.with(context)
                    .load(Uri.parse(messageModel.text))
                    .dontAnimate()
                    .into(sentImage)
            }

        }

        init {
            messageTV = itemView.findViewById(R.id.message_text)
            dateTV = itemView.findViewById(R.id.date_text)
            sentImageLayout = itemView.findViewById(R.id.sentImageLayout)
            sentMessageLayout = itemView.findViewById(R.id.sent_message_layout)
            sentTime = itemView.findViewById(R.id.sentImagetime)
            sentImage = itemView.findViewById(R.id.sentImage)
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
        this.list = list
    }
}