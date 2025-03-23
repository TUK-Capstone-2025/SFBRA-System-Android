package com.example.sfbra_system_android.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PathRecordViewModel(application: Application) : AndroidViewModel(application) {
    private val _pathRecords = MutableLiveData<List<PathRecordData>>()
    val pathRecords: LiveData<List<PathRecordData>> get() = _pathRecords

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    fun fetchPathRecords() {
        val service = RetrofitClient.getPathRecordService(token)

        service.getPathRecord()
            .enqueue(object : Callback<PathRecordResponse> {
                override fun onResponse(call: Call<PathRecordResponse>, response: Response<PathRecordResponse>) {
                    if (response.isSuccessful) {
                        // 주행기록 목록 조회 성공
                        _pathRecords.value = response.body()?.data
                    } else {
                        // 조회 실패
                        Log.e("PathRecordViewModel", "주행기록 목록 조회 실패: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PathRecordResponse>, t: Throwable) {
                    // 네트워크 오류
                    Log.e("PathRecordViewModel", "네트워크 오류: ${t.message}")
                }
            })
    }
}