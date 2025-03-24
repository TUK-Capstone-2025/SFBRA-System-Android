package com.example.sfbra_system_android.data

import retrofit2.Call
import retrofit2.http.GET

//주행기록 목록 응답 데이터 클래스
data class PathRecordResponse(
    val success: Boolean,
    val message: String,
    val data: List<PathRecordData>
)

// 주행기록 목록 형식
data class PathRecordData(
    val PathRecordId: Int,
    val PathRecordDate: String
)

//주행기록 목록 조회 api 호출 인터페이스
interface PathRecordService {
    @GET("record/list") // 엔드포인트
    fun getPathRecord(): Call<PathRecordResponse>
}