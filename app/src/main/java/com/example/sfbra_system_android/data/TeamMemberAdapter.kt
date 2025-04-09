package com.example.sfbra_system_android.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfbra_system_android.R

class TeamMemberAdapter(
    private var members: List<TeamMember>,
    private val onViewProfile: (Int) -> Unit,
    private val onViewRecord: (TeamMember) -> Unit,
    private val onKickMember: (TeamMember) -> Unit,
    private var currentUserIsLeader: Boolean
) : RecyclerView.Adapter<TeamMemberAdapter.TeamMemberViewHolder>() {

    inner class TeamMemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val more_button = itemView.findViewById<ImageView>(R.id.more_button)
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

        holder.more_button.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.member_options_menu, popup.menu)

            // 팀장만 강퇴 버튼 보이게
            if (!currentUserIsLeader) {
                popup.menu.findItem(R.id.action_kick).isVisible = false
            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_view_profile -> {
                        onViewProfile(member.userId)
                        true
                    }
                    R.id.action_view_record -> {
                        onViewRecord(member)
                        true
                    }
                    R.id.action_kick -> {
                        onKickMember(member)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

    }

    override fun getItemCount(): Int = members.size

    fun updateMembers(newMembers: List<TeamMember>) {
        this.members = newMembers
        notifyDataSetChanged()
    }

    // 리더 상태 동적 갱신 함수
    fun updateLeaderStatus(isLeader: Boolean) {
        currentUserIsLeader = isLeader
        notifyDataSetChanged() // 모든 아이템 갱신
    }
}

data class TeamMember(
    val userId: Int,
    val name: String,
    val isLeader: Boolean = false,
    val rank: Int = -1 // 1부터 시작 (0 이하이면 순위 없음 처리)
)
