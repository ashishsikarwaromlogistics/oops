package com.example.omoperation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.adapters.CommonAdapter
import com.example.omoperation.databinding.ActivityBarcodeModuleBinding

class BarcodeModule : AppCompatActivity() ,CommonAdapter.CommonInterface{
    lateinit var binding :ActivityBarcodeModuleBinding
    var name: Array<String> = arrayOf(
        "Print Sticker",
        "Multiple Sticker Print",
        "Docket Print"
    )
    var icon: Array<Int> = arrayOf(
        R.drawable.barcodeprint,
        R.drawable.plan,
        R.drawable.barcodeprint
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_barcode_module)
         binding.recyList.setHasFixedSize(true)
        binding.recyList.layoutManager=LinearLayoutManager(this)
        binding.recyList.adapter=CommonAdapter(name,icon,this)
    }

    override fun sendposition(i: Int) {
        when (i) {
            0 -> {
                startActivity(Intent(this, BarcodePrint::class.java))
            }
            1 -> {
                startActivity(Intent(this, PrintMultiple::class.java))
            }
            2 -> {
                startActivity(Intent(this, PrintCN::class.java))
            }
        }
    }
}