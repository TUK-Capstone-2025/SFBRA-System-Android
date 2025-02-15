package com.example.sfbra_system_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// 내 팀 화면
class MyTeamFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_team, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreateTeam = view.findViewById<Button>(R.id.btnCreateTeam)
        val btnJoinTeam = view.findViewById<Button>(R.id.btnJoinTeam)

        btnCreateTeam.setOnClickListener {
            replaceFragment(CreateTeamFragment())
        }

        btnJoinTeam.setOnClickListener {
            replaceFragment(JoinTeamFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 프래그먼트 교체, 메인 액티비티 프래그먼트 컨테이너 id
            .addToBackStack(null) // 뒤로가기 눌렀을 시 이전 프래그먼트로 돌아가기
            .commit()
    }
}