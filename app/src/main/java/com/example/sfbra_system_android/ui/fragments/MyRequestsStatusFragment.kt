package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.RequestStatus
import com.example.sfbra_system_android.data.RequestStatusAdapter
import com.example.sfbra_system_android.data.RequestStatusItem
import com.example.sfbra_system_android.data.viewmodels.RequestListViewModel

// 내 신청 관리 화면
class MyRequestsStatusFragment : Fragment() {
    private val requestListViewModel: RequestListViewModel by viewModels()
    private lateinit var requestStatusRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_requests_status, container, false)

        requestStatusRecyclerView = view.findViewById(R.id.request_status_recyclerview)

        val adapter = RequestStatusAdapter(emptyList()) { team ->
            Toast.makeText(context, "${team.teamName} 신청 취소 선택", Toast.LENGTH_SHORT).show()
            // todo 서버 요청 or 리스트 갱신
        }

        requestStatusRecyclerView.adapter = adapter
        requestStatusRecyclerView.layoutManager = LinearLayoutManager(context)

        showRequestsStatus(adapter)

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
    private fun showRequestsStatus(adapter: RequestStatusAdapter) {
        requestListViewModel.getRequestList()

        requestListViewModel.requestList.observe(viewLifecycleOwner, Observer { requestList ->
            requestList?.let {
                val list = requestList.data ?: emptyList()

                if (list.isNotEmpty() && requestList.success) {
                    // 신청 목록이 있을 때
                    val items = list.map { item ->
                        val status = when (item.status) {
                            "REJECTED" -> RequestStatus.REJECTED
                            "WAITING" -> RequestStatus.WAITING
                            "ACCEPTED" -> RequestStatus.ACCEPTED
                            else -> RequestStatus.WAITING // 기본값으로 처리
                        }
                        RequestStatusItem(item.teamName, status)
                    }

                    adapter.updateItems(items)
                } else {
                    // 신청 목록이 없을 때
                }
            }
        })
    }

    // 신청 취소 함수
    private fun cancelRequest() {

    }
}