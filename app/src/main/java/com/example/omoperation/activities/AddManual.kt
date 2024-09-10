package com.example.omoperation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ManualAdapter
import com.example.omoperation.databinding.ActivityAddManualBinding
import com.example.omoperation.model.cnvalidate.CnValidateResp
import com.example.omoperation.model.cnvaridate.CnValidateMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.ManualAvr
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddManual : AppCompatActivity() , ManualAdapter.ManualInterface {


    private lateinit var binding: ActivityAddManualBinding

   lateinit var db : AppDatabase
   lateinit var manualInterface :  ManualAdapter.ManualInterface
    lateinit var manuallist:List<ManualAvr>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_manual)
        manualInterface=this
        manuallist=ArrayList()
        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=ManualAdapter(manuallist,manualInterface)
        db= AppDatabase.getDatabase(this)
        getData()
        binding.addBtn.setOnClickListener {
        if(Utils.haveInternet(this)){
            val mod=CnValidateMod()
            mod.avr=""
            mod.bcode=""
            mod.cn_no=binding.cnNo.text.toString()
            ApiClient.getClient().create(ServiceInterface::class.java).
            cn_validate1(Utils.getheaders(),mod).enqueue(object : Callback<CnValidateResp>{
                override fun onResponse(
                    call: Call<CnValidateResp>,
                    response: Response<CnValidateResp>
                ) {
                   if(response.code()==200){
                    if(response.body()?.error.equals("false",true)){
                      lifecycleScope.launch {
                          val manual=ManualAvr(cn=binding.cnNo.text.toString(), boxes = binding.boxes.toString(), challan = "")
                          db.manualDao().inserbarcode(manual)
                      }
                    }
                       else{
                        lifecycleScope.launch {
                            val manual=ManualAvr(cn=binding.cnNo.text.toString(), boxes = binding.boxes.text.toString(), challan = "")
                            db.manualDao().inserbarcode(manual)
                        }
                        Utils.showDialog(this@AddManual,response.code().toString(),response.body()!!.response,R.drawable.ic_error_outline_red_24dp)

                    }
                       getData()
                   }
                    else{
                       Utils.showDialog(this@AddManual,response.code().toString(),response.message(),R.drawable.ic_error_outline_red_24dp)

                   }
                }

                override fun onFailure(call: Call<CnValidateResp>, t: Throwable) {
                  Utils.showDialog(this@AddManual,"onFailure","",R.drawable.ic_error_outline_red_24dp)
                }

            })
        }
    }



    }

    private fun getData() {
        lifecycleScope.launch {
            manuallist= db.manualDao().getData()
            if(manuallist.size>0){
                binding.recyclerView.adapter=ManualAdapter(manuallist,manualInterface)
            }
        }
    }

    override fun deleteCN(cn: String) {
        lifecycleScope.launch {
            db.manualDao().deleteoneentry(cn)
            getData()
        }


    }
}