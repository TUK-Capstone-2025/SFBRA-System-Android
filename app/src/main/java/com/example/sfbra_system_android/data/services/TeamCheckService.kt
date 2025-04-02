package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.GET

// 사용자 정보 API 호출 인터페이스
interface TeamCheckService {
    @GET("member/team") // 엔드포인트
    fun checkUserHasTeam(): Call<TeamCheckResponse>
}

data class TeamCheckResponse (
    val success: Boolean,
    val message: String,
    val data: TeamCheckData
)

data class TeamCheckData (
    val isInTeam: Boolean,
    val teamId: Int
)