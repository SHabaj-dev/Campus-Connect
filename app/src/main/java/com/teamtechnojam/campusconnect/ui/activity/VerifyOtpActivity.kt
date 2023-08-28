package com.teamtechnojam.campusconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.databinding.ActivityVerifyOtpBinding
import java.util.concurrent.TimeUnit

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var verificationId: String
    private lateinit var phoneNumber: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var resendCountDownTimer: CountDownTimer
    private var isResendEnabled = true
    private var timeLeftMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        verificationId = intent.getStringExtra("verificationId") ?: ""
        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""

        binding.tvPhoneNumber.text = phoneNumber
        startResendTimer()

        binding.btnVerifyOtp.setOnClickListener {
            val otpCode = binding.pvOtp.text.toString()
            if (otpCode.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
                signInWithPhoneAuthCredential(credential)
            } else {
                showToastMessage("Please enter the OTP code.")
            }
        }
        binding.tvResend.setOnClickListener {
            if (isResendEnabled) {
                // Resend the OTP
                resendVerificationCode(phoneNumber, resendToken)
                isResendEnabled = false
                startResendTimer()
            }
        }


    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60, // Timeout duration
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval of OTP completed
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    showToastMessage("Verification failed: ${exception.message}")
                }

                override fun onCodeSent(
                    newVerificationId: String,
                    newResendToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = newVerificationId
                    resendToken = newResendToken
                    showToastMessage("Verification code sent again.")
                }
            },
            token
        )
    }

    private fun startResendTimer() {
        timeLeftMillis = TimeUnit.MINUTES.toMillis(1) // Set the timer for 1 minute
        resendCountDownTimer = object : CountDownTimer(timeLeftMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateResendTimerUI()
            }

            override fun onFinish() {
                isResendEnabled = true
                updateResendTimerUI()
            }
        }.start()
    }

    private fun updateResendTimerUI() {
        if (isResendEnabled) {
            binding.tvResend.isEnabled = true
            binding.tvTimer.text = "Resend?"
        } else {
            binding.tvResend.isEnabled = false
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis - minutes * 60 * 1000)
            binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToastMessage("Phone number sign-up successful!")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showToastMessage("Phone number sign-up failed: ${task.exception?.message}")
                }
            }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this@VerifyOtpActivity, message, Toast.LENGTH_SHORT).show()
    }
}