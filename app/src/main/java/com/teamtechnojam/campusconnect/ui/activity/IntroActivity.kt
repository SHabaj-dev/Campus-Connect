package com.teamtechnojam.campusconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding;
    private val layoutsList = arrayOf(
        R.layout.intro_item_1,
        R.layout.intro_item_2,
        R.layout.intro_item_3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNextIntro.setOnClickListener {
            startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
            finish()
        }

    }
}