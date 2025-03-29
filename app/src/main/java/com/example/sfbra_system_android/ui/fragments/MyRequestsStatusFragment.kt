package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sfbra_system_android.R

// 내 신청 관리 화면
class MyRequestsStatusFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_requests_status, container, false)

        return view
    }

    // 신청 상태 표시 함수
    private fun showRequestsStatus() {

    }

    // 신청 취소 함수
    private fun cancelRequest() {

    }
}