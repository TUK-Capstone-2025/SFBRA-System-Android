package com.example.sfbra_system_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 리사이클러뷰 어댑터 -> 주행기록 동적표시
class RidingRecordAdapter(private val records: List<RidingRecord>) :
    RecyclerView.Adapter<RidingRecordAdapter.RecordViewHolder>() {

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recordName: TextView = itemView.findViewById(R.id.record_name)
        val recordDate: TextView = itemView.findViewById(R.id.record_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riding_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.recordName.text = record.name
        holder.recordDate.text = record.date
    }

    override fun getItemCount(): Int = records.size
}

data class RidingRecord(val name: String, val date: String)