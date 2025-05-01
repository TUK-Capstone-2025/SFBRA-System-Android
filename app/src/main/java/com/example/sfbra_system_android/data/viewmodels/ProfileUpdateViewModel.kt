package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.ChangeAvatarResponse
import com.example.sfbra_system_android.data.services.ChangeNicknameRequest
import com.example.sfbra_system_android.data.services.ChangeNicknameResponse
import com.example.sfbra_system_android.data.services.ChangePasswordRequest
import com.example.sfbra_system_android.data.services.ChangePasswordResponse
import com.example.sfbra_system_android.data.services.ChangeUserIdRequest
import com.example.sfbra_system_android.data.services.ChangeUserIdResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import okhttp3.RequestBody.Companion.asRequestBody

class ProfileUpdateViewModel(application: Application) : AndroidViewModel(application) {
    private val _changeNickResponse = MutableLiveData<ChangeNicknameResponse?>()
    val changeNickResponse: MutableLiveData<ChangeNicknameResponse?> get() = _changeNickResponse
    private val _changeIdResponse = MutableLiveData<ChangeUserIdResponse?>()
    val changeIdResponse: MutableLiveData<ChangeUserIdResponse?> get() = _changeIdResponse
    private val _changePassResponse = MutableLiveData<ChangePasswordResponse?>()
    val changePassResponse: MutableLiveData<ChangePasswordResponse?> get() = _changePassResponse
    private val _changeAvatarResponse = MutableLiveData<ChangeAvatarResponse?>()
    val changeAvatarResponse: MutableLiveData<ChangeAvatarResponse?> get() = _changeAvatarResponse

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
                        _changeNickResponse.value = ChangeNicknameResponse(false, "닉네임 변경 실패", "")
                    }
                }

                override fun onFailure(call: Call<ChangeNicknameResponse>, t: Throwable) {
                    // 네트워크 오류
                    Log.e("ProfileUpdateViewModel", "네트워크 오류: ${t.message}")
                    _changeNickResponse.value = ChangeNicknameResponse(false, "닉네임 변경 실패", "")
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
                    _changeIdResponse.value = ChangeUserIdResponse(false, "아이디 변경 실패", "")
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
                        _changePassResponse.value = ChangePasswordResponse(false, "비밀번호 변경 실패", "")
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    // 네트워크 오류
                    Log.e("ProfileUpdateViewModel", "네트워크 오류: ${t.message}")
                    _changePassResponse.value = ChangePasswordResponse(false, "비밀번호 변경 실패", "")
                }
            })
    }

    // 프로필 사진 변경 (이미지 업로드)
    fun changeAvatar(uri: Uri) {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver

        try {
            // contentResolver로 URI에서 이미지 파일 읽기
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "profile_upload_temp.png")

            // 임시 파일로 복사
            tempFile.outputStream().use {
                inputStream?.copyTo(it)
            }

            // Multipart 형식으로 파일 준비
            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            // Retrofit 서비스 호출
            val service = RetrofitClient.getProfileUpdateService(token)
            service.changeAvatar(body).enqueue(object : Callback<ChangeAvatarResponse> {
                override fun onResponse(
                    call: Call<ChangeAvatarResponse>,
                    response: Response<ChangeAvatarResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("ProfileUpdateViewModel", "프로필 사진 업로드 성공: ${response.body()?.message}")
                        _changeAvatarResponse.value = response.body()
                    } else {
                        Log.e("ProfileUpdateViewModel", "프로필 사진 업로드 실패: ${response.message()}")
                        _changeAvatarResponse.value = ChangeAvatarResponse(false, "프로필 사진 업로드 실패", "")
                    }
                }

                override fun onFailure(call: Call<ChangeAvatarResponse>, t: Throwable) {
                    Log.e("ProfileUpdateViewModel", "프로필 사진 업로드 네트워크 오류: ${t.message}")
                    _changeAvatarResponse.value = ChangeAvatarResponse(false, "프로필 사진 업로드 네트워크 오류", "")
                }
            })

        } catch (e: Exception) {
            Log.e("ProfileUpdateViewModel", "프로필 이미지 처리 중 오류: ${e.message}")
        }
    }

    fun clearLiveData() {
        _changeNickResponse.value = null
        _changeIdResponse.value = null
        _changePassResponse.value = null
        _changeAvatarResponse.value = null
    }
}