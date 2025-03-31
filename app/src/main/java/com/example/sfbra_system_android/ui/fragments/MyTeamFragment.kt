package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.viewmodels.TeamCheckViewModel

// 내 팀 화면
class MyTeamFragment : Fragment() {
    // 프래그먼트 저장용 맵
    private val fragmentMap = mutableMapOf<String, Fragment>()
    private var activeFragment: Fragment? = null
    private val teamCheckViewModel: TeamCheckViewModel by viewModels()
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var errorLayout: LinearLayout
    private lateinit var retryButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_team, container, false)
        fragmentContainer = view.findViewById(R.id.fragment_container2)
        errorLayout = view.findViewById(R.id.error_layout)
        retryButton = view.findViewById(R.id.retry_button)

        checkMyTeam()

        retryButton.setOnClickListener {
            checkMyTeam()
            errorLayout.visibility = View.GONE
        }

        return view
    }

    // 사용자 팀 유무 체크 함수
    private fun checkMyTeam() {
        teamCheckViewModel.hasTeam()

        teamCheckViewModel.hasTeam.observe(viewLifecycleOwner) { response ->
            if (response != null && response.success) {
                if (response.data.isInTeam) {
                    switchFragment(MyTeamInfoFragment(), "MY_TEAM")
                } else {
                    switchFragment(NoTeamActionFragment(), "NO_TEAM")
                }
            }
            else {
                fragmentContainer.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
            }
        }
    }

    // 프래그먼트 교체 함수
    fun switchFragment(fragment: Fragment, tag: String) {
        if (activeFragment?.tag == tag) return

        val transaction = childFragmentManager.beginTransaction()
        
        // 기존 프래그먼트 숨기기
        activeFragment?.let { transaction.hide(it) }

        // 새 프래그먼트 가져오기 (기존 프래그먼트가 있으면 재사용)
        val newFragment = fragmentMap[tag] ?: fragment.also { fragmentMap[tag] = it }

        // 프래그먼트 추가 또는 보여주기
        if (!newFragment.isAdded) {
            transaction.add(R.id.fragment_container2, newFragment, tag)
        } else {
            transaction.show(newFragment)
        }

        transaction.commit()
        activeFragment = newFragment
    }
}