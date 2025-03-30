package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.TeamListAdapter

// 팀 참가하기 화면
class JoinTeamFragment : Fragment() {
    private lateinit var myRequestManagement: TextView
    private lateinit var teamListRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_join_team, container, false)

        myRequestManagement = view.findViewById(R.id.my_request_management)
        myRequestManagement.setOnClickListener {
            showMyRequestManagement()
        }

        // todo 임시 데이터
        teamListRecyclerView = view.findViewById(R.id.team_list_recyclerview)
        val teamList = listOf("재미있는 팀", "멋있는 팀", "활기찬 팀", "어지러운 팀")

        val adapter = TeamListAdapter(teamList) { teamName ->
            Toast.makeText(context, "$teamName 클릭됨", Toast.LENGTH_SHORT).show()
            // todo 클릭 시 원하는 동작
        }

        teamListRecyclerView.adapter = adapter
        teamListRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    // 내 신청 관리 프래그먼트 이동 함수
    private fun showMyRequestManagement() {

    }
}