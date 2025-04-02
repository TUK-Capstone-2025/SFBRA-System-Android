package com.example.sfbra_system_android.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.services.TeamInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyTeamInfoViewModel {
    private val _teamInfo = MutableLiveData<TeamInfoResponse?>()
    val teamInfo: LiveData<TeamInfoResponse?> get() = _teamInfo

    fun getTeamInfo(teamId: Int) {
        val service = RetrofitClient.getMyTeamInfoService()

        service.getTeamInfo(teamId).enqueue(object : Callback<TeamInfoResponse> {
            override fun onResponse(
                call: Call<TeamInfoResponse>,
                response: Response<TeamInfoResponse>
            ) {
                if (response.isSuccessful) {
                    _teamInfo.value = response.body()
                } else {
                    _teamInfo.value = null
                    Log.e("MyTeamInfoViewModel", "팀 상세정보 조회 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TeamInfoResponse>, t: Throwable) {
                _teamInfo.value = null
                Log.e("MyTeamInfoViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}