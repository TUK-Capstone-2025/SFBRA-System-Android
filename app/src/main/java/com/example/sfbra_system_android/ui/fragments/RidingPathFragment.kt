package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.RidingRecord
import com.example.sfbra_system_android.data.RidingRecordAdapter
import com.example.sfbra_system_android.data.viewmodels.PathRecordViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject

// 주행 기록 화면
class RidingPathFragment : Fragment() {
    private val viewModel: PathRecordViewModel by viewModels()
    private lateinit var noRecordsText: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_riding_path, container, false)

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

        getRidingRecords(adapter)

        return view
    }

    // 주행 기록 불러오기 함수
    private fun getRidingRecords(adapter: RidingRecordAdapter) {
        viewModel.fetchPathRecords()

        // pathRecords 옵저빙 (주행 기록 데이터 변경 시)
        viewModel.pathRecords.observe(viewLifecycleOwner, Observer { pathRecords ->
            val records = pathRecords.data ?: emptyList() // null 방지

            if (records.isNotEmpty() && pathRecords.success) {
                noRecordsText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                adapter.updateRecords(records.map { RidingRecord("주행기록 ${it.id}", it.startTime) })
            } else {
                noRecordsText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })

        // message 옵저빙 (TextView 업데이트)
        viewModel.message.observe(viewLifecycleOwner, Observer { message ->
            val gson = Gson()
            val jsonObject = gson.fromJson(message, JsonObject::class.java)

            val errorMessage = jsonObject.get("message").asString
            noRecordsText.text = errorMessage // TextView에 message 반영
            Log.d("PathRecordViewModel", "message: $errorMessage")
        })
    }
}
