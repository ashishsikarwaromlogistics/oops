package com.example.omoperation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.adapters.CheckMissingAdap
import com.example.omoperation.databinding.ActivityCheckMissingBinding
import com.example.omoperation.room.AppDatabase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CheckMissing : AppCompatActivity() {
    lateinit var binding : ActivityCheckMissingBinding
    lateinit var db:AppDatabase
    lateinit var cn : String
    var totalbox by Delegates.notNull<Int>()

    val notbarcode by lazy { ArrayList<String>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_check_missing)
        init()
    }
    fun init(){
        binding.recyMissing.setHasFixedSize(false)
        binding.recyMissing.layoutManager=LinearLayoutManager(this)
         binding.recyScan.setHasFixedSize(false)
        binding.recyScan.layoutManager=LinearLayoutManager(this)
        cn=intent!!.getStringExtra("cn").toString()
        totalbox=intent!!.getIntExtra("totalbox",0)

        db=AppDatabase.getDatabase(this)
        findValue()

    }

    private fun findValue() {
        lifecycleScope.launch {
            val allbarcode=db.barcodeDao().getAll()
            val scanbarcodes=ArrayList<String>()
            for(i in allbarcode){
                scanbarcodes.add(i.barcode.toString())
            }
            for(box in 1 until totalbox+1){
                var barcode=cn+box
                if(box<10){
                    barcode=cn+"000"+box
                }
                else if(box>=10 && box<100){
                    barcode=cn+"00"+box
                }
                else if(box>=100 && box<1000){
                    barcode=cn+"0"+box
                }
                if(scanbarcodes.contains(barcode)){
                  //  sacnbarcode.add(barcode)
                }
                else{
                    notbarcode.add(barcode)
                }

            }

            val adapter1=CheckMissingAdap(notbarcode)

            binding.recyMissing.adapter=adapter1



        }
        lifecycleScope.launch {
            val allbarcode=db.barcodeDao().getscan(cn)
            val adapter2=CheckMissingAdap(allbarcode)
            binding.recyScan.adapter=adapter2
        }
    }
}