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
    private val _pathRecords = MutableLiveData<PathRecordResponse>() // 본인 기록용
    val pathRecords: LiveData<PathRecordResponse> get() = _pathRecords
    private val _message1 = MutableLiveData<String>() // 메시지 저장을 위한 LiveData
    val message1: LiveData<String> get() = _message1

    private val _memberPathRecords = MutableLiveData<PathRecordResponse>() // 멤버 기록용
    val memberPathRecords: LiveData<PathRecordResponse> get() = _memberPathRecords
    private val _message2 = MutableLiveData<String>() // 메시지 저장을 위한 LiveData
    val message2: LiveData<String> get() = _message2

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 본인 주행기록 목록 조회
    fun getPathRecords() {
        val service = RetrofitClient.getPathRecordService(token)

        service.getPathRecord()
            .enqueue(object : Callback<PathRecordResponse> {
                override fun onResponse(call: Call<PathRecordResponse>, response: Response<PathRecordResponse>) {
                    if (response.isSuccessful) {
                        // 주행기록 목록 조회 성공
                        _pathRecords.value = response.body()
                    } else {
                        // 주행기록 불러오기 실패: errorBody에서 메시지 읽기
                        try {
                            val errorResponse = response.errorBody()?.string()

                            // errorBody를 통해 받은 에러 메시지를 ApiResponse로 변환하여 저장
                            _pathRecords.value = PathRecordResponse(false, errorResponse ?: "불러오기 실패", emptyList())
                            _message1.value = errorResponse ?: "불러오기 실패"

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

    // 멤버 주행기록 목록 조회
    fun getMemberPathRecords(memberId: Int) {
        val service = RetrofitClient.getPathRecordService(token)

        service.getMemberPathRecord(memberId)
            .enqueue(object : Callback<PathRecordResponse> {
                override fun onResponse(call: Call<PathRecordResponse>, response: Response<PathRecordResponse>) {
                    if (response.isSuccessful) {
                        // 주행기록 목록 조회 성공
                        _memberPathRecords.value = response.body()
                    } else {
                        // 주행기록 불러오기 실패: errorBody에서 메시지 읽기
                        try {
                            val errorResponse = response.errorBody()?.string()

                            // errorBody를 통해 받은 에러 메시지를 ApiResponse로 변환하여 저장
                            _memberPathRecords.value = PathRecordResponse(false, errorResponse ?: "불러오기 실패", emptyList())
                            _message2.value = errorResponse ?: "불러오기 실패"

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