package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.JoinTeamResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinTeamViewModel(application: Application) : AndroidViewModel(application) {
    private val _joinTeamResponse = MutableLiveData<JoinTeamResponse?>()
    val joinTeamResponse: LiveData<JoinTeamResponse?> get() = _joinTeamResponse

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 팀 참가 요청
    fun joinTeam(teamId: Int) {
        val service = RetrofitClient.getJoinTeamService(token)

        service.joinTeam(teamId).enqueue(object : Callback<JoinTeamResponse> {
            override fun onResponse(call: Call<JoinTeamResponse>, response: Response<JoinTeamResponse>) {
                if (response.isSuccessful) {
                    // 팀 참가 요청 성공
                    _joinTeamResponse.value = response.body()
                } else {
                    // 팀 참가 요청 실패
                    _joinTeamResponse.value = JoinTeamResponse(false, "팀 참가 요청에 실패했습니다.", null)
                    Log.e("JoinTeamViewModel", "팀 참가 요청 실패: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<JoinTeamResponse>, t: Throwable) {
                // 네트워크 오류
                _joinTeamResponse.value = JoinTeamResponse(false, "팀 참가 요청에 실패했습니다.", null)
                Log.e("JoinTeamViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }

    fun clearJoinTeamResponse() {
        _joinTeamResponse.value = null
    }
}