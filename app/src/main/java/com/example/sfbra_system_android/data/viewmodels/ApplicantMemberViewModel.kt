package com.example.sfbra_system_android.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.AcceptApplicantResponse
import com.example.sfbra_system_android.data.services.ApplicantMemberResponse

class ApplicantMemberViewModel(application: Application) : AndroidViewModel(application) {
    private val _applicantMemberList = MutableLiveData<ApplicantMemberResponse>()
    val applicantMemberList: LiveData<ApplicantMemberResponse> get() = _applicantMemberList

    private val _acceptApplicantResponse = MutableLiveData<AcceptApplicantResponse>()
    val acceptApplicantResponse: LiveData<AcceptApplicantResponse> get() = _acceptApplicantResponse
    private val _rejectApplicantResponse = MutableLiveData<AcceptApplicantResponse>()
    val rejectApplicantResponse: LiveData<AcceptApplicantResponse> get() = _rejectApplicantResponse

    private val token: String = SharedPreferencesHelper.getToken(application).toString()
}