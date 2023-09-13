package com.teamtechnojam.campusconnect.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var circularProgressBar: CircularProgressDrawable

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

        binding.rvUsersList.layoutManager = LinearLayoutManager(requireContext())


    }

    private fun setUserData() {
        if (currentUser != null) {
            val userName = currentUser!!.displayName.toString().split(" ")
            binding.tvUserName.text = userName[0]
            var photoUrl = ""

            val ref = FirebaseDatabase.getInstance().getReference("Users")

            ref.child(currentUser!!.uid).child("profileImage")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        photoUrl = snapshot.value.toString()
                        Log.d("IMAGE_URL", "onDataChange: $photoUrl")
                        Glide.with(requireContext())
                            .load(photoUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .placeholder(requireContext().getDrawable(R.drawable.ic_person))
                            .into(binding.ivUserProfilePic)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                    }

                })


        } else {
            Log.d("HOMEFRAGMENT", "setUserData: fetching Failed.")
        }
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/
}