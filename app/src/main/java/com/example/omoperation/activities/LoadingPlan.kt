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
import com.example.omoperation.adapters.LoadingPlanAdapter
import com.example.omoperation.databinding.ActivityLoadingPlanBinding
import com.example.omoperation.model.loading.LoadingNo
import com.example.omoperation.model.loading.LoadingResp
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.model.CommonMod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadingPlan : AppCompatActivity() , LoadingPlanAdapter.Loadinginterface {
    lateinit var cp : CustomProgress
    lateinit var binding : ActivityLoadingPlanBinding
    lateinit var adapter : LoadingPlanAdapter
    lateinit var interfaces :  LoadingPlanAdapter.Loadinginterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_loading_plan)
        cp= CustomProgress(this)
        interfaces=this
        binding.recyLoading.setHasFixedSize(false)
        binding.recyLoading.layoutManager= LinearLayoutManager(this)
        findata()
       /* var loadingNo:  MutableList<LoadingNo> = mutableListOf()
        loadingNo.add(LoadingNo("","","123456","1305"))
        loadingNo.add(LoadingNo("","","67890","7500"))
        loadingNo.add(LoadingNo("","","24680","8510"))
        loadingNo.add(LoadingNo("","","13579","6640"))

        adapter= LoadingPlanAdapter(interfaces,this@LoadingPlan,loadingNo)
        binding.recyLoading.adapter=adapter
        calltofilter()*/

         }
    fun findata(){
        cp.show()
        val mod= CommonMod()
        mod.status="getTallyNo"
        // mod.branch="1328"
        mod.branch=OmOperation.getPreferences(Constants.BCODE,"")
        ApiClient.getClientsanchar().create(ServiceInterface::class.java).loadingplan(Utils.getheaders(),mod).enqueue(object : Callback<LoadingResp>{
            override fun onResponse(call: Call<LoadingResp>, resp: Response<LoadingResp>) {
                cp.dismiss()
                if(resp.body()!!.error.toString().equals("false"))
                {
                    adapter= LoadingPlanAdapter(interfaces,this@LoadingPlan,resp.body()!!.loadingNo)
                    binding.recyLoading.adapter=adapter
                    calltofilter()
                }
                else{
                    Utils.showDialog(this@LoadingPlan,"error","Loading Plan not found",R.drawable.ic_error_outline_red_24dp)

                }
            }

            override fun onFailure(call: Call<LoadingResp>, t: Throwable) {
                cp.dismiss()
                Utils.showDialog(this@LoadingPlan,"onFailure","${t.message}",R.drawable.ic_error_outline_red_24dp)

            }

        })

    }
    fun calltofilter(){
        binding.edtloading.addTextChangedListener(object : TextWatcher {
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
    override fun senddata(a: LoadingNo) {
        val intent = Intent()
        setResult(-1, intent)
        intent.putExtra("loadingno",a.LOADING_NO)
        finish()
    }
}