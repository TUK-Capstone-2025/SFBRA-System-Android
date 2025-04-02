package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.DELETE

// 신청 취소 요청
interface DeleteRequestService {
    @DELETE("member/cancel") // 엔드포인트
    fun deleteRequest(): Call<DeleteRequestResponse>
}

data class DeleteRequestResponse (
    val success: Boolean,
    val message: String,
    val data: String?
)