package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.RidingRecord
import com.example.sfbra_system_android.data.RidingRecordAdapter
import com.example.sfbra_system_android.data.TeamMember
import com.example.sfbra_system_android.data.viewmodels.PathRecordViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject

// 주행 기록 화면
class RidingPathFragment : Fragment() {
    private val pathRecordViewModel: PathRecordViewModel by viewModels()
    private lateinit var noRecordsText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var titleText: TextView
    private var memberId: Int = -1
    private var memberName: String? = null

    companion object {
        private const val ARG_MEMBER_ID = "member_id" // 나중에 전달받기 위한 키
        private const val ARG_MEMBER_NAME = "member_name"

        // 이전 프래그먼트에서 값들을 전달받아 새로운 인스턴스 생성
        fun newInstance(memberId: Int, memberName: String): RidingPathFragment {
            val fragment = RidingPathFragment()
            val args = Bundle().apply {
                putInt(ARG_MEMBER_ID, memberId)
                putString(ARG_MEMBER_NAME, memberName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { // 프래그먼트 생성 시 전달받은 값들 가져오기
            memberId = it.getInt(ARG_MEMBER_ID)
            memberName = it.getString(ARG_MEMBER_NAME)
        }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_riding_path, container, false)

        titleText = view.findViewById(R.id.title)
        noRecordsText = view.findViewById(R.id.no_records_text)
        recyclerView = view.findViewById(R.id.riding_records_recycler_view)
        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(context)

        // DividerItemDecoration 추가
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        val adapter = RidingRecordAdapter(emptyList()) // 초기 데이터 비워두기
        recyclerView.adapter = adapter

        // 내 기록인지 멤버 기록인지 구분하여 내용 변경
        if (memberId != -1) {
            titleText.text = "${memberName}의 주행기록"
            getMemberRecords(adapter, memberId)
        } else {
            titleText.text = "나의 주행기록"
            getRidingRecords(adapter)
        }

        return view
    }

    // 주행 기록 불러오기 함수
    private fun getRidingRecords(adapter: RidingRecordAdapter) {
        pathRecordViewModel.getPathRecords()

        // pathRecords 옵저빙 (주행 기록 데이터 변경 시)
        pathRecordViewModel.pathRecords.observe(viewLifecycleOwner, Observer { pathRecords ->
            val records = pathRecords.data ?: emptyList() // null 방지

            if (records.isNotEmpty() && pathRecords.success) {
                noRecordsText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                var index = 1

                adapter.updateRecords(records.map { RidingRecord(it.id, "주행기록 ${index}", it.startTime) })
            } else {
                noRecordsText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })

        // message 옵저빙 (TextView 업데이트)
        pathRecordViewModel.message1.observe(viewLifecycleOwner, Observer { message ->
            val gson = Gson()
            val jsonObject = gson.fromJson(message, JsonObject::class.java)

            val errorMessage = jsonObject.get("message").asString
            noRecordsText.text = errorMessage // TextView에 message 반영
            Log.d("PathRecordViewModel", "message: $errorMessage")
        })
    }

    private fun getMemberRecords(adapter: RidingRecordAdapter, memberId: Int) {
        pathRecordViewModel.getMemberPathRecords(memberId)

        // pathRecords 옵저빙 (주행 기록 데이터 변경 시)
        pathRecordViewModel.memberPathRecords.observe(viewLifecycleOwner, Observer { pathRecords ->
            val records = pathRecords.data ?: emptyList() // null 방지

            if (records.isNotEmpty() && pathRecords.success) {
                noRecordsText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                var index = 1

                adapter.updateRecords(records.map { RidingRecord(it.id, "주행기록 ${index}", it.startTime) })
            } else {
                noRecordsText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })

        // message 옵저빙 (TextView 업데이트)
        pathRecordViewModel.message2.observe(viewLifecycleOwner, Observer { message ->
            val gson = Gson()
            val jsonObject = gson.fromJson(message, JsonObject::class.java)

            val errorMessage = jsonObject.get("message").asString
            noRecordsText.text = errorMessage // TextView에 message 반영
            Log.d("PathRecordViewModel", "message: $errorMessage")
        })
    }
}
