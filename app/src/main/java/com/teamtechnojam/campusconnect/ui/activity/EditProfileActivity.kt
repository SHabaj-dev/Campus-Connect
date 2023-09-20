package com.teamtechnojam.campusconnect.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.ActivityEditProfileBinding
import com.teamtechnojam.campusconnect.model.ProfileUserModel
import com.teamtechnojam.campusconnect.ui.bottomSheet.AddCertificateBottomSheet

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val mAuth: FirebaseAuth
        get() = Firebase.auth

    private val firebaseDatabase: DatabaseReference
        get() = FirebaseDatabase.getInstance().reference

    private val certificateBottomSheet: AddCertificateBottomSheet
        get() = AddCertificateBottomSheet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackEditProfile.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        getDataFromFirebase()

        binding.btnAddCertifications.setOnClickListener {
            certificateBottomSheet.show(supportFragmentManager, certificateBottomSheet.tag)
        }


    }

    private fun getDataFromFirebase() {
        firebaseDatabase
            .child("Users")
            .child(mAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                val profileUserData = it.getValue(ProfileUserModel::class.java)
                if (profileUserData != null) {
                    updateUi(profileUserData)
                } else {
                    showToast(getString(R.string.error_msg))
                }
            }.addOnFailureListener {
                showToast(getString(R.string.error_msg))
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUi(profileUserData: ProfileUserModel) {
        binding.etUserName.setText(profileUserData.userName)
        binding.etAbout.setText(profileUserData.about)
        binding.etCourse.setText(profileUserData.courseName)
        binding.etSkills.setText(profileUserData.skills)
        binding.etPhoneNumber.setText(profileUserData.phoneNumber)
        binding.etCollageName.setText(profileUserData.university)

        Glide.with(this@EditProfileActivity)
            .load(profileUserData.profileImage)
            .placeholder(R.drawable.ic_person)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivUserProfilePicture)
    }
}