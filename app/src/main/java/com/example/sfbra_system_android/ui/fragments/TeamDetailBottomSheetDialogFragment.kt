package com.example.sfbra_system_android.ui.bottomsheets

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.services.TeamListItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// 바텀시트 팀 세부 정보
class TeamDetailBottomSheetDialogFragment(
    private val team: TeamListItem,
    private val onJoinClick: (Int) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_detail_bottom_sheet_dialog, container, false)

        val nameText = view.findViewById<TextView>(R.id.team_name)
        val leaderText = view.findViewById<TextView>(R.id.team_leader)
        val memberCountText = view.findViewById<TextView>(R.id.team_member_count)
        val descriptionText = view.findViewById<TextView>(R.id.team_description)
        val joinButton = view.findViewById<Button>(R.id.join_team_button)

        nameText.text = team.name
        leaderText.text = "팀장: ${team.leader}"
        memberCountText.text = "멤버 수: ${team.memberCount}"
        descriptionText.text = team.description ?: "설명이 없습니다."

        joinButton.setOnClickListener {
            onJoinClick(team.teamId)
            dismiss()
        }

        return view
    }
}
