package com.teamtechnojam.campusconnect.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val mAuth = Firebase.auth
        currentUser = mAuth.currentUser
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserData()


    }

    private fun setUserData() {
        if (currentUser != null) {
            val userName = currentUser!!.displayName.toString().split(" ")
            binding.tvUserName.text = userName[0]
            val photoUrl = currentUser!!.photoUrl.toString()

            val circularProgressBar = CircularProgressDrawable(requireContext())
            circularProgressBar.strokeWidth = 5f
            circularProgressBar.centerRadius = 30f
            circularProgressBar.start()


            Glide.with(requireContext())
                .load(photoUrl)
                .placeholder(circularProgressBar)
                .into(binding.ivUserProfilePic)
        } else {
            Log.d("HOMEFRAGMENT", "setUserData: fetching Failed.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}