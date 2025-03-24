package com.example.sfbra_system_android.data

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileUpdateViewModel(application: Application) : AndroidViewModel(application) {
    private val _changeNickResponse = MutableLiveData<ChangeNicknameResponse?>()
    val changeNickResponse: MutableLiveData<ChangeNicknameResponse?> get() = _changeNickResponse
    private val _changeIdResponse = MutableLiveData<ChangeUserIdResponse?>()
    val changeIdResponse: MutableLiveData<ChangeUserIdResponse?> get() = _changeIdResponse
    private val _changePassResponse = MutableLiveData<ChangePasswordResponse?>()
    val changePassResponse: MutableLiveData<ChangePasswordResponse?> get() = _changePassResponse

    // 저장된 사용자 토큰
    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 닉네임 변경
    fun changeNickname(newNickname: String) {
        val service = RetrofitClient.getProfileUpdateService(token)

        service.changeNickname(ChangeNicknameRequest(newNickname))
            .enqueue(object : Callback<ChangeNicknameResponse> {
                override fun onResponse(call: Call<ChangeNicknameResponse>, response: Response<ChangeNicknameResponse>) {
                    if (response.isSuccessful) {
                        // 닉네임 변경 성공
                        _changeNickResponse.value = response.body()
                    } else {
                        // 닉네임 변경 실패
                        Log.e("ProfileUpdateViewModel", "닉네임 변경 실패: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ChangeNicknameResponse>, t: Throwable) {
                    // 네트워크 오류
                    Log.e("ProfileUpdateViewModel", "네트워크 오류: ${t.message}")
                }
            })
    }

    // 아이디 변경
    fun changeUserId(newUserId: String) {
        val service = RetrofitClient.getProfileUpdateService(token)

        service.changeUserId(ChangeUserIdRequest(newUserId))
            .enqueue(object : Callback<ChangeUserIdResponse> {
                override fun onResponse(call: Call<ChangeUserIdResponse>, response: Response<ChangeUserIdResponse>) {
                    if (response.isSuccessful) {
                        // 아이디 변경 성공
                        _changeIdResponse.value = response.body()
                    } else {
                        // 아이디 변경 실패: errorBody에서 메시지 읽기
                        try {
                            val errorResponse = response.errorBody()?.string()
                            Log.e("ProfileUpdateViewModel", "아이디 변경 실패: $errorResponse")

                            // errorBody를 통해 받은 에러 메시지를 ApiResponse로 변환하여 저장
                            _changeIdResponse.value = ChangeUserIdResponse(false, errorResponse ?: "알 수 없는 오류", "")
                        } catch (e: Exception) {
                            Log.e("ProfileUpdateViewModel", "아이디 변경 실패 - Error parsing errorBody: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<ChangeUserIdResponse>, t: Throwable) {
                    // 네트워크 오류
                    Log.e("ProfileUpdateViewModel", "네트워크 오류: ${t.message}")
                }
            })
    }

    // 비밀번호 변경
    fun changePassword(currentPassword: String, newPassword: String) {
        val service = RetrofitClient.getProfileUpdateService(token)

        service.changePassword(ChangePasswordRequest(currentPassword, newPassword))
            .enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                    if (response.isSuccessful) {
                        // 비밀번호 변경 성공
                        _changePassResponse.value = response.body()
                    }
                    else {
                        // 비밀번호 변경 실패
                        Log.e("ProfileUpdateViewModel", "비밀번호 변경 실패: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    // 네트워크 오류
                    Log.e("ProfileUpdateViewModel", "네트워크 오류: ${t.message}")
                }
            })
    }
}