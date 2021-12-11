package com.example.chatapp.service

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.wrapper.GroupChat
import com.example.chatapp.wrapper.Message
import com.example.chatapp.wrapper.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.collections.ArrayList
import kotlin.coroutines.suspendCoroutine
import kotlin.streams.asSequence

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

    suspend fun readUserDataFromDatabase(): User {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            AuthenticationService.getUserID()?.let {
                db.collection(Constants.USERS).document(it)
                    .get().addOnSuccessListener {
                        val user = User(
                            it.get(Constants.USERNAME).toString(),
                            it.get(Constants.ABOUT).toString(), it.get(Constants.USERID).toString(),
                            it.get(Constants.PFP_URI).toString()
                        )
                        cont.resumeWith(Result.success(user))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    suspend fun getChatsofUserFromDB(peerid: String, limit: Long): MutableList<Message> {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            AuthenticationService.getUserID()?.let { recid ->
                Log.d("userid", recid)
                val chatDocId = getChatDocid(recid, peerid)
                db.collection(Constants.CHATS)
                    .document(chatDocId)
                    .get().addOnSuccessListener {

                        it.reference.collection(Constants.MESSAGES)
                            .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                            .limit(limit).get()
                            .addOnSuccessListener {
                                val msgList = arrayListOf<Message>()
                                for (msg in it.documents) {
                                    msgList.add(
                                        Message(
                                            msg.getString(Constants.SENDERID)!!,
                                            msg.get(Constants.SENT_TIME)!! as Long,
                                            msg.getString(Constants.TEXT)!!,
                                            msg.getString(Constants.MESSAGE_TYPE)!!,
                                            msg.get(Constants.SENDER_NAME).toString()
                                        )
                                    )
                                }

                                cont.resumeWith(Result.success(msgList))
//                    Log.d("chatsfromdb", chat.toString())
                            }
                            .addOnFailureListener {
                                cont.resumeWith(Result.failure(it))
                            }
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                        Log.d("chatsfromdb", it.toString())
                    }
            }
        }
    }

    private fun getChatDocid(senderId: String, recId: String): String {
        if (senderId > recId) {
            return recId + "_" + senderId
        } else {
            return senderId + "_" + recId
        }
    }

    suspend fun sendTextToUserDb(senderId: String, receiverId: String, message: String, msgType: String): Boolean {
        val chatId = getChatDocid(senderId, receiverId)
        val dbMessage = Message(
            senderId,
            System.currentTimeMillis(),
            message,
            msgType,
            senderName = SharedPref.get(Constants.USERNAME).toString()
        )
        val db = FirebaseFirestore.getInstance()
        return suspendCoroutine { callback ->
            db.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
                .add(dbMessage).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    } else {
                        callback.resumeWith(
                            Result.failure(task.exception ?: Exception("Something went wrong"))
                        )
                    }
                }
        }
    }

    @ExperimentalCoroutinesApi
    fun getUpdatedChatsFromDb(senderId: String, receiverId: String):
            Flow<ArrayList<Message>?> {
        return callbackFlow {
            val chatId = getChatDocid(senderId, receiverId)
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(Constants.CHATS).document(chatId)
                .collection(Constants.MESSAGES)
                .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        this.trySend(null).isFailure
                        error.printStackTrace()
                    } else {
                        if (value != null) {
                            val messageList = arrayListOf<Message>()
                            for (item in value.documents) {
                                val data = item.data as HashMap<*, *>
                                val message = Message(
                                    senderId = data[Constants.SENDERID].toString(),
                                    sentTime = data[Constants.SENT_TIME] as Long,
                                    text = data[Constants.TEXT].toString(),
                                    messageType = data[Constants.MESSAGE_TYPE].toString(),
                                    senderName = data[Constants.SENDER_NAME].toString()
                                )
                                messageList.add(message)
                            }
                            this.trySend(messageList).isSuccess
                        }
                    }
                }
            awaitClose() {
                ref.remove()
            }
        }
    }

    suspend fun addUriToProfile(uri: Uri): Boolean {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            AuthenticationService.getUserID()?.let {
                db.collection(Constants.USERS).document(it)
                    .get().addOnSuccessListener { doc ->
                        doc.reference.update(Constants.PFP_URI, uri.toString())
                        cont.resumeWith(Result.success(true))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    fun getAllUsersFromDb(): Flow<ArrayList<User>?> {

        return callbackFlow {
            val uid = AuthenticationService.getUserID()
            val userList = ArrayList<User>()
            val ref = FirebaseFirestore.getInstance().collection(Constants.USERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        this.trySend(null).isSuccess
                        error.printStackTrace()
                    } else {
                        if (snapshot != null) {
                            for (doc in snapshot.documentChanges) {
                                if (doc.type == DocumentChange.Type.ADDED) {
                                    val item = doc.document
                                    if (item.id == uid) {
                                        continue
                                    } else {
                                        val user = User(
                                            item.get(Constants.USERNAME).toString(),
                                            item.get(Constants.ABOUT).toString(),
                                            item.get(Constants.USERID).toString(),
                                            item.get(Constants.PFP_URI).toString()
                                        )
                                        userList.add(user)
                                    }
                                }
                            }
                            this.trySend(userList).isSuccess
                        }
                    }
                }
            awaitClose {
                ref.remove()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun createGrp(name: String, userList: ArrayList<String>?): Boolean? {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

            val randomId = java.util.Random().ints(36, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
            val group = userList?.let { list ->
                AuthenticationService.getUserID()?.let { list.add(it) }
                GroupChat(randomId, list, name, messages = listOf(
                    Message(list[list.size - 1],
                System.currentTimeMillis(), "Welcome to Group $name", "text"
                    )
                ))
            }

            if (group != null) {

                db.collection(Constants.GROUPS).document(randomId)
                    .set(group).addOnSuccessListener {
                        cont.resumeWith(Result.success(true))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }


    fun getGroupsFromDb(): Flow<ArrayList<GroupChat>?> {

        return callbackFlow {
            val uid = AuthenticationService.getUserID()
            val groupList = ArrayList<GroupChat>()
            val ref = uid?.let {
                FirebaseFirestore.getInstance().collection(Constants.GROUPS)
                    .whereArrayContains(Constants.PARTICIPANTS, it)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            this.trySend(null).isSuccess
                            error.printStackTrace()
                        } else {
                            if (snapshot != null) {
                                for (doc in snapshot.documentChanges) {
                                    if (doc.type == DocumentChange.Type.ADDED) {
                                        val item = doc.document
                                        if (item.id == uid) {
                                            continue
                                        } else {
                                            val group = GroupChat(
                                                item.get(Constants.GROUP_ID).toString(),
                                                item.get(Constants.PARTICIPANTS) as ArrayList<String>,
                                                item.get(Constants.GROUP_NAME).toString(),
                                                item.get(Constants.PFP_URI).toString(),
                                                item.get(Constants.MESSAGES) as List<Message>
                                            )
                                            groupList.add(group)
                                        }
                                    }
                                }
                                Log.d("groupList", groupList.size.toString())
                                this.trySend(groupList).isSuccess
                            }
                        }
                    }
            }
            awaitClose {
                ref?.remove()
            }
        }

    }

    @ExperimentalCoroutinesApi
    fun getUpdatedGroupChatsFromDb(groupId: String):
            Flow<ArrayList<Message>?> {
        return callbackFlow {
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(Constants.GROUPS).document(groupId)
                .collection(Constants.MESSAGES)
                .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        this.trySend(null).isFailure
                        error.printStackTrace()
                    } else {
                        if (value != null) {
                            val messageList = arrayListOf<Message>()
                            for (item in value.documents) {
                                val data = item.data as HashMap<*, *>
                                val message = Message(
                                    senderId = data[Constants.SENDERID].toString(),
                                    sentTime = data[Constants.SENT_TIME] as Long,
                                    text = data[Constants.TEXT].toString(),
                                    messageType = data[Constants.MESSAGE_TYPE].toString(),
                                    senderName = data[Constants.SENDER_NAME].toString()
                                )
                                messageList.add(message)
                            }
                            this.trySend(messageList).isSuccess
                        }
                    }
                }
            awaitClose() {
                ref.remove()
            }
        }
    }

    suspend fun sendTextToGroupDb(sender: String, groupId: String, message: String, msgType: String): Boolean {
        val dbMessage = Message(
            sender,
            System.currentTimeMillis(),
            message,
            msgType,
            senderName = SharedPref.get(Constants.USERNAME).toString()
        )
        val db = FirebaseFirestore.getInstance()
        return suspendCoroutine { callback ->
            db.collection(Constants.GROUPS).document(groupId).collection(Constants.MESSAGES)
                .add(dbMessage).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    } else {
                        callback.resumeWith(
                            Result.failure(task.exception ?: Exception("Something went wrong"))
                        )
                    }
                }
        }
    }


}