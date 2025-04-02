package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateTeamService {
    @POST("team/create") // 엔드포인트
    fun createTeam(@Body request: CreateTeamRequest): Call<CreateTeamResponse>
}

data class CreateTeamRequest(
    val name: String,
    val description: String?
)

data class CreateTeamResponse(
    val success: Boolean,
    val message: String,
    val data: String?
)