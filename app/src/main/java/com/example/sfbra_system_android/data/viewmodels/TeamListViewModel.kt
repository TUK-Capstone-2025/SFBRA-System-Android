package com.example.sfbra_system_android.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.services.TeamListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamListViewModel {
    private val _teamList = MutableLiveData<TeamListResponse>()
    val teamList: LiveData<TeamListResponse> get() = _teamList

    private val _message = MutableLiveData<String>() // 메시지 저장을 위한 LiveData
    val message: LiveData<String> get() = _message

    // 팀 목록 조회
    fun getTeamList() {
        val service = RetrofitClient.getTeamListService()

        service.getTeamList().enqueue(object : Callback<TeamListResponse> {
            override fun onResponse(call: Call<TeamListResponse>, response: Response<TeamListResponse>) {
                if (response.isSuccessful) {
                    _teamList.value = response.body()
                } else {
                    // 팀 조회 실패: errorBody에서 메시지 읽기
                    try {
                        val errorResponse = response.errorBody()?.string()

                        // errorBody를 통해 받은 에러 메시지를 ApiResponse로 변환하여 저장
                        _teamList.value = TeamListResponse(false, errorResponse ?: "불러오기 실패", emptyList())
                        _message.value = errorResponse ?: "불러오기 실패"

                        Log.e("PathRecordViewModel", "팀 목록 조회 실패: $errorResponse")
                    } catch (e: Exception) {
                        Log.e("PathRecordViewModel", "팀 목록 조회 실패: ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call<TeamListResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("TeamListViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}