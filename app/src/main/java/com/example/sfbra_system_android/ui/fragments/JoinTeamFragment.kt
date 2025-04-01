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
import com.example.sfbra_system_android.data.services.TeamListItem
import com.example.sfbra_system_android.data.viewmodels.TeamListViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject

// 팀 참가하기 화면
class JoinTeamFragment : Fragment() {
    private val teamListViewModel: TeamListViewModel = TeamListViewModel()
    private lateinit var myRequestManagement: TextView
    private lateinit var teamListRecyclerView: RecyclerView
    private lateinit var noTeamsText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_join_team, container, false)

        noTeamsText = view.findViewById(R.id.no_teams_text)
        myRequestManagement = view.findViewById(R.id.my_request_management)
        myRequestManagement.setOnClickListener {
            replaceFragment(MyRequestsStatusFragment())
        }

        teamListRecyclerView = view.findViewById(R.id.team_list_recyclerview)

        val adapter = TeamListAdapter(emptyList()) { teamName ->
            Toast.makeText(context, "$teamName 클릭됨", Toast.LENGTH_SHORT).show()
            // todo 클릭 시 원하는 동작
        }

        teamListRecyclerView.adapter = adapter
        teamListRecyclerView.layoutManager = LinearLayoutManager(context)

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
    }

    // 자식 프래그먼트 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container2, fragment) // 프래그먼트 교체, 메인 액티비티 프래그먼트 컨테이너 id
            .addToBackStack(null) // 뒤로가기 눌렀을 시 이전 프래그먼트로 돌아가기
            .commit()
    }

    private fun getTeamList(adapter: TeamListAdapter) {
        teamListViewModel.getTeamList()

        teamListViewModel.teamList.observe(viewLifecycleOwner, Observer { teamList ->
            val teams = teamList.data ?: emptyList()

            if (teams.isNotEmpty() && teamList.success) {
                noTeamsText.visibility = View.GONE
                teamListRecyclerView.visibility = View.VISIBLE
                adapter.updateTeams(teams.map { TeamListItem(
                    it.leader,
                    it.teamId,
                    it.memberCount,
                    it.name,
                    it.description) })

            } else {
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
}