package com.example.omoperation.activities

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.omoperation.R


class VideoSample : AppCompatActivity() {
    lateinit var back :Button
    lateinit var videoView :VideoView
    var seconds=0
    private var handler: Handler? = null
    private var checkVideoStatusRunnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_sample)
        back=findViewById(R.id.back)
        videoView=findViewById(R.id.myvideo)

        val videoUri = Uri.parse(intent.getStringExtra("url"))
        intent.getStringExtra("url")?.let { Log.d("url", it) }
        // For online video use: Uri.parse("https://www.example.com/sample.mp4")

        // Set up media controller for playback controls
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Set video URI and start playing
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.start()
        handler = Handler()
        checkVideoStatusRunnable = object : Runnable {
            override fun run() {
                if (videoView.isPlaying()) {
                    seconds=seconds+1
                    Log.d("VideoStatus", "Video is playing")
                } else {
                    Log.d("VideoStatus", "Video is not playing")
                }
                // Schedule the next check after 1 second (1000ms)
                handler!!.postDelayed(this, 1000)
            }
        }

        // Start checking video status every second
        handler!!.post(checkVideoStatusRunnable as Runnable)
        back.setOnClickListener {
         //   Toast.makeText(this,""+seconds,0).show()
            finish()
           // startActivity(Intent(this,MainActivity2::class.java))
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Perform your custom action here
            // For example, show a confirmation dialog or exit the activity
       //   Toast.makeText(this,""+seconds,0).show()
            finish()
            true // Return true to consume the back button press (prevent default behavior)
        } else {
            super.onKeyDown(keyCode, event) // For other keys, pass the event to the default handler
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()  // Pause the video when the activity is paused
    }

    override fun onResume() {
        super.onResume()
        videoView.resume() // Resume the video when the activity is resumed
    }
    override fun onDestroy() {
        super.onDestroy()
        // Stop the handler when the activity is closed
        handler!!.removeCallbacks(checkVideoStatusRunnable!!)
    }
}