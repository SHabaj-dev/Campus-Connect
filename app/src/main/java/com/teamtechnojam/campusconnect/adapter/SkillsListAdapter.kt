package com.teamtechnojam.campusconnect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamtechnojam.campusconnect.databinding.SkillsListItemBinding

class SkillsListAdapter(
    private val context: Context,
    private val skillList: List<String>
) : RecyclerView.Adapter<SkillsListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: SkillsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(skill: String) {
            binding.tvSkillName.text = skill
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkillsListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = SkillsListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(skillList[position])
    }

}
