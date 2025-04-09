package com.example.omoperation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.omoperation.R

class VideoListViewModel(application: Application) : AndroidViewModel(application) {
    val name = arrayOf(
        "Pre-booking planning",
        "Dangerous goods",
        "Client PRQ request",
        "CN creation through e-waybill",
        "Short excess token generation",
        "Quality Check of shipment",
        "Transit-worthy shipments",
        "KOM PRQ",
        "Branch PRQ",

        "V-Measure Machine Process",
        "Print of CN on A5-sized paper",
        "Loading Plan Tally",


        "Loading By Scanning",
        "Use Of Barcode Module",
        "Unloading By Scanning"
    )
    var icon: Array<Int> = arrayOf(
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera,
        R.drawable.ic_video_camera
    )

}