package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.GET

interface RequestListService {
    @GET("member/applyStatus")
    fun getRequestList(): Call<RequestListResponse>
}

data class RequestListResponse(
    val success: Boolean,
    val message: String,
    val data: List<RequestListItem>?
)

data class RequestListItem(
    val teamId: Int,
    val teamName: String,
    val status: String
)