package com.example.omoperation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ODAStationAdapter
import com.example.omoperation.databinding.ActivityOdaStationBinding
import com.example.omoperation.model.oda.EmpEnquiry
import com.example.omoperation.model.oda.OdaResp
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.model.CommonMod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OdaStation : AppCompatActivity() , ODAStationAdapter.ContactsAdapterListener {
    lateinit var cp : CustomProgress
   lateinit var adapter: ODAStationAdapter
   lateinit var binding: ActivityOdaStationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cp= CustomProgress(this)
        binding=DataBindingUtil. setContentView(this,R.layout.activity_oda_station)
        binding.recyOda.setHasFixedSize(false)
        binding.recyOda.layoutManager=LinearLayoutManager(this)
        finddata()

    }
    fun calltofilter(){
        binding.edtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })
    }
    fun finddata(){
        val mod= CommonMod()
        mod.status="challan"
       // mod.branch="5393"
        mod.bcode= OmOperation.getPreferences(Constants.BCODE,"")
        ApiClient.getClient().create(ServiceInterface::class.java).oda_station(Utils.getheaders(),mod).enqueue(object :
            Callback<OdaResp> {
            override fun onResponse(call: Call<OdaResp>, resp: Response<OdaResp>) {
                if(resp.body()!!.error.toString().equals("false"))
                {
                    adapter= ODAStationAdapter(this@OdaStation,resp.body()!!.emp_enquiry,this@OdaStation)
                    binding.recyOda.adapter=adapter
                    calltofilter()
                }
                else{
                    Utils.showDialog(this@OdaStation,"error","Loading Plan not found",R.drawable.ic_error_outline_red_24dp)

                }
            }

            override fun onFailure(call: Call<OdaResp>, t: Throwable) {
                Utils.showDialog(this@OdaStation,"onFailure","${t.message}",R.drawable.ic_error_outline_red_24dp)

            }

        })
    }

    override fun onContactSelected(model: EmpEnquiry?) {
        val intent = Intent()
        setResult(-1, intent)
        intent.putExtra("branchcode",model!!.BRANCH_BRANCH_CODE)
        finish()
    }
}