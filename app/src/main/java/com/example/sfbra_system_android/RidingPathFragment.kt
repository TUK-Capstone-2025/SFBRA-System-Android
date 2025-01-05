package com.example.sfbra_system_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RidingPathFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
