package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.TeamCheckResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamCheckViewModel(application: Application) : AndroidViewModel(application) {
    private val _hasTeam = MutableLiveData<TeamCheckResponse?>()
    val hasTeam: LiveData<TeamCheckResponse?> get() = _hasTeam

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 팀 유무 조회
    fun hasTeam() {
        val teamCheckService = RetrofitClient.getTeamCheckService(token)

        teamCheckService.checkUserHasTeam().enqueue(object : Callback<TeamCheckResponse> {
            override fun onResponse(call: Call<TeamCheckResponse>, response: Response<TeamCheckResponse>) {
                if (response.isSuccessful) {
                    _hasTeam.value = response.body()
                } else {
                    Log.e("TeamCheckViewModel", "팀 유무 요청 실패: ${response.code()}")
                    _hasTeam.value = null
                }
            }

            override fun onFailure(call: Call<TeamCheckResponse>, t: Throwable) {
                Log.e("TeamCheckViewModel", "네트워크 오류: ${t.message}")
                _hasTeam.value = null
            }
        })
    }
}