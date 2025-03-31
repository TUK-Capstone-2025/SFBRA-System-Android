package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
            replaceFragment(MyRequestsStatusFragment())
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
}