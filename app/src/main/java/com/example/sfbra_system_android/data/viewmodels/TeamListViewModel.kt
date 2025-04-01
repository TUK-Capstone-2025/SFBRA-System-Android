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

    fun getTeamList() {
        val teamListService = RetrofitClient.getTeamListService()

        teamListService.getTeamList().enqueue(object : Callback<TeamListResponse> {
            override fun onResponse(call: Call<TeamListResponse>, response: Response<TeamListResponse>) {
                if (response.isSuccessful) {
                    _teamList.value = response.body()
                } else {
                    // 에러 처리
                    Log.e("TeamListViewModel", "팀 목록 조회 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TeamListResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("TeamListViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}