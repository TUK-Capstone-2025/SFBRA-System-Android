package com.example.sfbra_system_android.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R

class TeamMemberAdapter(
    private var members: List<TeamMember>
) : RecyclerView.Adapter<TeamMemberAdapter.TeamMemberViewHolder>() {

    inner class TeamMemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leaderIcon: ImageView = view.findViewById(R.id.leader_icon)
        val topNumber: TextView = view.findViewById(R.id.top_number)
        val normalNumber: TextView = view.findViewById(R.id.nomal_number)
        val memberName: TextView = view.findViewById(R.id.member_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_member, parent, false)
        return TeamMemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamMemberViewHolder, position: Int) {
        val member = members[position]
        val context = holder.itemView.context

        // 닉네임 설정
        holder.memberName.text = member.name

        // 리더인 경우
        if (member.isLeader) {
            holder.leaderIcon.visibility = View.VISIBLE
            holder.topNumber.visibility = View.GONE
            holder.normalNumber.visibility = View.GONE
        } else {
            holder.leaderIcon.visibility = View.GONE

            // 1~3등인 경우
            if (member.rank in 1..3) {
                holder.topNumber.visibility = View.VISIBLE
                holder.normalNumber.visibility = View.GONE
                holder.topNumber.text = member.rank.toString()

                val topDrawable = when (member.rank) {
                    1 -> R.drawable.bg_rank_number_1
                    2 -> R.drawable.bg_rank_number_2
                    3 -> R.drawable.bg_rank_number_3
                    else -> R.drawable.bg_rank_number_1
                }
                holder.topNumber.setBackgroundResource(topDrawable)
            } else {
                // 4등 이후
                holder.topNumber.visibility = View.GONE
                holder.normalNumber.visibility = View.VISIBLE
                holder.normalNumber.text = member.rank.toString()
                holder.normalNumber.setBackgroundResource(R.drawable.bg_rank_normal_number)
            }
        }
    }

    override fun getItemCount(): Int = members.size

    fun updateMembers(newMembers: List<TeamMember>) {
        this.members = newMembers
        notifyDataSetChanged()
    }

}


data class TeamMember(
    val userId: Int,
    val name: String,
    val isLeader: Boolean = false,
    val rank: Int = -1 // 1부터 시작 (0 이하이면 순위 없음 처리)
)
