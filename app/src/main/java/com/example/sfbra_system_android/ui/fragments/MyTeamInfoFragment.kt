package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.TeamMember
import com.example.sfbra_system_android.data.TeamMemberAdapter
import com.example.sfbra_system_android.data.viewmodels.MyTeamInfoViewModel

// 팀이 있는 경우: 팀 정보 화면
class MyTeamInfoFragment : Fragment() {
    private val myTeamViewModel: MyTeamInfoViewModel = MyTeamInfoViewModel()
    private lateinit var memberRecyclerView: RecyclerView
    private lateinit var teamName: TextView
    private lateinit var teamIntro: TextView
    private lateinit var failText: TextView
    private lateinit var retryButton: Button
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
        teamName = view.findViewById(R.id.team_name)
        teamIntro = view.findViewById(R.id.team_intro)
        failText = view.findViewById(R.id.fail_text)
        retryButton = view.findViewById(R.id.retry_button)

        val adapter = TeamMemberAdapter(emptyList())
        memberRecyclerView.adapter = adapter
        memberRecyclerView.layoutManager = LinearLayoutManager(context)

        getMyTeamInfo(adapter)

        retryButton.setOnClickListener {
            getMyTeamInfo(adapter)
        }

        return view
    }

    private fun getMyTeamInfo(adapter: TeamMemberAdapter) {
        myTeamViewModel.getTeamInfo(teamId)

        myTeamViewModel.teamInfo.observe(viewLifecycleOwner, Observer { teamInfo ->
            if (teamInfo != null && teamInfo.success) {
                failText.visibility = View.GONE
                memberRecyclerView.visibility = View.VISIBLE

                teamName.text = teamInfo.data.name // 팀 이름 적용
                teamIntro.text = teamInfo.data.description ?: "팀 소개가 없습니다." // 팀 소개 적용
                val leaderId = teamInfo.data.leader
                Log.d("MyTeamInfoFragment", "leaderId: $leaderId")

                val sortedMembers = mutableListOf<TeamMember>()
                var rank = 1

                val leader = teamInfo.data.members.find { it.memberId == leaderId }
                if (leader != null) {
                    sortedMembers.add(TeamMember(leader.memberId, leader.nickname, isLeader = true)) // 리더 처음으로 추가
                }

                // 리더를 제외, 팀원 정렬 후 랭킹 매김
                teamInfo.data.members
                    .filter { it.memberId != leaderId }
                    .forEach {
                        sortedMembers.add(
                            TeamMember(userId = it.memberId, name = it.nickname, isLeader = false, rank = rank++)
                        )
                    }

                adapter.updateMembers(sortedMembers) // 어댑터에 멤버 리스트 업데이트
            } else {
                // 실패 처리
                failText.visibility = View.VISIBLE
                memberRecyclerView.visibility = View.GONE
            }
            })
    }
}