package com.example.omoperation.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.databinding.ActivitySplashBinding
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class Splash : AppCompatActivity() {
    lateinit var binding:ActivitySplashBinding
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)
        mediaPlayer = MediaPlayer.create(this, R.raw.a) // Add your audio file here
        /*if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }*/
       //val animation=Animation.
        binding.logo.animate()
           .alpha(0f)
           .setDuration(100)
           .withEndAction {
               // Fade in after fade out is complete
               binding.logo.animate()
                   .alpha(1f)
                   .setDuration(3000)
                   .start()
           }.start()
        val splashScreenTimeOut = 3000.00 // 3 seconds

        // Splash screen timer
        Handler(Looper.getMainLooper()).postDelayed({
            // Start your next activity
          //  startActivity(Intent(this,MyBrowser::class.java).putExtra("url","https://vms.omlogistics.co.in/administrator"))
            if(OmOperation.getPreferences(Constants.ISLOGIN,"0").equals("1")){
                startActivity(Intent(this,DashboardAct::class.java))
                finish()
            }
            else{
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            // Close this activity
            finish()
        }, splashScreenTimeOut.toLong())

    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            // Reset and prepare the MediaPlayer to replay the audio
            mediaPlayer.reset()
          //  mediaPlayer = MediaPlayer.create(this, R.raw.sample_audio)
        }
    }

}