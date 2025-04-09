package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.KickMemberResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KickMemberViewModel(application: Application) : AndroidViewModel(application) {
    private val _kickResult = MutableLiveData<KickMemberResponse>()
    val kickResult: LiveData<KickMemberResponse> get() = _kickResult

    private val token: String = SharedPreferencesHelper.getToken(application).toString()

    fun kickMember(memberId: Int) {
        val service = RetrofitClient.getKickMemberService(token)

        service.kickMember(memberId).enqueue(object : Callback<KickMemberResponse> {
            override fun onResponse(call: Call<KickMemberResponse>, response: Response<KickMemberResponse>) {
                if (response.isSuccessful) {
                    _kickResult.value = response.body()
                } else {
                    _kickResult.value = KickMemberResponse(false, "멤버 강퇴에 실패했습니다.", null)
                    Log.e("KickMemberViewModel", "멤버 강퇴 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<KickMemberResponse>, t: Throwable) {
                _kickResult.value = KickMemberResponse(false, "멤버 강퇴에 실패했습니다.", null)
                Log.e("KickMemberViewModel", "네트워크 오류: ${t.message}")
            }
        })
    }
}