package com.example.omoperation.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityStockDocScanBinding
import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockDocScan : AppCompatActivity() {
    lateinit var title :TextView
    lateinit var binding :ActivityStockDocScanBinding
    val doclist=ArrayList<String>()
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
                    var docnum = binding.barcodeText.getText().toString().trim()
                    binding.barcodeText.setText("")
                    if (docnum.length > 5 && docnum.length <= 16) {
                        if(doclist.contains(docnum)){
                            showCustomBackgroundToast("Already Scan")
                        clearBarcodeEdittext()}
                        else
                            checkcn(docnum)
                    }
                    else {
                        showCustomBackgroundToast("Please Scan proper barcode")
                        clearBarcodeEdittext()
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
    fun checkcn(DOC:String){
        val mod: CommonMod = CommonMod()
        mod.status=("sa")
        mod.cn_no=(DOC)
        ApiClient.getClient().create(ServiceInterface::class.java).
        cn_validate(Utils.getheaders(),mod).enqueue(object : Callback<CommonRespS>{
            override fun onResponse(call: Call<CommonRespS>, response: Response<CommonRespS>) {
               if(response.body()!=null){
                   if(response.body()!!.error.equals("false")){
                       doclist.add(DOC)
                   }
                   else  Utils.showDialog(this@StockDocScan,"error",response.body()!!.response,R.drawable.ic_error_outline_red_24dp)

               }
                else {
                   Utils.showDialog(this@StockDocScan,"response boy is empty","server error or net connection problem",R.drawable.ic_error_outline_red_24dp)

               }
            }

            override fun onFailure(call: Call<CommonRespS>, t: Throwable) {
               Utils.showDialog(this@StockDocScan,"onFailure",t.message,R.drawable.ic_error_outline_red_24dp)
            }

        })

    }
    private fun showCustomBackgroundToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        val view = toast.view

        // Get the TextView from the default Toast view
        val textView = view?.findViewById<TextView>(android.R.id.message)
        textView?.setTextColor(Color.WHITE) // Change text color if needed

        // Change the background of the Toast
        view?.setBackgroundResource(R.drawable.toast_background)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
    private fun clearBarcodeEdittext() {
        binding.barcodeText.setText("")
    }
}