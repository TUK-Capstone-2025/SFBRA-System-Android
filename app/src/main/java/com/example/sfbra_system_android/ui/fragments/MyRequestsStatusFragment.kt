package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.RequestStatus
import com.example.sfbra_system_android.data.RequestStatusAdapter
import com.example.sfbra_system_android.data.RequestStatusItem

// 내 신청 관리 화면
class MyRequestsStatusFragment : Fragment() {
    private lateinit var requestStatusRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_requests_status, container, false)

        requestStatusRecyclerView = view.findViewById(R.id.request_status_recyclerview)

        // todo 임시 데이터
        val sampleData = listOf(
            RequestStatusItem("멋있는 팀", RequestStatus.REJECTED),
            RequestStatusItem("어지러운 팀", RequestStatus.WAITING)
        )

        val adapter = RequestStatusAdapter(sampleData) { item ->
            Toast.makeText(context, "${item.teamName} 신청 취소", Toast.LENGTH_SHORT).show()
            // todo 서버 요청 or 리스트 갱신
        }

        requestStatusRecyclerView.adapter = adapter
        requestStatusRecyclerView.layoutManager = LinearLayoutManager(context)

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

    // 신청 상태 표시 함수
    private fun showRequestsStatus() {

    }

    // 신청 취소 함수
    private fun cancelRequest() {

    }
}