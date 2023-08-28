package com.teamtechnojam.campusconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.databinding.ActivitySignUpBinding
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userName: String
    private lateinit var phoneNumber: String
    private lateinit var countryCode: String
    private lateinit var firebaseAuth: FirebaseAuth
    private var verificationId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        firebaseAuth = Firebase.auth

        if (savedInstanceState != null) {
            userName = savedInstanceState.getString("userName").toString()
            phoneNumber = savedInstanceState.getString("phoneNo").toString()
            countryCode = savedInstanceState.getString("countryCode").toString()
        }

        binding.btnBackSignUp.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            userName = binding.etUserName.text.toString()
            countryCode = binding.etCountryCode.text.toString()
            phoneNumber = binding.etPhoneNumber.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (userName.isEmpty()) {
                showToastMessage("User Name can't be empty")
            } else if (phoneNumber.isEmpty() || phoneNumber.length != 10) {
                showToastMessage("Please check phone number!!")
            } else if (newPassword.isEmpty()) {
                showToastMessage("Password can't be empty")
            } else if (newPassword.length < 8) {
                showToastMessage("Password too short!!")
            } else if (newPassword != confirmPassword) {
                showToastMessage("Password didn't match. Please check again")
            } else {
                val fullPhoneNumber = countryCode + phoneNumber
                startPhoneNumberVerification(fullPhoneNumber)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userName", userName)
        outState.putString("phoneNo", phoneNumber)
        outState.putString("countryCode", countryCode)
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
        Log.d("SIGNUP_ACTIVITY", "showToastMessage: $message")
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback is triggered if the verification is done automatically
                    // You can sign in the user here if required
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    showToastMessage("Verification failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Store the verification ID and move to VerifyOtpActivity
                    this@SignUpActivity.verificationId = verificationId
                    startActivity(
                        Intent(this@SignUpActivity, VerifyOtpActivity::class.java)
                            .putExtra("verificationId", verificationId)
                            .putExtra("phoneNumber", phoneNumber)
                    )
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}