package ir.aliza.sherkatmanage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import ir.aliza.sherkatmanage.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val anim = AlphaAnimation(
            1f, 0f
        )
        anim.duration = 1000
        anim.fillAfter = true
        anim.repeatCount = 5
        anim.repeatMode = Animation.REVERSE

        binding.splashAnimation.startAnimation(anim)

        Handler().postDelayed( {

            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
}