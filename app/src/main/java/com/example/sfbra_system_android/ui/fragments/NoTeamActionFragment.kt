package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.sfbra_system_android.R

// 팀이 없는 경우: 팀 만들기, 참가하기 버튼
class NoTeamActionFragment : Fragment() {
    private lateinit var CreateTeamButton: Button
    private lateinit var JoinTeamButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_no_team_action, container, false)

        CreateTeamButton = view.findViewById(R.id.btn_create_team)
        JoinTeamButton = view.findViewById(R.id.btn_join_team)

        // 팀 생성 버튼 클릭 시
        CreateTeamButton.setOnClickListener {
            replaceFragment(CreateTeamFragment())
        }

        // 팀 참가 버튼 클릭 시
        JoinTeamButton.setOnClickListener {
            replaceFragment(JoinTeamFragment())
        }

        return view
    }

    // 자식 프래그먼트 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container2, fragment) // 프래그먼트 교체, 메인 액티비티 프래그먼트 컨테이너 id
            .addToBackStack(null) // 뒤로가기 눌렀을 시 이전 프래그먼트로 돌아가기
            .commit()
    }
}