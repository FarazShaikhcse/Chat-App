package com.example.chatapp.service

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.wrapper.ChatUser
import com.example.chatapp.wrapper.GroupChat
import com.example.chatapp.wrapper.Message
import com.example.chatapp.wrapper.User
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
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
                            it.get(Constants.PFP_URI).toString(), it.get(Constants.TOKEN).toString()
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

    suspend fun sendTextToUserDb(
        senderId: String,
        receiverId: String,
        message: String,
        msgType: String
    ): Boolean {
        val chatId = getChatDocid(senderId, receiverId)
        val dbMessage = Message(
            senderId,
            System.currentTimeMillis(),
            message,
            msgType,
            senderName = SharedPref.get(Constants.USERNAME).toString()
        )
        Log.d("sendmessagedb", "called")
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
                GroupChat(
                    randomId, list, name, messages = listOf(
                        Message(
                            list[list.size - 1],
                            System.currentTimeMillis(), "Welcome to Group $name", "text"
                        )
                    )
                )
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

    suspend fun sendTextToGroupDb(
        sender: String,
        groupId: String,
        message: String,
        msgType: String
    ): Boolean {
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

    suspend fun getChatsFromDB(limit: Long): MutableList<ChatUser?> {
        return suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            var userList: List<ChatUser> = emptyList()
            AuthenticationService.getUserID()?.let { recid ->
                Log.d("userid", recid)

                var userDoc: DocumentSnapshot? = null
                val requests = ArrayList<Deferred<ChatUser>>()
                val userDetailsRequest = ArrayList<Deferred<User>>()
                db.collection(Constants.CHATS)
                    .whereArrayContains(Constants.PARTICIPANTS, recid)
                    .get().addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (doc in it) {
                                userDetailsRequest.add(async {
                                    var peerid = ""
                                    val participants =
                                        doc.get(Constants.PARTICIPANTS) as ArrayList<String>
                                    if (participants[0] == SharedPref.get(Constants.USERID)) {
                                        peerid = participants[1]
                                    } else {
                                        peerid = participants[0]
                                    }
                                    val fetchUserRequest = ArrayList<Deferred<User>>()
                                    fetchUserRequest.add(async {
                                        getUserDetails(peerid)
                                    }
                                    )
                                    val user = fetchUserRequest.awaitAll()
                                    return@async user[0]

                                })
                                val user = userDetailsRequest.awaitAll()
                                requests.add(async {
                                    getMessages(
                                        user[0].userId,
                                        user[0].userName,
                                        user[0].pfpUri,
                                        user[0].msgToken,
                                        limit,
                                        doc
                                    )
                                })
                                userDetailsRequest.clear()
                            }
                            val chats = requests.awaitAll()
                            Collections.sort(chats) { o1, o2 -> (o2.recentMsgTime - o1.recentMsgTime).toInt() }
                            cont.resumeWith(Result.success(chats.toMutableList()))
                            Log.d("chatlist", chats.size.toString())
                        }
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                        Log.d("chatsfromdb", it.toString())
                    }
            }

        }
    }

    private suspend fun getUserDetails(peerid: String): User {
        return suspendCoroutine { cont ->
            FirebaseFirestore.getInstance().collection(Constants.USERS).document(peerid).get()
                .addOnSuccessListener {
                    val peername = it.get(Constants.USERNAME) as String
                    val pfpUri = it.get(Constants.PFP_URI) as String
                    val msgToken = it.get(Constants.TOKEN) as String
                    val phone = it.get(Constants.PHONE) as String
                    cont.resumeWith(
                        Result.success(
                            User(
                                peername,
                                userId = peerid,
                                pfpUri = pfpUri,
                                msgToken = msgToken
                            )
                        )
                    )
                }

        }
    }


    suspend fun getMessages(
        peerid: String,
        peername: String,
        pfpUri: String,
        msgToken: String,
        limit: Long,
        doc: QueryDocumentSnapshot
    ) =
        suspendCoroutine<ChatUser> { cont ->

            lateinit var chatUser: ChatUser
            doc.reference.collection(Constants.MESSAGES)
                .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                .limit(limit).get()
                .addOnSuccessListener {

                    for (msg in it.documents) {
                        chatUser = ChatUser(
                            peername,
                            peerid,
                            msg.get(Constants.TEXT) as String,
                            msg.get(Constants.SENT_TIME) as Long,
                            pfpUri,
                            msgToken = msgToken
                        )
                    }
                    cont.resumeWith(Result.success(chatUser))
                }
                .addOnFailureListener {
                    cont.resumeWith(Result.failure(it))
                }
        }

    suspend fun getChatUsersFromDb(): ArrayList<User>? {
        return suspendCoroutine { cont ->
            var userList = ArrayList<User>()
            val db = FirebaseFirestore.getInstance()
            val cuid = SharedPref.get(Constants.USERID).toString()
            val chatDetailsRequest = ArrayList<Deferred<User?>>()
            db.collection(Constants.USERS)
                .whereNotEqualTo(Constants.USERID, cuid)
                .get().addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (doc in it) {
                            delay(100)
                            chatDetailsRequest.add(async {
                                if (!checkIfChatExists(cuid, doc.get(Constants.USERID) as String)) {
                                    val user = User(
                                        doc.get(Constants.USERNAME) as String,
                                        doc.get(Constants.ABOUT) as String,
                                        doc.get(Constants.USERID) as String,
                                        doc.get(Constants.PFP_URI) as String,
                                        doc.get(Constants.TOKEN) as String
                                    )
                                    return@async user
                                } else
                                    return@async null
                            })
                        }
                        userList = chatDetailsRequest.awaitAll().filterNotNull() as ArrayList<User>
                        Log.d("selectChatuser", userList.size.toString())
                        cont.resumeWith(Result.success(userList))
                    }
                }

                .addOnFailureListener {
                    cont.resumeWith(Result.failure(it))
                    Log.d("chatsfromdb", it.toString())
                }
        }

    }


    private suspend fun checkIfChatExists(user1: String, user2: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        return suspendCoroutine { cont ->
            db.collection(Constants.CHATS).document(getChatDocid(user1, user2)).get()
                .addOnCompleteListener {
                    cont.resumeWith(Result.success(it.result!!.exists()))
                }
                .addOnFailureListener {
                    cont.resumeWith(Result.failure(it))
                }
        }
    }

    suspend fun addNewUserChat(peerId: ChatUser): Boolean? {
        val db = FirebaseFirestore.getInstance()
        val cuid = SharedPref.get(Constants.USERID).toString()
        return suspendCoroutine { cont ->
            val chat = hashMapOf(
                Constants.PARTICIPANTS to listOf<String>(cuid, peerId.userId)
            )
            db.collection(Constants.CHATS).document(getChatDocid(peerId.userId, cuid)).set(chat)
                .addOnSuccessListener {
                    cont.resumeWith(Result.success(true))
                }
                .addOnFailureListener {
                    cont.resumeWith(Result.failure(it))
                }
        }
    }

    suspend fun updateTokentoDB(token: String) {
        val db = FirebaseFirestore.getInstance()
        val cuid = SharedPref.get(Constants.USERID).toString()
        return suspendCoroutine { cont ->
            val chat = hashMapOf(
                Constants.TOKEN to token
            )
            db.collection(Constants.USERS).document(cuid).update(chat as Map<String, Any>)
                .addOnFailureListener {
                    cont.resumeWith(Result.failure(it))
                }
        }
    }

    suspend fun getTokensOfMembers(participants: java.util.ArrayList<String>): List<String>? {

        return suspendCoroutine { cont ->

            val request = ArrayList<Deferred<String>>()
            CoroutineScope(Dispatchers.IO).launch {
                for (user in participants) {
                    if (user != SharedPref.get(Constants.USERID)) {
                        request.add(async {
                            getToken(user)
                        })
                    }
                }
                val list = request.awaitAll()
                Log.d("tokenlist", list.size.toString() + "ele")
                cont.resumeWith(Result.success(list))
            }

        }
    }

    private suspend fun getToken(user: String): String {
        val db = FirebaseFirestore.getInstance()
        return suspendCoroutine { continuation ->
            db.collection(Constants.USERS).document(user).get().addOnSuccessListener {
                continuation.resumeWith(Result.success(it.get(Constants.TOKEN) as String))
            }
                .addOnFailureListener {
                    continuation.resumeWith(Result.failure(it))
                }
        }
    }

    suspend fun loadNextChats(receiver: String?, offset: Long): MutableList<Message> {
        return suspendCoroutine { cont ->
            val sender = SharedPref.get(Constants.USERID).toString()
            val list = mutableListOf<Message>()
            if (offset != 0L && receiver != null) {
                Log.d("pagination", "offset $offset")
                val documentKey = getChatDocid(receiver, sender)
                FirebaseFirestore.getInstance().collection(Constants.CHATS)
                    .document(documentKey)
                    .collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                    .startAfter(offset)
                    .limit(15)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val querySnapshot = it.result
                            if (querySnapshot != null) {
                                for (i in querySnapshot.documents) {
                                    val senderId =
                                        i.get(Constants.SENDERID).toString()
                                    val senttime =
                                        i.get(Constants.SENT_TIME) as Long
                                    val messagec =
                                        i.get(Constants.TEXT).toString()
                                    val messageType =
                                        i.get(Constants.MESSAGE_TYPE)
                                            .toString()
                                    val senderName =
                                        i.get(Constants.SENDER_NAME).toString()
                                    val message = Message(
                                        senderId,
                                        senttime,
                                        messagec,
                                        messageType,
                                        senderName
                                    )
                                    list.add(message)
                                }
                            }
                            cont.resumeWith(Result.success(list))
                        } else {
                            Log.d("pagination", "failed")
                        }
                    }
                    .addOnFailureListener {
                        Log.d("pagination", "failed")
                    }
            }
        }
    }

    suspend fun loadNextChatsGroups(groupId: String, offset: Long): MutableList<Message> {
        return suspendCoroutine { cont ->
            val list = mutableListOf<Message>()
            if (offset != 0L) {
                Log.d("pagination", "offset $offset")
                FirebaseFirestore.getInstance().collection(Constants.GROUPS)
                    .document(groupId)
                    .collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                    .startAfter(offset)
                    .limit(15)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val querySnapshot = it.result
                            if (querySnapshot != null) {
                                for (i in querySnapshot.documents) {
                                    val senderId =
                                        i.get(Constants.SENDERID).toString()
                                    val senttime =
                                        i.get(Constants.SENT_TIME) as Long
                                    val messagec =
                                        i.get(Constants.TEXT).toString()

                                    val messageType =
                                        i.get(Constants.MESSAGE_TYPE)
                                            .toString()
                                    val senderName =
                                        i.get(Constants.SENDER_NAME).toString()
                                    val message = Message(
                                        senderId,
                                        senttime,
                                        messagec,
                                        messageType,
                                        senderName
                                    )
                                    list.add(message)
                                }
                            }
                            cont.resumeWith(Result.success(list))
                        } else {
                            Log.d("pagination", "failed")
                        }
                    }
                    .addOnFailureListener {
                        Log.d("pagination", "failed")
                    }
            }
        }
    }
    @ExperimentalCoroutinesApi
    fun getChatUpdates(receiver: String?): Flow<Message?> {
        return callbackFlow<Message?> {
            val sender = SharedPref.get(Constants.USERID)
            if (sender != null && receiver != null) {
                val getdocumentkey = getChatDocid(receiver, sender)
                val ref = FirebaseFirestore.getInstance().collection(Constants.CHATS)
                    .document(getdocumentkey)
                    .collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.ASCENDING)
                    .limitToLast(15)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            cancel("error fetching collection data at path", error)
                        }
                        if (snapshot != null) {
                            for (document in snapshot.documentChanges) {
                                if (document.type == DocumentChange.Type.ADDED) {
//                                    Log.d("snapshot", "inside snapshot")
                                    val senderid =
                                        document.document.get(Constants.SENDERID)
                                            .toString()
                                    val sentTime =
                                        document.document.get(Constants.SENT_TIME) as Long
                                    val message =
                                        document.document.get(Constants.TEXT).toString()
                                    val senderName = document.document.get(Constants.SENDER_NAME)
                                        .toString()
                                    val messageType =
                                        document.document.get(Constants.MESSAGE_TYPE)
                                            .toString()
                                    val chat = Message(
                                        senderid,
                                        sentTime,
                                        message,
                                        messageType,
                                        senderName
                                    )
                                    Log.d("add", "fetching notes")
                                    trySend(chat).isSuccess
                                }
                            }
                        }
                    }
                awaitClose {
                    ref.remove()
                }

            }
        }
    }

    fun getGroupChatUpdates(groupId: String): Flow<Message?>  {
        return callbackFlow<Message?> {
            val ref = FirebaseFirestore.getInstance().collection(Constants.GROUPS)
                .document(groupId)
                .collection(Constants.MESSAGES)
                .orderBy(Constants.SENT_TIME, Query.Direction.ASCENDING)
                .limitToLast(15)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        cancel("error fetching collection data at path", error)
                    }
                    if (snapshot != null) {
                        for (document in snapshot.documentChanges) {
                            if (document.type == DocumentChange.Type.ADDED) {
//                                    Log.d("snapshot", "inside snapshot")
                                val senderid =
                                    document.document.get(Constants.SENDERID)
                                        .toString()
                                val sentTime =
                                    document.document.get(Constants.SENT_TIME) as Long
                                val message =
                                    document.document.get(Constants.TEXT).toString()
                                val senderName = document.document.get(Constants.SENDER_NAME)
                                    .toString()
                                val messageType =
                                    document.document.get(Constants.MESSAGE_TYPE)
                                        .toString()
                                val chat = Message(
                                    senderid,
                                    sentTime,
                                    message,
                                    messageType,
                                    senderName
                                )
                                Log.d("add", "fetching notes")
                                trySend(chat).isSuccess
                            }
                        }
                    }
                }
            awaitClose {
                ref.remove()
            }

        }
    }
}