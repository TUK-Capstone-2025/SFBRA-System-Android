package com.example.sfbra_system_android.data

import retrofit2.Call
import retrofit2.http.GET

data class PathRecordResponse(
    val success: Boolean,
    val message: String,
    val data: List<PathRecordData>
)

data class PathRecordData(
    val PathRecordId: Int,
    val PathRecordDate: String
)

interface PathRecordService {
    @GET("member/??") // todo 엔드포인트 작성
    fun getPathRecord(): Call<PathRecordResponse>
}