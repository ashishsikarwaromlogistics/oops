package com.example.omoperation.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityTripChallanBinding

class TripChallan : AppCompatActivity() {
    lateinit var binding: ActivityTripChallanBinding
    lateinit var title: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_trip_challan)
        title=findViewById(R.id.title)
        title.setText("Trip Challan")
        binding.searchBtn.setOnClickListener {
            if(Utils.haveInternet(this)){
                val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")

                // Create an ArrayAdapter
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,  // Layout for the dropdown
                    items
                )
               // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spintrip.adapter=adapter
            }
        }
    }
}