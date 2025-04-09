package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.TeamListAdapter
import com.example.sfbra_system_android.data.services.JoinTeamResponse
import com.example.sfbra_system_android.data.services.TeamListItem
import com.example.sfbra_system_android.data.viewmodels.JoinTeamViewModel
import com.example.sfbra_system_android.data.viewmodels.TeamListViewModel
import com.example.sfbra_system_android.ui.bottomsheets.TeamDetailBottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject

// 팀 참가하기 화면
class JoinTeamFragment : Fragment() {
    private val teamListViewModel: TeamListViewModel = TeamListViewModel()
    private val joinTeamViewModel: JoinTeamViewModel by viewModels()
    private lateinit var myRequestManagement: TextView
    private lateinit var teamListRecyclerView: RecyclerView
    private lateinit var noTeamsText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_join_team, container, false)

        noTeamsText = view.findViewById(R.id.no_teams_text) // 팀 목록 없을 때의 텍스트뷰
        myRequestManagement = view.findViewById(R.id.my_request_management) // 내 신청 관리
        myRequestManagement.setOnClickListener {
            replaceFragment(MyRequestsStatusFragment())
        }

        teamListRecyclerView = view.findViewById(R.id.team_list_recyclerview) // 팀목록 리사이클러뷰

        val adapter = TeamListAdapter(emptyList()) { team ->
            // 바텀시트 호출
            val bottomSheet = TeamDetailBottomSheetDialogFragment(team) { teamId ->
                joinTeam(teamId)
            }
            bottomSheet.show(parentFragmentManager, "TEAM_DETAIL")
        }

        teamListRecyclerView.adapter = adapter
        teamListRecyclerView.layoutManager = LinearLayoutManager(context) // 어댑터 붙이기

        getTeamList(adapter)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 시 이전 프래그먼트로 돌아가기
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = parentFragmentManager
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // 옵저버 한 번만 등록
        joinTeamViewModel.joinTeamResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                if (response.success) {
                    Toast.makeText(requireContext(), "참가 요청을 했습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "참가 요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                // 이후 다시 들어왔을 때 중복 실행되지 않도록 null 처리
                joinTeamViewModel.clearJoinTeamResponse()
            }
        })
    }

    // 자식 프래그먼트 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container2, fragment) // 프래그먼트 교체, 메인 액티비티 프래그먼트 컨테이너 id
            .addToBackStack(null) // 뒤로가기 눌렀을 시 이전 프래그먼트로 돌아가기
            .commit()
    }

    // 팀 목록 조회 함수
    private fun getTeamList(adapter: TeamListAdapter) {
        teamListViewModel.getTeamList()

        teamListViewModel.teamList.observe(viewLifecycleOwner, Observer { teamList ->
            val teams = teamList.data ?: emptyList()

            if (teams.isNotEmpty() && teamList.success) { // 팀 목록이 있을 때
                noTeamsText.visibility = View.GONE
                teamListRecyclerView.visibility = View.VISIBLE

                // 리사이클러뷰 어댑터에 팀 목록 설정
                adapter.updateTeams(teams.map { TeamListItem(
                    it.leader,
                    it.teamId,
                    it.memberCount,
                    it.name,
                    it.description) })

            } else { // 팀 목록이 없을 때
                noTeamsText.visibility = View.VISIBLE
                teamListRecyclerView.visibility = View.GONE
            }
        })

        // message 옵저빙 (TextView 업데이트)
        teamListViewModel.message.observe(viewLifecycleOwner, Observer { message ->
            val gson = Gson()
            val jsonObject = gson.fromJson(message, JsonObject::class.java)

            val errorMessage = jsonObject.get("message").asString
            noTeamsText.text = errorMessage // TextView에 message 반영
            Log.d("PathRecordViewModel", "message: $errorMessage")
        })
    }

    private fun joinTeam(teamId: Int) {
        joinTeamViewModel.joinTeam(teamId) // 옵저빙 onViewCreated에서 한번만 실행(중첩 방지)
    }
}