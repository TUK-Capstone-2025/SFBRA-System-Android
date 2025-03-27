package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.PathRecordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PathRecordViewModel(application: Application) : AndroidViewModel(application) {
    private val _pathRecords = MutableLiveData<PathRecordResponse>()
    val pathRecords: LiveData<PathRecordResponse> get() = _pathRecords

    private val _message = MutableLiveData<String>() // 메시지 저장을 위한 LiveData
    val message: LiveData<String> get() = _message

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    fun fetchPathRecords() {
        val service = RetrofitClient.getPathRecordService(token)

        service.getPathRecord()
            .enqueue(object : Callback<PathRecordResponse> {
                override fun onResponse(call: Call<PathRecordResponse>, response: Response<PathRecordResponse>) {
                    if (response.isSuccessful) {
                        // 주행기록 목록 조회 성공
                        _pathRecords.value = response.body()
                    } else {
                        // 아이디 변경 실패: errorBody에서 메시지 읽기
                        try {
                            val errorResponse = response.errorBody()?.string()

                            // errorBody를 통해 받은 에러 메시지를 ApiResponse로 변환하여 저장
                            _pathRecords.value = PathRecordResponse(false, errorResponse ?: "불러오기 실패", emptyList())
                            _message.value = errorResponse ?: "불러오기 실패"

                            Log.e("PathRecordViewModel", "주행기록 목록 조회 실패: $errorResponse")
                        } catch (e: Exception) {
                            Log.e("PathRecordViewModel", "주행기록 조회 실패: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<PathRecordResponse>, t: Throwable) {
                    // 네트워크 오류
                    Log.e("PathRecordViewModel", "네트워크 오류: ${t.message}")
                }
            })
    }
}