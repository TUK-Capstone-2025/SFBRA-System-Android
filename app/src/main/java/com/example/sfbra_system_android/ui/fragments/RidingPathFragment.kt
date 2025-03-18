package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.RidingRecord
import com.example.sfbra_system_android.RidingRecordAdapter

// 주행 기록 화면
class RidingPathFragment : Fragment() {

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

        recyclerView.adapter = RidingRecordAdapter(getRidingRecords())

        return view
    }

    // 주행 기록 더미 데이터
    private fun getRidingRecords(): List<RidingRecord> {
        return listOf(
            RidingRecord("주행기록1", "2024.11.25"),
            RidingRecord("주행기록2", "2024.11.30"),
            RidingRecord("주행기록3", "2024.12.03"),
            RidingRecord("주행기록4", "2025.12.22"),
            RidingRecord("주행기록5", "2025.01.05")
        )
    }
}
