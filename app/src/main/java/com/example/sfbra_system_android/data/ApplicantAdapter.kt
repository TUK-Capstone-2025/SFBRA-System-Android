package com.example.sfbra_system_android.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R

class ApplicantAdapter(
    private var applicants: List<Applicant>,
    private val onAcceptClick: (Int) -> Unit, // id만 전달
    private val onRejectClick: (Int) -> Unit
) : RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder>() {

    inner class ApplicantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.applicant_name)
        private val acceptButton: Button = itemView.findViewById(R.id.accept_button)
        private val rejectButton: Button = itemView.findViewById(R.id.reject_button)

        fun bind(applicant: Applicant) {
            nameText.text = applicant.nickname

            acceptButton.setOnClickListener {
                onAcceptClick(applicant.id)
            }

            rejectButton.setOnClickListener {
                onRejectClick(applicant.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_applicant, parent, false)
        return ApplicantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        holder.bind(applicants[position])
    }

    override fun getItemCount(): Int = applicants.size

    fun updateItems(newItems: List<Applicant>) {
        applicants = newItems
        notifyDataSetChanged()
    }
}

data class Applicant(
    val id: Int,
    val nickname: String
)