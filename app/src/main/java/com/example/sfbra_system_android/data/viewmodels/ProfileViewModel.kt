package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _userInfo = MutableLiveData<ProfileResponse?>()
    val userInfo: LiveData<ProfileResponse?> get() = _userInfo

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 사용자 정보 조회
    fun fetchUserInfo() {
        val userService = RetrofitClient.getUserInfoService(token) // retrofit 객체 생성

        userService.getUserInfo().enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    _userInfo.value = response.body()
                } else {
                    Log.e("UserViewModel", "사용자 정보 요청 실패: ${response.code()}")
                    _userInfo.value = null
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("UserViewModel", "네트워크 오류: ${t.message}")
                _userInfo.value = null
            }
        })
    }
}