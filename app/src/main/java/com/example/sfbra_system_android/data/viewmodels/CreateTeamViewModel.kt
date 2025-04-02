package com.example.sfbra_system_android.data.viewmodels;

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.CreateTeamRequest
import com.example.sfbra_system_android.data.services.CreateTeamResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateTeamViewModel(application: Application) : AndroidViewModel(application) {
    private val _createTeamResponse = MutableLiveData<CreateTeamResponse?>()
    val createTeamResponse: LiveData<CreateTeamResponse?> get() = _createTeamResponse

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 팀 생성
    fun createTeam(name: String, description: String) {
        val service = RetrofitClient.getCreateTeamService(token)

        service.createTeam(CreateTeamRequest(name, description))
            .enqueue(object : Callback<CreateTeamResponse> {
                override fun onResponse(call: Call<CreateTeamResponse>, response: Response<CreateTeamResponse>) {
                    if (response.isSuccessful) {
                        // 팀 생성 성공
                        _createTeamResponse.value = response.body()
                    } else {
                        // 팀 생성 실패
                        _createTeamResponse.value = null
                        Log.e("CreateTeamViewModel", "팀 생성 실패: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<CreateTeamResponse>, t: Throwable) {
                    // 네트워크 오류
                    _createTeamResponse.value = null
                    Log.e("CreateTeamViewModel", "네트워크 오류: ${t.message}")
                }
            })
    }
}

