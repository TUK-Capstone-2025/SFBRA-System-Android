package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.PathRecord
import com.example.sfbra_system_android.data.services.PathRecordDetailResponse
import com.example.sfbra_system_android.data.services.PathRecordUploadResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PathRecordRouteViewModel(application: Application) : AndroidViewModel(application) {
    private val _pathRecordUploadResponse = MutableLiveData<PathRecordUploadResponse>()
    val pathRecordUploadResponse: LiveData<PathRecordUploadResponse> get() = _pathRecordUploadResponse

    private val _pathRecordRoute = MutableLiveData<PathRecordDetailResponse>()
    val pathRecordRoute: LiveData<PathRecordDetailResponse> get() = _pathRecordRoute

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    fun postRecord(route: PathRecord) {
        val service = RetrofitClient.getPathRecordRouteService(token)

        service.postPathRecord(route).enqueue(object : Callback<PathRecordUploadResponse> {
            override fun onResponse(call: Call<PathRecordUploadResponse>, response: Response<PathRecordUploadResponse>) {
                if (response.isSuccessful) {
                    _pathRecordUploadResponse.value = response.body()
                } else {
                    Log.e("PathRecordRouteViewModel", "주행기록 업로드 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PathRecordUploadResponse>, t: Throwable) {
                Log.e("PathRecordRouteViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }

    fun getRecordRoute(recordId: Int) {
        val service = RetrofitClient.getPathRecordRouteService(token)

        service.getPathRecordDetail(recordId).enqueue(object : Callback<PathRecordDetailResponse> {
            override fun onResponse(call: Call<PathRecordDetailResponse>, response: Response<PathRecordDetailResponse>) {
                if (response.isSuccessful) {
                    _pathRecordRoute.value = response.body()
                } else {
                    Log.e("PathRecordRouteViewModel", "주행기록 상세 조회 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PathRecordDetailResponse>, t: Throwable) {
                Log.e("PathRecordRouteViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}