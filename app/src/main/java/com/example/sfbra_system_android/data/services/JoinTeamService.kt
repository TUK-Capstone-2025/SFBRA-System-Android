package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

// 팀 참가 요청
interface JoinTeamService {
    @POST("member/applyTeam/{teamId}") // 엔드포인트
    fun joinTeam(@Path("teamId") teamId: Int): Call<JoinTeamResponse>
}

data class JoinTeamResponse(
    val success: Boolean,
    val message: String,
    val data: String?
)