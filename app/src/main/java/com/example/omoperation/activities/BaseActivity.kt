package com.example.omoperation.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val swDp = resources.configuration.smallestScreenWidthDp
            if (swDp < 600) {
                // Lock only on phones (typically < 600dp)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        } catch (e: Exception) {
            Log.e("BaseActivity", "Orientation lock failed", e)
        }
    }
}
