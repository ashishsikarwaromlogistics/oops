package com.example.omoperation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.omoperation.R

class VideoListViewModel(application: Application) : AndroidViewModel(application) {
    val name = arrayOf(
        "Loading By Scanning",
        "Use Of Barcode Module",
        "Unloading By Scanning",
        "Video 4",
        "Video 5"
    )

    val icon = arrayOf(
        R.drawable.banner_covid,
        R.drawable.plan,
        R.drawable.challan,
        R.drawable.rewarehouse,
        R.drawable.challan
    )
}