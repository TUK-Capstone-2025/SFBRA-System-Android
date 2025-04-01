package com.example.sfbra_system_android.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R

data class RequestStatusItem(
    val teamName: String,
    val status: RequestStatus
)

enum class RequestStatus {
    WAITING, REJECTED, ACCEPTED
}

class RequestStatusAdapter(
    private val items: List<RequestStatusItem>,
    private val onCancelClick: (RequestStatusItem) -> Unit
) : RecyclerView.Adapter<RequestStatusAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teamName: TextView = view.findViewById(R.id.team_name)
        val statusText: TextView = view.findViewById(R.id.request_status)
        val cancelButton: ImageView = view.findViewById(R.id.cancel_button)
        val justSpace: ImageView = view.findViewById(R.id.just_space)

        fun bind(item: RequestStatusItem) {
            teamName.text = item.teamName

            when (item.status) {
                RequestStatus.REJECTED -> {
                    statusText.text = itemView.context.getString(R.string.status_reject)
                    statusText.setTextColor(ContextCompat.getColor(itemView.context, R.color.status_reject))
                    cancelButton.visibility = View.GONE
                    justSpace.visibility = View.VISIBLE
                }
                RequestStatus.WAITING -> {
                    statusText.text = itemView.context.getString(R.string.status_waiting)
                    statusText.setTextColor(ContextCompat.getColor(itemView.context, R.color.status_waiting))
                    cancelButton.visibility = View.VISIBLE
                    justSpace.visibility = View.GONE
                    cancelButton.setOnClickListener {
                        onCancelClick(item)
                    }
                }
                RequestStatus.ACCEPTED -> {
                    statusText.text = itemView.context.getString(R.string.status_accept)
                    statusText.setTextColor(ContextCompat.getColor(itemView.context, R.color.status_accept))
                    cancelButton.visibility = View.GONE
                    justSpace.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
