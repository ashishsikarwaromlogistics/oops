package com.example.omoperation.activities

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityStockDocScanBinding
import kotlinx.coroutines.launch

class StockDocScan : AppCompatActivity() {
    lateinit var title :TextView
    lateinit var binding :ActivityStockDocScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_stock_doc_scan)
        title=findViewById(R.id.title)
        title.setText("Stock Audit(Doc Scan)")
        binding.recylist.setHasFixedSize(true)
        binding.recylist.layoutManager=LinearLayoutManager(this)
        binding.barcodeText.setText("")
        binding.barcodeText.requestFocus()
        binding.barcodeText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 1) {
                    binding.barcodeText.text = null
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.barcodeText.setOnEditorActionListener { v, actionId, event ->
            //  if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 0) {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
            {
                try {
                    //viewmode.checkdata(binding.barcodeText.getText().toString().trim())
                    var barCode = binding.barcodeText.getText().toString().trim()
                    binding.barcodeText.setText("")
                    if (barCode.startsWith("O")) {
                        barCode = barCode.substring(1, barCode.length)
                        barCode = Utils.revertTransform(barCode)
                    }
                    if (barCode.contains(getString(R.string.NBC_Sticker_Identification))) {
                        val CustomerBarcode =
                            barCode.split(getString(R.string.NBC_Sticker_Identification).toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        barCode =
                            getString(R.string.NBC_Prefix) + CustomerBarcode[1] + CustomerBarcode[4]
                    } else {
                        if (barCode.contains("-")) {
                            val builder = StringBuilder(barCode)
                            barCode = builder.deleteCharAt(builder.indexOf("-") + 1)
                                .deleteCharAt(builder.indexOf("-")).toString()
                                .replaceFirst("^0+(?!$)".toRegex(), "")
                        } else if (barCode.startsWith("0")) {
                            barCode =
                                barCode.trim { it <= ' ' }.replaceFirst("^0+(?!$)".toRegex(), "")
                        }
                    }
                    if (barCode.isEmpty()) {
                        binding.barcodeText.error = "Please Enter Barcode"
                    }
                    else if(barCode.length<9 || barCode.length>20){
                        binding.barcodeText.error = "Please Enter valid Barcode"
                    }

                }
                catch (e: Exception) {
                    Toast.makeText(this@StockDocScan, e.message, Toast.LENGTH_SHORT).show()
                }
                clearBarcodeEdittext()
                return@setOnEditorActionListener true
            }
            else {
                clearBarcodeEdittext()
                return@setOnEditorActionListener false
            }
        }

    }

    private fun clearBarcodeEdittext() {
        binding.barcodeText.setText("")
    }
}