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
    val leader: Int,
    val description: String?,
    val memberCount: Int,
    val members: List<MemberData>,
    val sortedMembersByDistance: List<MemberData>
)

data class MemberData(
    val memberId: Int,
    val userId: String,
    val nickname: String,
    val totalDistance: Double
)