package com.example.sfbra_system_android.ui.fragments

import android.app.AlertDialog
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
import com.example.sfbra_system_android.data.RequestStatus
import com.example.sfbra_system_android.data.RequestStatusAdapter
import com.example.sfbra_system_android.data.RequestStatusItem
import com.example.sfbra_system_android.data.viewmodels.DeleteRequestViewModel
import com.example.sfbra_system_android.data.viewmodels.RequestListViewModel

// 내 신청 관리 화면
class MyRequestsStatusFragment : Fragment() {
    private val requestListViewModel: RequestListViewModel by viewModels()
    private val deleteRequestViewModel: DeleteRequestViewModel by viewModels()
    private lateinit var requestStatusRecyclerView: RecyclerView // 신청 팀 목록 리사이클러뷰
    private lateinit var noRequests: TextView // 신청 목록 없을때 텍스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_requests_status, container, false)

        noRequests = view.findViewById(R.id.no_requests_text)
        requestStatusRecyclerView = view.findViewById(R.id.request_status_recyclerview)

        val adapter = RequestStatusAdapter(emptyList()) {
            // 취소 버튼 눌렀을 경우
            confirmCancelRequest()
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
                val fragmentManager = requireActivity().supportFragmentManager
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
                    requestStatusRecyclerView.visibility = View.VISIBLE
                    noRequests.visibility = View.GONE

                    val items = list.map { item ->
                        //Log.d("MyRequestsStatusFragment", "status: ${item.status}")
                        val status = when (item.status) {
                            "REJECT" -> RequestStatus.REJECTED
                            "PENDING" -> RequestStatus.WAITING
                            "APPROVE" -> RequestStatus.ACCEPTED
                            else -> RequestStatus.WAITING // 기본값으로 처리
                        }
                        RequestStatusItem(item.teamName, status)
                    }

                    adapter.updateItems(items)
                } else {
                    // 신청 목록이 없을 때
                    requestStatusRecyclerView.visibility = View.GONE
                    noRequests.visibility = View.VISIBLE
                }
            }
        })
    }

    // 취소 요청 팝업 생성
    private fun confirmCancelRequest() {
        AlertDialog.Builder(requireContext())
            .setTitle("신청 취소")
            .setMessage("신청을 취소하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                cancelRequest() // 취소 요청
            }
            .setNegativeButton("아니오", null)
            .show()
    }


    // 신청 취소 함수
    private fun cancelRequest() {
        deleteRequestViewModel.deleteRequest()

        deleteRequestViewModel.deleteRequestResponse.observe(viewLifecycleOwner, Observer { deleteResponse ->
            if (deleteResponse != null && deleteResponse.success) {
                Toast.makeText(context, "신청이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "신청 취소에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}