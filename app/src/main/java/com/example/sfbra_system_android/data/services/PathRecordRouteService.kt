package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PathRecordRouteService {
    @POST("record/end")
    fun postPathRecord(@Body request: PathRecord): Call<PathRecordUploadResponse>

    @GET("record/my/route/{recordId}")
    fun getPathRecordDetail(@Path("recordId") recordId: Int): Call<PathRecordDetailResponse>
}

data class PathRecordUploadResponse(
    val success: Boolean,
    val message: String,
    val data: String
)

data class PathRecordDetailResponse(
    val success: Boolean,
    val message: String,
    val data: PathRecord
)

data class PathRecord (
    val startTime: String,
    val endTime: String,
    val route: List<LocationPoint>
)

data class LocationPoint (
    val latitude: Double,
    val longitude: Double,
    val warning: Int
)