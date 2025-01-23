package com.example.omoperation.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.omoperation.R
import com.example.omoperation.databinding.ActivityMyBrowserBinding


class MyBrowser : AppCompatActivity() {

    private lateinit var binding: ActivityMyBrowserBinding
   lateinit var url:String
   lateinit var title:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        url=intent!!.getStringExtra("url").toString()
        title=findViewById(R.id.title)
        title.setText(intent!!.getStringExtra("title").toString())
        val webSettings: WebSettings = binding.webview.getSettings()
        webSettings.javaScriptEnabled = true

        // Set WebViewClient to open links within the WebView

        // Set WebViewClient to open links within the WebView
         binding.webview.setWebViewClient(WebViewClient())
      /*  binding.webview.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                binding. progressBar.setVisibility(View.VISIBLE)
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progressBar.setVisibility(View.GONE)
            }
        })*/

        // Optional: You can also set WebChromeClient to update progress

        // Optional: You can also set WebChromeClient to update progress
        binding.webview.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress < 100) {
                    binding.progressBar.setVisibility(View.VISIBLE)
                //    binding.progressBar.setProgress(newProgress)
                } else {
                    binding.progressBar.setVisibility(View.GONE)
                }
            }
        })
        // Load a URL

        // Load a URL
        binding.webview.loadUrl(url)

    }
    override fun onBackPressed() {
        // Check if there's history to go back to
        if (binding.webview.canGoBack()) {
            binding. webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}