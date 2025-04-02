package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.RequestListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestListViewModel(application: Application) : AndroidViewModel(application) {
    private val _requestList = MutableLiveData<RequestListResponse?>()
    val requestList: LiveData<RequestListResponse?> get() = _requestList

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 팀 신청 목록 조회
    fun getRequestList() {
        val service = RetrofitClient.getRequestListService(token)

        service.getRequestList().enqueue(object : Callback<RequestListResponse> {
            override fun onResponse(call: Call<RequestListResponse>, response: Response<RequestListResponse>) {
                if (response.isSuccessful) {
                    _requestList.value = response.body()
                } else {
                    _requestList.value = null
                    Log.e("RequestListViewModel", "요청 목록 요청 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<RequestListResponse>, t: Throwable) {
                _requestList.value = null
                Log.e("RequestListViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}