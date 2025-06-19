package com.example.omoperation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.adapters.CommonAdapter
import com.example.omoperation.databinding.ActivityVideoListBinding
import com.example.omoperation.viewmodel.VideoListViewModel

class VideoList : AppCompatActivity() , CommonAdapter.CommonInterface {
    var adapter: CommonAdapter? = null
    var url: Array<String> = arrayOf(
        "https://omsl.omlogistics.co.in/omvideo/video/prebooking_planning.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/dangerous_goods.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/client_prq.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/cncreation_by_ewaybill.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/shortexcess_token_generation.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/quality_check.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/transit_worthy.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/kom_prq.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/branch_prq.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/V-Measure-New.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/cn_printA5.mp4",
        "https://omsl.omlogistics.co.in/omvideo/video/loading_tally.mp4",
        "https://omapp.omlogistics.co.in/assets/videos/1.mp4",
        "https://omapp.omlogistics.co.in/assets/videos/2.mp4",
        "https://omapp.omlogistics.co.in/assets/videos/3.mp4"
    )
    var name: Array<String> = arrayOf(
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


    lateinit var binding: ActivityVideoListBinding
    private lateinit var viewModel: VideoListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ashish","onCreate")
        binding=DataBindingUtil. setContentView(this,R.layout.activity_video_list)
        /*intent = Intent(this@VideoList, VideoSample::class.java)
        intent.putExtra("url", "https://omapp.omlogistics.co.in/assets/videos/2.mp4")
        intent.putExtra("title", "Help")
        binding.recy.setHasFixedSize(true)
        binding.recy.layoutManager=LinearLayoutManager(this)
        adapter =  CommonAdapter(name, icon, this)
        binding.recy.setAdapter(adapter)*/
        viewModel = ViewModelProvider(this).get(VideoListViewModel::class.java)
        binding.recy.setHasFixedSize(true)
        binding.recy.layoutManager = LinearLayoutManager(this)
        adapter = CommonAdapter(viewModel.name, viewModel.icon, this)
        binding.recy.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        Log.d("ashish","onResume")
    }
    override fun sendposition(i: Int) {
        startActivity(Intent(this,VideoSample::class.java)
            .putExtra("title",name[i])
            .putExtra("url",url[i])
        )
    }

 /*   override fun sendposition(i: Int) {
        when (i) {
            0          -> {
                startActivity(Intent(this, VideoSample::class.java)
                    .putExtra("title","Video 1")
                    .putExtra("url","https://omapp.omlogistics.co.in/assets/videos/1.mp4")
                )
            }

            1          -> {
                startActivity(Intent(this, VideoSample::class.java)
                    .putExtra("title","Video 2")
                    .putExtra("url","https://omapp.omlogistics.co.in/assets/videos/2.mp4")
                )
            }

            2          -> {
                startActivity(Intent(this, VideoSample::class.java)
                    .putExtra("title","Video 3")
                    .putExtra("url","https://omapp.omlogistics.co.in/assets/videos/3.mp4")
                )
            }

            3          -> {
                startActivity(Intent(this, VideoSample::class.java)
                    .putExtra("title","Video 4")
                    .putExtra("url","https://omsl.omlogistics.co.in/omvideo/video/V-Measure-New.mp4")
                )
            }
            4          -> {
                startActivity(Intent(this, VideoSample::class.java)
                    .putExtra("title","Video 5")
                    .putExtra("url","https://omapp.omlogistics.co.in/assets/videos/5.mp4")
                )
            }

        }

    }*/
}