package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.RidingRecord
import com.example.sfbra_system_android.RidingRecordAdapter
import com.example.sfbra_system_android.data.PathRecordViewModel

// 주행 기록 화면
class RidingPathFragment : Fragment() {
    private val viewModel: PathRecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_riding_path, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.riding_records_recycler_view)
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

        viewModel.pathRecords.observe(viewLifecycleOwner, Observer { pathRecords ->
            adapter.updateRecords(pathRecords.map { RidingRecord("주행기록 ${it.PathRecordId}", it.PathRecordDate) })
        })
    }
}
