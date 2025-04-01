package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.GET

// 팀 목록 API 호출 인터페이스
interface TeamListService {
    @GET("team/list") // 엔드포인트
    fun getTeamList(): Call<TeamListResponse>
}

data class TeamListResponse (
    val success: Boolean,
    val message: String,
    val data: List<TeamListItem>
)

data class TeamListItem (
    val leader: String,
    val teamId: Int,
    val memberCount: Int,
    val name: String,
    val description: String?
)