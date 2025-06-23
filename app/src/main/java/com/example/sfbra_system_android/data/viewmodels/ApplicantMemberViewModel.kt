package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.AcceptApplicantRequest
import com.example.sfbra_system_android.data.services.AcceptApplicantResponse
import com.example.sfbra_system_android.data.services.ApplicantMemberResponse
import com.example.sfbra_system_android.data.services.RejectApplicantRequest
import com.example.sfbra_system_android.data.services.RejectApplicantResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplicantMemberViewModel(application: Application) : AndroidViewModel(application) {
    private val _applicantMemberList = MutableLiveData<ApplicantMemberResponse>()
    val applicantMemberList: LiveData<ApplicantMemberResponse> get() = _applicantMemberList

    private val _acceptApplicantResponse = MutableLiveData<AcceptApplicantResponse>()
    val acceptApplicantResponse: LiveData<AcceptApplicantResponse> get() = _acceptApplicantResponse
    private val _rejectApplicantResponse = MutableLiveData<RejectApplicantResponse>()
    val rejectApplicantResponse: LiveData<RejectApplicantResponse> get() = _rejectApplicantResponse

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    // 멤버 조회
    fun getApplicantMemberList() {
        val service = RetrofitClient.getApplicantMemberService(token)

        service.getApplicantMembers().enqueue(object : Callback<ApplicantMemberResponse> {
            override fun onResponse(call: Call<ApplicantMemberResponse>, response: Response<ApplicantMemberResponse>) {
                if (response.isSuccessful) {
                    _applicantMemberList.value = response.body()
                } else {
                    Log.e("ApplicantMemberViewModel", "멤버 조회 실패: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ApplicantMemberResponse>, t: Throwable) {
                Log.e("ApplicantMemberViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }

    fun acceptApplicant(applicantId: Int) {
        val service = RetrofitClient.getApplicantMemberService(token)

        service.acceptApplicant(AcceptApplicantRequest(applicantId)).enqueue(object : Callback<AcceptApplicantResponse> {
            override fun onResponse(call: Call<AcceptApplicantResponse>, response: Response<AcceptApplicantResponse>) {
                if (response.isSuccessful) {
                    _acceptApplicantResponse.value = response.body()
                } else {
                    Log.e("ApplicantMemberViewModel", "멤버 수락 실패: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<AcceptApplicantResponse>, t: Throwable) {
                Log.e("ApplicantMemberViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }

    fun rejectApplicant(applicantId: Int) {
        val service = RetrofitClient.getApplicantMemberService(token)

        service.rejectApplicant(RejectApplicantRequest(applicantId)).enqueue(object : Callback<RejectApplicantResponse> {
            override fun onResponse(call: Call<RejectApplicantResponse>, response: Response<RejectApplicantResponse>) {
                if (response.isSuccessful) {
                    _rejectApplicantResponse.value = response.body()
                } else {
                    Log.e("ApplicantMemberViewModel", "멤버 거절 실패: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RejectApplicantResponse>, t: Throwable) {
                Log.e("ApplicantMemberViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}