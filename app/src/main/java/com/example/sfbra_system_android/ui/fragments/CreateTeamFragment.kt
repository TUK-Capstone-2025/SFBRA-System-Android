package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.sfbra_system_android.R

// 팀 생성하기 화면
class CreateTeamFragment : Fragment() {
    private lateinit var createButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_team, container, false)

        createButton = view.findViewById(R.id.create_button)
        createButton.setOnClickListener {
            createTeam()
        }

        return view
    }

    // 팀 생성 요청 함수
    private fun createTeam() {

    }
}