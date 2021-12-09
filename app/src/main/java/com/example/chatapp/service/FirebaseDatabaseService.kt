package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.util.Constants
import com.example.chatapp.wrapper.Message
import com.example.chatapp.wrapper.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    suspend fun readUserDataToDatabase(): User {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            AuthenticationService.getUserID()?.let {
                db.collection(Constants.USERS).document(it)
                    .get().addOnSuccessListener {
                        val user = User(it.get(Constants.USERNAME).toString(),
                            it.get(Constants.ABOUT).toString(), it.get(Constants.USERID).toString())
                        cont.resumeWith(Result.success(user))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

//    suspend fun getChatsFromDB(limit: Long): java.util.ArrayList<Message> {
//        return suspendCoroutine { cont ->
//            val db = FirebaseFirestore.getInstance()
//            AuthenticationService.getUserID()?.let { recid ->
//                Log.d("userid", recid)
//                db.collection(Constants.USERS)
//                    .whereArrayContains(Constants.PARTICIPANTS, recid)
//                    .get().addOnSuccessListener {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            val requests = ArrayList<Deferred<Message>>()
//                            for (doc in it.documents) {
//                                requests.add(async { getMessages(limit, doc) })
//                            }
//                            val chats = requests.awaitAll()
//                            cont.resumeWith(Result.success(chats))
//                            Log.d("chatsfromdb", chats.size.toString())
//                        }
//                    }
//                    .addOnFailureListener {
//                        cont.resumeWith(Result.failure(it))
//                        Log.d("chatsfromdb", it.toString())
//                    }
//            }
//        }
//    }

//    suspend fun getMessages(limit: Long, doc: DocumentSnapshot) =
//        suspendCoroutine<Message> { cont ->
//
//            doc.reference.collection(Constants.MESSAGES)
//                .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
//                .limit(limit).get()
//                .addOnSuccessListener {
//                    val msgList = arrayListOf<Message>()
//                    for (msg in it.documents) {
//                        msgList.add(
//                             Message(
//                                msg.getString(Constants.SENDERID)!!,
//                                msg.get(Constants.SENT_TIME)!! as Long,
//                                msg.getString(Constants.TEXT)!!,
//                                msg.getString(Constants.MESSAGE_TYPE)!!
//                            )
//                        )
//                    }
//
//                    cont.resumeWith(Result.success(Message()))
////                    Log.d("chatsfromdb", chat.toString())
//                }
//                .addOnFailureListener {
//                    cont.resumeWith(Result.failure(it))
//                }
//        }

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
                                            msg.getString(Constants.MESSAGE_TYPE)!!
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
        }
        else {
            return senderId + "_" +  recId
        }
    }

    suspend fun sendTextToDb(senderId: String, receiverId:String, message: String):Boolean {
        val chatId = getChatDocid(senderId, receiverId)
        val dbMessage = Message(
            senderId,
            System.currentTimeMillis(),
            message,
            "text",
        )
        val db = FirebaseFirestore.getInstance()
        return suspendCoroutine {   callback ->
            db.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
                .add(dbMessage).addOnCompleteListener { task->
                    if(task.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    }else {
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
                .collection(Constants.MESSAGES).orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        this.trySend(null).isFailure
                        error.printStackTrace()
                    }else {
                        if(value != null) {
                            val messageList = arrayListOf<Message>()
                            for(item in value.documents) {
                                val data = item.data as HashMap<*,*>
                                val message = Message(
                                    senderId = data[Constants.SENDERID].toString(),
                                    sentTime = data[Constants.SENT_TIME] as Long,
                                    text = data[Constants.TEXT].toString(),
                                    messageType = data[Constants.MESSAGE_TYPE].toString(),
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

    suspend fun getAllUsersFromDb(): MutableList<User>? {
        return suspendCoroutine { cont ->
            val userList = mutableListOf<User>()
            val db = FirebaseFirestore.getInstance()
            AuthenticationService.getUserID()?.let {
                db.collection(Constants.USERS)
                    .whereNotEqualTo(Constants.USERID, it)
                    .get().addOnSuccessListener {
                        for ( doc in it ) {
                            val user = User(
                                doc.get(Constants.USERNAME).toString(),
                                doc.get(Constants.ABOUT).toString(),
                                doc.get(Constants.USERID).toString()
                            )
                            userList.add(user)
                        }
                        cont.resumeWith(Result.success(userList))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }
}