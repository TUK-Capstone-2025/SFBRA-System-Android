package com.example.sfbra_system_android.data.services

interface PathRecordUploadService {

}

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