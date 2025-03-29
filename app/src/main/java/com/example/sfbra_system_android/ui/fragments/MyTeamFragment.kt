package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sfbra_system_android.R

// 내 팀 화면
class MyTeamFragment : Fragment() {
    // 프래그먼트 저장용 맵
    private val fragmentMap = mutableMapOf<String, Fragment>()
    private var activeFragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_team, container, false)

        //switchFragment(NoTeamActionFragment(), "NO_TEAM")
        //switchFragment(MyTeamInfoFragment(), "MY_TEAM")
        checkMyTeam()

        return view
    }

    // 사용자 팀 유무 체크 함수
    private fun checkMyTeam() {

    }

    // 프래그먼트 교체 함수
    private fun switchFragment(fragment: Fragment, tag: String) {
        if (activeFragment?.tag == tag) return

        val transaction = childFragmentManager.beginTransaction()
        
        // 기존 프래그먼트 숨기기
        activeFragment?.let { transaction.hide(it) }

        // 새 프래그먼트 가져오기 (기존 프래그먼트가 있으면 재사용)
        val newFragment = fragmentMap[tag] ?: fragment.also { fragmentMap[tag] = it }

        // 프래그먼트 추가 또는 보여주기
        if (!newFragment.isAdded) {
            transaction.add(R.id.fragment_container, newFragment, tag)
        } else {
            transaction.show(newFragment)
        }

        transaction.commit()
        activeFragment = newFragment
    }
}