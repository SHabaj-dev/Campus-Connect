package com.teamtechnojam.campusconnect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.teamtechnojam.campusconnect.databinding.HomeFeedListItemBinding
import com.teamtechnojam.campusconnect.model.ProfileUserModel

class UserProfileListAdapter(
    private val context: Context,
    private val usersList: List<ProfileUserModel>
) : RecyclerView.Adapter<UserProfileListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserProfileListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = HomeFeedListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserProfileListAdapter.ViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class ViewHolder(private val binding: HomeFeedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: ProfileUserModel) {
            binding.tvUserName.text = user.userName
            binding.tvCourseName.text = user.courseName
            binding.tvUniversityName.text = user.university
            Glide.with(context)
                .load(user.profileImage)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivProfilePicList)
            val skillsArray = user.skills?.split(",")
            binding.rvSkillsList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.rvSkillsList.adapter = skillsArray?.let { SkillsListAdapter(context, it) }
            binding.btnChat.setOnClickListener {
                //Go to chat Fragments
            }
            binding.btnViewProfile.setOnClickListener {
                Toast.makeText(context, "Btn View Profile is Clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}