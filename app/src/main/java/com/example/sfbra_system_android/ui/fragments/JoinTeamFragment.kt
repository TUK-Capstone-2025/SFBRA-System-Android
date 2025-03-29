package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.sfbra_system_android.R

// 팀 참가하기 화면
class JoinTeamFragment : Fragment() {
    private lateinit var myRequestManagement: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_join_team, container, false)

        myRequestManagement = view.findViewById(R.id.my_request_management)
        myRequestManagement.setOnClickListener {
            showMyRequestManagement()
        }

        return view
    }

    // 내 신청 관리 프래그먼트 이동 함수
    private fun showMyRequestManagement() {

    }
}