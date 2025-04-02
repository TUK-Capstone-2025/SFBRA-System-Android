package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.TeamMember
import com.example.sfbra_system_android.data.TeamMemberAdapter

// 팀이 있는 경우: 팀 정보 화면
class MyTeamInfoFragment : Fragment() {
    private lateinit var memberRecyclerView: RecyclerView
    private var teamId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teamId = arguments?.getInt(ARG_TEAM_ID) ?: -1 // 프래그먼트 생성 시 전달받은 teamId 가져오기
    }

    companion object {
        private const val ARG_TEAM_ID = "team_id" // teamId를 전달받기 위한 키

        // 이전 프래그먼트에서 id를 전달받아 새로운 인스턴스 생성
        fun newInstance(teamId: Int): MyTeamInfoFragment {
            val fragment = MyTeamInfoFragment()
            val args = Bundle() // 데이터 전달용 bundle
            args.putInt(ARG_TEAM_ID, teamId) // teamId를 번들에 담기
            fragment.arguments = args // 번들을 프래그먼트에 설정
            return fragment // 프래그먼트 반환
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_team_info, container, false)

        memberRecyclerView = view.findViewById(R.id.team_member_recyclerview)

        val members = listOf(
            TeamMember("홍길동", isLeader = true),
            TeamMember("김철수", rank = 1),
            TeamMember("이영희", rank = 2),
            TeamMember("박현우", rank = 3),
            TeamMember("최은지", rank = 4),
            TeamMember("정민호", rank = 5)
        )

        val adapter = TeamMemberAdapter(members)
        memberRecyclerView.adapter = adapter
        memberRecyclerView.layoutManager = LinearLayoutManager(context)


        return view
    }
}