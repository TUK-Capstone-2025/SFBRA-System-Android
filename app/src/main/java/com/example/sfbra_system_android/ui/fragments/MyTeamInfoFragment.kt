package com.example.sfbra_system_android.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.Applicant
import com.example.sfbra_system_android.data.ApplicantAdapter
import com.example.sfbra_system_android.data.TeamMember
import com.example.sfbra_system_android.data.TeamMemberAdapter
import com.example.sfbra_system_android.data.viewmodels.ApplicantMemberViewModel
import com.example.sfbra_system_android.data.viewmodels.KickMemberViewModel
import com.example.sfbra_system_android.data.viewmodels.MyTeamInfoViewModel

// 팀이 있는 경우: 팀 정보 화면
class MyTeamInfoFragment : Fragment() {
    private val myTeamViewModel: MyTeamInfoViewModel = MyTeamInfoViewModel()
    private val applicantsViewModel: ApplicantMemberViewModel by viewModels()
    private val kickMemberViewModel: KickMemberViewModel by viewModels()
    private lateinit var memberAdapter: TeamMemberAdapter // 멤버 목록 어댑터
    private lateinit var applicantAdapter: ApplicantAdapter // 신청자 목록 어댑터
    private lateinit var memberRecyclerView: RecyclerView // 멤버 리사이클러뷰
    private lateinit var applicantRecyclerView: RecyclerView // 신청 멤버 리사이클러뷰
    private lateinit var applicantContainer: LinearLayout
    private lateinit var teamName: TextView // 팀 이름
    private lateinit var teamIntro: TextView // 팀 소개
    private lateinit var failText: TextView
    private lateinit var retryButton: Button
    private var teamId: Int = -1 // 팀 id
    private var currentUserIsLeader = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teamId = arguments?.getInt(ARG_TEAM_ID) ?: -1 // 프래그먼트 생성 시 전달받은 teamId 가져오기
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_team_info, container, false)

        memberRecyclerView = view.findViewById(R.id.team_member_recyclerview) // 멤버 리사이클러뷰
        applicantRecyclerView = view.findViewById(R.id.applicant_recyclerview) // 신청 멤버 리사이클러뷰
        applicantContainer = view.findViewById(R.id.applicant_container) // 신청 멤버 컨테이너
        teamName = view.findViewById(R.id.team_name) // 팀 이름
        teamIntro = view.findViewById(R.id.team_intro) // 팀 소개
        failText = view.findViewById(R.id.fail_text) // 불러오기 실패 시 텍스트
        retryButton = view.findViewById(R.id.retry_button) // 재시도 버튼

        // 팀 신청 멤버 어댑터
        applicantAdapter = ApplicantAdapter(emptyList(),
            onAcceptClick = { applicantId ->
                Log.d("ApplicantAdapter", "수락: $applicantId")
                acceptApplicant(applicantId) // 수락 요청
            },
            onRejectClick = { applicantId ->
                Log.d("ApplicantAdapter", "거절: $applicantId")
                rejectApplicant(applicantId) // 거절 요청
            }
        )
        applicantRecyclerView.adapter = applicantAdapter
        applicantRecyclerView.layoutManager = LinearLayoutManager(context)

        getApplicantMember(applicantAdapter) // 지원자 목록 불러오기

        // 팀 멤버 어댑터
        memberAdapter = TeamMemberAdapter(
            members = emptyList(),
            onViewProfile = { memberId ->
                // 프로필 보기
                showMemberProfile(memberId)
            },
            onViewRecord = { member ->
                // 주행 기록 보기
                navigateToMemberRecord(member)
            },
            onKickMember = { member ->
                // 퇴출
                confirmKickMember(member)
            },
            currentUserIsLeader = currentUserIsLeader // 현재 사용자가 팀장인 경우
        )
        memberRecyclerView.adapter = memberAdapter
        memberRecyclerView.layoutManager = LinearLayoutManager(context)

        getMyTeamInfo(memberAdapter) // 팀 멤버 목록 불러오기

        // 재시도 버튼 클릭
        retryButton.setOnClickListener {
            // 다시 불러오기 요청
            getMyTeamInfo(memberAdapter)
            getApplicantMember(applicantAdapter)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kickMemberViewModel.kickResult.observe(viewLifecycleOwner, Observer { response ->
            if(response != null) {
                if (response.success) {
                    Toast.makeText(requireContext(), "해당 멤버를 퇴출시켰습니다.", Toast.LENGTH_SHORT).show()
                    getMyTeamInfo(memberAdapter)
                } else {
                    Toast.makeText(requireContext(), "퇴출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                kickMemberViewModel.clearKickMemberResponse()
            }
        })
    }

    // 팀 정보 불러오기 함수
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

    private fun showMemberProfile(memberId: Int) {
        Toast.makeText(requireContext(), "프로필 보기: $memberId", Toast.LENGTH_SHORT).show()
    }

    // 멤버 주행기록 프래그먼트로 전환 함수
    private fun navigateToMemberRecord(member: TeamMember) {
        val fragment = RidingPathFragment.newInstance(member.userId, member.name)
        replaceFragment(fragment)
    }

    // 자식 프래그먼트 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container2, fragment) // 프래그먼트 교체, 메인 액티비티 프래그먼트 컨테이너 id
            .addToBackStack(null) // 뒤로가기 눌렀을 시 이전 프래그먼트로 돌아가기
            .commit()
    }

    // 퇴출 확인 함수
    private fun confirmKickMember(member: TeamMember) {
        AlertDialog.Builder(requireContext())
            .setTitle("팀원 강퇴")
            .setMessage("${member.name}님을 정말로 강퇴하시겠습니까?")
            .setPositiveButton("강퇴") { _, _ ->
                kickMember(member.userId)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun kickMember(memberId: Int) {
        kickMemberViewModel.kickMember(memberId)
    }

    // 신청자 정보 불러오기 함수
    private fun getApplicantMember(adapter: ApplicantAdapter) {
        applicantsViewModel.getApplicantMemberList()

        applicantsViewModel.applicantMemberList.observe(viewLifecycleOwner, Observer { applicantList ->
            if (applicantList != null && applicantList.success) {
                // 신청자 목록
                val applicants = applicantList.data.map { applicant ->
                    Applicant(id = applicant.memberId, nickname = applicant.nickname)
                }
                currentUserIsLeader = true
                memberAdapter.updateLeaderStatus(true) // 동적으로 리더 상태 업데이트

                if (applicants.isNotEmpty()) {
                    applicantContainer.visibility = View.VISIBLE
                    applicantRecyclerView.visibility = View.VISIBLE
                    adapter.updateItems(applicants)
                } else {
                    // 리스트가 비어있을 때
                    applicantContainer.visibility = View.GONE
                    applicantRecyclerView.visibility = View.GONE
                }
            } else {
                currentUserIsLeader = false
                applicantContainer.visibility = View.GONE
                applicantRecyclerView.visibility = View.GONE
            }
        })
    }

    // 수락 요청 함수
    private fun acceptApplicant(applicantId: Int) {
        applicantsViewModel.acceptApplicant(applicantId)

        applicantsViewModel.acceptApplicantResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.success) {
                // 다시 불러오기
                getMyTeamInfo(memberAdapter)
                getApplicantMember(applicantAdapter)
            } else {
                Toast.makeText(requireContext(),"수락을 실패했습니다.\n다시 시도해주십시오.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 거절 요청 함수
    private fun rejectApplicant(applicantId: Int) {
        applicantsViewModel.rejectApplicant(applicantId)

        applicantsViewModel.rejectApplicantResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.success) {
                // 다시 불러오기
                getMyTeamInfo(memberAdapter)
                getApplicantMember(applicantAdapter)
            } else {
                Toast.makeText(requireContext(),"거절을 실패했습니다.\n다시 시도해주십시오.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}