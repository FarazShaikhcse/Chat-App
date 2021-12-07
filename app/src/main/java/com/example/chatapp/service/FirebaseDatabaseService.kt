package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.util.Chat
import com.example.chatapp.util.Constants
import com.example.chatapp.wrapper.Message
import com.example.chatapp.wrapper.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlin.collections.ArrayList
import kotlin.coroutines.suspendCoroutine

object FirebaseDatabaseService {
    suspend fun writeUserDataToDatabase(user: User): Boolean {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            AuthenticationService.getUserID()?.let {
                db.collection(Constants.USERS).document(it)
                    .set(user).addOnSuccessListener {
                        cont.resumeWith(Result.success(true))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    suspend fun getChatsFromDB(limit: Int): MutableList<Chat> {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            AuthenticationService.getUserID()?.let { recid ->
                Log.d("userid", recid)
                db.collection(Constants.CHATS)
                    .whereArrayContains(Constants.PARTICIPANTS, recid)
                    .get().addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            val requests = ArrayList<Deferred<Chat>>()
                            for (doc in it) {
                                requests.add(async { getMessages(limit, doc) })
                            }
                            val chats = requests.awaitAll()
                            cont.resumeWith(Result.success(chats.toMutableList()))
                            Log.d("chatsfromdb", chats.size.toString())
                        }
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                        Log.d("chatsfromdb", it.toString())
                    }
            }

        }
    }

    suspend fun getMessages(limit: Int, doc: QueryDocumentSnapshot) = suspendCoroutine<Chat>{ cont ->

        doc.reference.collection(Constants.MESSAGES)
            .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
            .limit(limit.toLong()).get()
            .addOnSuccessListener {
                val msgList = arrayListOf<Message>()
                for (msg in it.documents) {
                    msgList.add(Message(
                        msg.getString(Constants.MESSAGEID)!!,
                        msg.getString(Constants.SENDERID)!!,
                        msg.get(Constants.SENT_TIME)!! as Long,
                        msg.getString(Constants.TEXT)!!,
                        msg.getString(Constants.MESSAGE_TYPE)!!
                    ))
                }
                val chat = Chat(
                    doc.get(Constants.PARTICIPANTS) as ArrayList<String>,
                    msgList
                )
                cont.resumeWith(Result.success(chat))
                Log.d("chatsfromdb", chat.toString())
            }
            .addOnFailureListener {
                cont.resumeWith(Result.failure(it))
            }
    }

}