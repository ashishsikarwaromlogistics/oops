package com.example.omoperation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.adapters.MisAdap
import com.example.omoperation.databinding.ActivityMisBinding
import com.example.omoperation.model.MIS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MisAct : AppCompatActivity() {
    lateinit var binding: ActivityMisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil. setContentView(this,R.layout.activity_mis)
        binding.recy.setHasFixedSize(true)
        binding.recy.layoutManager=LinearLayoutManager(this)
        val jsonString = OmOperation.getPreferences2(Constants.MISDATA, "[]")
        val listType = object : TypeToken<ArrayList<MIS>>() {}.type
        val nameObjects: ArrayList<MIS> = Gson().fromJson(jsonString, listType)
        // nameObjects.add(MIS("33322","asgusg","765432","challan"))

       /* val updatedJsonString = Gson().toJson(nameObjects)
        OmOperation.savePreferences2(Constants.MISDATA, updatedJsonString)*/
        binding.recy.adapter=MisAdap(nameObjects)
    }
}