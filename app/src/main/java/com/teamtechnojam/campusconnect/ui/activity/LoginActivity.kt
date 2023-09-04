package com.teamtechnojam.campusconnect.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var verificationId: String? = null
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog
    private final val TAG = "LOGIN_ACTIVITY"
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        dialog = Dialog(this@LoginActivity)
        dialog.setContentView(R.layout.loading_pop_up)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.flSignUpWithGoogle.setOnClickListener {
            dialog.show()
            loginUsingGoogle()
        }

    }

    private fun loginUsingGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this@LoginActivity.getString(R.string.default_sign_in_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, googleSignInOptions)

        val intent: Intent = googleSignInClient.signInIntent
        startActivityForResult(intent, 100)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            if (signInAccountTask.isSuccessful) {
                val s = "Google sign in successful"
                showToastMessage(s)
                try {
                    val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)

                    if (googleSignInAccount != null) {

                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken, null
                        )

                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->

                                if (task.isSuccessful) {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    dialog.dismiss()
                                    finish()
                                    Log.d("FIREBASE_LOGIN", "Firebase authentication successful")
                                } else {
                                    Log.d(
                                        "FIREBASE_LOGIN",
                                        "Authentication Failed :" + task.exception?.message
                                    )
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

}