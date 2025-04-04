package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApplicantMemberService {
    @GET("member/listMembers")
    fun getApplicantMembers(): Call<ApplicantMemberResponse>

    @POST("team/approve")
    fun acceptApplicant(@Body request: AcceptApplicantRequest): Call<AcceptApplicantResponse>

    @POST("team/reject")
    fun rejectApplicant(@Body request: RejectApplicantRequest): Call<RejectApplicantResponse>
}

data class ApplicantMemberResponse (
    val success: Boolean,
    val message: String,
    val data: List<ApplicantMemberData>
)

data class ApplicantMemberData (
    val memberId: Int,
    val userId: String,
    val name: String,
    val nickname: String
)

data class AcceptApplicantRequest(
    val memberId: Int
)

data class AcceptApplicantResponse(
    val success: Boolean,
    val message: String,
    val data: String
)

data class RejectApplicantRequest(
    val memberId: Int
)

data class RejectApplicantResponse(
    val success: Boolean,
    val message: String,
    val data: String
)