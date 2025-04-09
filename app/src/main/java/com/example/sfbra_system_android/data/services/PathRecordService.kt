package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

//주행기록 목록 조회 api 호출 인터페이스
interface PathRecordService {
    @GET("record/list") // 엔드포인트
    fun getPathRecord(): Call<PathRecordResponse>

    @GET("record/member/{memberId}") // 엔드포인트
    fun getMemberPathRecord(@Path("memberId") memberId: Int): Call<PathRecordResponse>
}

//주행기록 목록 응답 데이터 클래스
data class PathRecordResponse(
    val success: Boolean,
    val message: String,
    val data: List<PathRecordData>
)

// 주행기록 목록 형식
data class PathRecordData(
    val startTime: String,
    val recordId: Int,
)