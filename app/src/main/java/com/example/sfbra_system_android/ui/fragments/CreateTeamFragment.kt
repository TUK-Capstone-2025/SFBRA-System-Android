package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.viewmodels.CreateTeamViewModel

// 팀 생성하기 화면
class CreateTeamFragment : Fragment() {
    private val createTeamViewModel: CreateTeamViewModel by viewModels()
    private lateinit var createButton: Button
    private lateinit var name_input: EditText
    private lateinit var info_input: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_team, container, false)

        name_input = view.findViewById(R.id.team_name_input)
        info_input = view.findViewById(R.id.team_intro_input)

        createButton = view.findViewById(R.id.create_button)
        createButton.setOnClickListener {
            createTeam()
        }

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

    // 팀 생성 요청 함수
    private fun createTeam() {
        val name = name_input.text.toString()
        val description = info_input.text.toString()

        createTeamViewModel.createTeam(name, description)

        createTeamViewModel.createTeamResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.success) {
                // 팀 생성 성공
                parentFragmentManager.popBackStack() // 현재 프래그먼트 제거(백스텍 제거)

                (parentFragment as? MyTeamFragment)?.switchFragment(MyTeamInfoFragment(), "MY_TEAM") // 프래그먼트 교체
            }
            else {
                // 팀 생성 실패
                Toast.makeText(requireContext(), "팀 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}