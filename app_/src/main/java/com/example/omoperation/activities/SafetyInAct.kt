package com.example.omoperation.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.SafetyAdapter
import com.example.omoperation.databinding.ActivitySafetyInBinding

class SafetyInAct : AppCompatActivity() {
    lateinit var title: TextView
    lateinit var imgback: ImageView
    lateinit var binding:ActivitySafetyInBinding
    val Separatorlist=ArrayList<String>()
    val cargolist=ArrayList<String>()
    val Ratchetlist=ArrayList<String>()
    val airlist=ArrayList<String>()
    val totallist=ArrayList<String>()
      val adapter: SafetyAdapter by lazy { SafetyAdapter(totallist) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding=DataBindingUtil. setContentView(this,R.layout.activity_safety_in)
        //totallist.add("1234567i")
        title=findViewById(R.id.title)
        imgback=findViewById(R.id.imgback)
        imgback=findViewById(R.id.imgback)
        imgback.visibility= View.VISIBLE
        binding.recy.setHasFixedSize(true)
        binding.recy.layoutManager=LinearLayoutManager(this)
        binding.recy.adapter=adapter
        imgback.setOnClickListener {
            finish()
        }
        var sourcelauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                //  des_branch_code=data!!.getStringExtra("branchcode").toString()
                binding.tvbranch.setText(data!!.getStringExtra("branchcode"))
            }
        }
        title.setText("Safety Kit In ")
        binding.tvbranch.setOnClickListener {
            val intent = Intent(this, BranchesAct::class.java)
            intent .putExtra("senddata",1)
            sourcelauncher.launch(intent)
        }
        binding.barcodeText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 1) {
                    binding.barcodeText.text = null
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })//32628
            binding.barcodeText.setOnEditorActionListener { v, actionId, event ->
                //  if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 0) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
                {
                  val barcodevalue=binding.barcodeText.text.toString()
                    if(totallist.contains(barcodevalue)){
                        clearBarcodeEdittext()
                        Utils.showDialog(this,"error","duplicate Barcode",R.drawable.ic_error_outline_red_24dp)
                        return@setOnEditorActionListener true
                    }
                    if(barcodevalue.equals("")){
                    }
                    else if(Utils.check_null_Int(barcodevalue) in 1000001..4999999
                   ){

                       if(barcodevalue.startsWith("1"))
                       {
                           Separatorlist.add(barcodevalue)
                           binding.edtShapSheet.setText(Separatorlist.size.toString())
                       }
                           else if(barcodevalue.startsWith("2"))
                       {
                               cargolist.add(barcodevalue)
                           binding.edtCargo.setText(cargolist.size.toString())
                           }
                       else if(barcodevalue.startsWith("3")) {
                           Ratchetlist.add(barcodevalue)
                           binding.edtRatchet.setText(Ratchetlist.size.toString())
                       }
                       else if(barcodevalue.startsWith("4")){
                           airlist.add(barcodevalue)
                           binding.edtAir.setText(airlist.size.toString())
                       }

                        totallist.add(barcodevalue)
                        binding.tvbarcode.setText(totallist.size.toString())
                        adapter.addbarcode(barcodevalue)
                        clearBarcodeEdittext()
                   }
                    else {
                    Utils.showDialog(this,"error","Invalid Barcode1",R.drawable.ic_error_outline_red_24dp)
                    }
                    clearBarcodeEdittext()
                    return@setOnEditorActionListener true
                } else {
                    clearBarcodeEdittext()
                    return@setOnEditorActionListener false
                }
            }


    }

    private fun clearBarcodeEdittext() {
        binding.barcodeText.setText("")
    }
}