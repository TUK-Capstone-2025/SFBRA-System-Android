package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.DeleteRequestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteRequestViewModel(application: Application) : AndroidViewModel(application) {
    private val _deleteRequestResponse = MutableLiveData<DeleteRequestResponse?>()
    val deleteRequestResponse: MutableLiveData<DeleteRequestResponse?> get() = _deleteRequestResponse

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 신청 취소
    fun deleteRequest() {
        val service = RetrofitClient.getDeleteRequestService(token)

        service.deleteRequest().enqueue(object : Callback<DeleteRequestResponse> {
            override fun onResponse(call: Call<DeleteRequestResponse>, response: Response<DeleteRequestResponse>) {
                if (response.isSuccessful) {
                    // 요청 삭제 성공
                    _deleteRequestResponse.value = response.body()
                } else {
                    // 요청 삭제 실패
                    _deleteRequestResponse.value = null
                    Log.e("DeleteRequestViewModel", "요청 삭제 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DeleteRequestResponse>, t: Throwable) {
                // 네트워크 오류
                _deleteRequestResponse.value = null
                Log.e("DeleteRequestViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}