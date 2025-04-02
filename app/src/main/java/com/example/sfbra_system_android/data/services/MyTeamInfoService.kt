package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MyTeamInfoService {
    @GET("team/{teamId}")
    fun getTeamInfo(@Path("teamId") teamId: Int): Call<TeamInfoResponse>
}

data class TeamInfoResponse(
    val success: Boolean,
    val message: String,
    val data: TeamInfoData
)

data class TeamInfoData(
    val teamId: Int,
    val name: String,
    val leader: String,
    val description: String?,
    val memberCount: Int,
    val members: List<MemberData>
)

data class MemberData(
    val userId: String,
    val name: String,
    val nickname: String
)