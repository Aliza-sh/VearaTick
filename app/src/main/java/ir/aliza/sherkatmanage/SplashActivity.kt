package ir.aliza.sherkatmanage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import ir.aliza.sherkatmanage.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val videoView = binding.videoView
//        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.logo_motion}")
//        videoView.setVideoURI(videoUri)
//        videoView.setOnCompletionListener {
//            // ویدئو تمام شده است، برای انتقال به اکتیویتی اصلی اقدام کنید
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        val playbackParams = PlaybackParams()
//        playbackParams.speed = 2.0f
//        videoView.start()

        Handler().postDelayed( {

            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)

    }
}