package com.teamtechnojam.campusconnect.ui.fragments.profile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.FragmentProfileBinding
import com.teamtechnojam.campusconnect.model.ProfileUserModel
import com.teamtechnojam.campusconnect.ui.activity.LoginActivity
import com.teamtechnojam.campusconnect.ui.customUIComponents.LoadingDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var loadingDialog: Dialog
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var profileUserData: ProfileUserModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        firebaseAuth = Firebase.auth
        loadingDialog = LoadingDialog.getLoadingDialog(requireContext())
        loadingDialog.show()
        getCurrentUserFromFirebase()
        return binding.root
    }

    private fun getCurrentUserFromFirebase() {
        val currentUser = firebaseAuth.currentUser?.uid
        val firebaseDatabase = FirebaseDatabase.getInstance().reference

        firebaseDatabase.child("Users").child(currentUser.toString()).get().addOnSuccessListener {

            profileUserData = it.getValue(ProfileUserModel::class.java)!!
            if (profileUserData != null) {
                binding.tvUserEmail.text = firebaseAuth.currentUser?.email.toString()
                binding.tvUserCollage.text = profileUserData.university.toString()
                binding.tvUserCourse.text = profileUserData.courseName.toString()
                binding.tvUserSkills.text = profileUserData.skills.toString()
                binding.tvUserAbout.text = profileUserData.about.toString()
                binding.tvUserNameProfile.text = profileUserData.userName.toString()
                Glide.with(requireContext())
                    .load(profileUserData.profileImage.toString())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(requireContext().getDrawable(R.drawable.ic_person))
                    .into(binding.ivUserProfilePic)
                loadingDialog.dismiss()
            }
        }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_msg),
                    Toast.LENGTH_SHORT
                ).show()
                loadingDialog.dismiss()
//                activity?.onBackPressedDispatcher?.onBackPressed()
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            loadingDialog.show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            loadingDialog.dismiss()
            activity?.finishAffinity()
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}