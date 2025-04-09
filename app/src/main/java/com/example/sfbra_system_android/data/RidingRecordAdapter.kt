package com.example.sfbra_system_android.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R

// 리사이클러뷰 어댑터 -> 주행기록 동적표시
class RidingRecordAdapter(
    private var records: List<RidingRecord>,
    private val onItemClick: (Int) -> Unit // 클릭 시 id 넘기기
) : RecyclerView.Adapter<RidingRecordAdapter.RecordViewHolder>() {

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recordIndex: TextView = itemView.findViewById(R.id.record_index)
        val recordDate: TextView = itemView.findViewById(R.id.record_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riding_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.recordIndex.text = record.index
        holder.recordDate.text = record.date

        // 아이템 클릭 시
        holder.itemView.setOnClickListener {
            onItemClick(record.id)
        }
    }

    override fun getItemCount(): Int = records.size

    fun updateRecords(newRecords: List<RidingRecord>) {
        records = newRecords
        notifyDataSetChanged() // 리스트 갱신
    }
}

data class RidingRecord(val id: Int, val index: String, val date: String)