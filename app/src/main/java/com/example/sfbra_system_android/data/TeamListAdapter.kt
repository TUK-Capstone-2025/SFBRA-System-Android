package com.example.sfbra_system_android.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.services.TeamListItem

// 리사이클러뷰 어댑터 -> 팀목록 동적표시
class TeamListAdapter(
    private var teamList: List<TeamListItem>,
    private val onItemClick: (TeamListItem) -> Unit
) : RecyclerView.Adapter<TeamListAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamNameTextView: TextView = itemView.findViewById(R.id.team_name)

        fun bind(team: TeamListItem) {
            teamNameTextView.text = team.name

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(teamList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_list, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teamList[position])
    }

    override fun getItemCount(): Int = teamList.size

    fun updateTeams(newTeams: List<TeamListItem>) {
        teamList = newTeams
        notifyDataSetChanged() // 데이터 갱신
    }
}