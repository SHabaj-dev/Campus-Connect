package com.teamtechnojam.campusconnect.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
    }

    private fun checkAuthentication(userName: String, userPassword: String) {

        if (userName == "shabaj" && userPassword == "12345678") {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
            dialog.dismiss()
        } else {
            Toast.makeText(
                this@LoginActivity,
                "Login failed!! Please Check Credentials",
                Toast.LENGTH_SHORT
            ).show()
            binding.etUserName.text?.clear()
            binding.etUserPassword.text?.clear()
            dialog.dismiss()
        }

    }

}