package com.vearad.vearatick

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.vearad.vearatick.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val anim = AlphaAnimation(
            1f, 0f
        )
        anim.duration = 500
        anim.fillAfter = true
        anim.repeatCount = 2
        anim.repeatMode = Animation.REVERSE

        binding.splashAnimation.startAnimation(anim)

        Handler(Looper.getMainLooper()).postDelayed( {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)

    }
}