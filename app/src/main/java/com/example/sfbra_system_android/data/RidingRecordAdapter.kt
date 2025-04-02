package com.example.sfbra_system_android.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R

// 리사이클러뷰 어댑터 -> 주행기록 동적표시
class RidingRecordAdapter(private var records: List<RidingRecord>) :
    RecyclerView.Adapter<RidingRecordAdapter.RecordViewHolder>() {

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recordId: TextView = itemView.findViewById(R.id.record_id)
        val recordDate: TextView = itemView.findViewById(R.id.record_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riding_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.recordId.text = record.id
        holder.recordDate.text = record.date
    }

    override fun getItemCount(): Int = records.size

    fun updateRecords(newRecords: List<RidingRecord>) {
        records = newRecords
        notifyDataSetChanged() // 리스트 갱신
    }
}

data class RidingRecord(val id: String, val date: String)
// todo 나중에 PathRecordData로 사용하고 RidingRecord은 지울것. 어댑터 내부에서 string으로 변경하면 됨.