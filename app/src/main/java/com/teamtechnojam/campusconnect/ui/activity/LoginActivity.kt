package com.teamtechnojam.campusconnect.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private var verificationId: String? = null
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog
    private final val TAG = "LOGIN_ACTIVITY"
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        dialog = Dialog(this@LoginActivity)
        dialog.setContentView(R.layout.loading_pop_up)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            dialog.show()
            val userName = binding.etUserName.text.toString()
            val userPassword = binding.etUserPassword.text.toString()

            if (userName.isEmpty()) {
                binding.tilUserName.boxStrokeColor = resources.getColor(R.color.red)
                Toast.makeText(this@LoginActivity, "Please Enter User Name.", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            } else if (userPassword.isEmpty()) {
                binding.tilUserPassword.boxStrokeColor = resources.getColor(R.color.red)
                Toast.makeText(this@LoginActivity, "Please Enter your password", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            } else {
                checkAuthentication(userName, userPassword)
            }

        }

        binding.btnSignUpWithGoogle.setOnClickListener {
            loginWithGoogle()
        }

    }

    private fun loginWithGoogle() {
        //yet to implement
    }

    private fun checkAuthentication(userName: String, userPassword: String) {
        val userReference = FirebaseDatabase.getInstance().getReference("users")

        userReference.orderByChild("userName").equalTo(userName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userId = dataSnapshot.children.first().key
                        if (userId != null) {
                            val storedPhoneNumber = dataSnapshot
                                .child(userId)
                                .child("phoneNumber")
                                .getValue(String::class.java)


                            val storedPassword = dataSnapshot
                                .child(userId)
                                .child("password")
                                .getValue(String::class.java)

                            Log.d(TAG, "onDataChange: $storedPhoneNumber $storedPassword")

                            if (userName == binding.etUserName.text.toString() &&
                                storedPassword == binding.etUserPassword.text.toString()
                            ) {
                                if (storedPhoneNumber != null) {
                                    startPhoneNumberVerification(storedPhoneNumber)
                                }
                            } else {
                                showToastMessage("Please check your Login Details.")
                            }

//                            val credentials = PhoneAuthProvider.getCredential(
//                                storedPhoneNumber.toString(),
//                                userPassword
//                            )
//                            signInWithPhoneCredentials(credentials)
                        }
                    } else {
                        showToastMessage("User not found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }
            })
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    showToastMessage("Verification failed: ${e.message}")
                    Log.d(TAG, "onVerificationFailed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@LoginActivity.verificationId = verificationId
                    startActivity(
                        Intent(this@LoginActivity, VerifyOtpActivity::class.java)
                            .putExtra("verificationId", verificationId)
                            .putExtra("phoneNumber", phoneNumber)
                    )
                    dialog.dismiss()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    /*private fun signInWithPhoneCredentials(credentials: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credentials)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                    )
                    finishAffinity()
                } else {

                    showToastMessage("Login failed due to: ${task.exception?.message}")
                    Log.d(TAG, "onDataChange: ${task.exception?.message}")
                }

            }
    }*/


    private fun showToastMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

}