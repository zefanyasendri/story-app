package com.dicoding.android.zsstoryapp.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.dicoding.android.zsstoryapp.databinding.ActivityWelcomeBinding
import com.dicoding.android.zsstoryapp.view.login.LoginActivity
import com.dicoding.android.zsstoryapp.view.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    // Fullscreen
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    // Button
    private fun setupAction() {
        binding.btnWelcomeLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnWelcomeRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Animation
    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvWelcomeTitle, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvWelcomeDesc, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnWelcomeLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnWelcomeRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply { playTogether(login, register) }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }
}