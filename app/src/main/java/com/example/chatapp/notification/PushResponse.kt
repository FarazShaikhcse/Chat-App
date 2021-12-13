package com.example.chatapp.notification

import com.google.gson.annotations.SerializedName

data class PushResponse(
    @SerializedName("multicast_id") val multicastId : Long,
    val success: Int,
    val failure: Int,
    @SerializedName("canonical_ids") val canonicalIds: Int,
    val results: ArrayList<HashMap<String,String>>
)