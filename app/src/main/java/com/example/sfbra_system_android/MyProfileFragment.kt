package com.example.sfbra_system_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

// 내 프로필 화면
class MyProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        val changeAvatar = view.findViewById<TextView>(R.id.change_avatar)
        val changeNickname = view.findViewById<TextView>(R.id.change_nickname)
        val changeId = view.findViewById<TextView>(R.id.change_id)
        val changePassword = view.findViewById<TextView>(R.id.change_password)

        changeAvatar.setOnClickListener { changeAvatar() }
        changeNickname.setOnClickListener { changeNickname() }
        changeId.setOnClickListener { changeLoginID() }
        changePassword.setOnClickListener { changePassword() }

        getInformation()

        return view
    }

    // todo 각 버튼 클릭 리스너 구현
    private fun changeAvatar() {
        Toast.makeText(requireContext(), "프로필 사진 변경 클릭", Toast.LENGTH_SHORT).show()
    }

    private fun changeNickname() {
        Toast.makeText(requireContext(), "닉네임 변경 클릭", Toast.LENGTH_SHORT).show()
    }

    private fun changeLoginID() {
        Toast.makeText(requireContext(), "아이디 변경 클릭", Toast.LENGTH_SHORT).show()
    }

    private fun changePassword() {
        Toast.makeText(requireContext(), "비밀번호 변경 클릭", Toast.LENGTH_SHORT).show()
    }

    // todo 서버에서 정보 가져와 닉네임, id, 프로필사진 출력
    private fun getInformation() {

    }
}