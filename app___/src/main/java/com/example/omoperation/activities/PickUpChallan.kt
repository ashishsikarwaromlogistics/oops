package com.example.omoperation.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.OfflineChallanAdapter
import com.example.omoperation.databinding.ActivityPickUpChallanBinding
import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.offline.OfflineChallanListModel
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.Barcode
import com.example.omoperation.room.tables.CN
import com.example.omoperation.room.tables.RestoreBarcode
import kotlinx.coroutines.launch

class PickUpChallan : AppCompatActivity() , OfflineChallanAdapter.OnItemSelectedListener {
    lateinit var binding:ActivityPickUpChallanBinding
    lateinit var adapter: OfflineChallanAdapter
    private var list: ArrayList<OfflineChallanListModel>? = null
    private var finalList: LinkedHashSet<OfflineChallanListModel>? = null
    lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil. setContentView(this,R.layout.activity_pick_up_challan)
        db= AppDatabase.getDatabase(this)
        list= ArrayList()
        finalList = java.util.LinkedHashSet()
        binding.offlineChallanList.setHasFixedSize(true)
        binding.offlineChallanList.layoutManager=LinearLayoutManager(this)
        adapter = OfflineChallanAdapter(list, this, this)
        binding.offlineChallanList.adapter=adapter
        getData()
        binding.nextButton.setOnClickListener {
            if (finalList!!.size > 0) {
                AlertDialog.Builder(this@PickUpChallan)
                    .setMessage("Do you really want to go next?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                        lifecycleScope.launch {
                            db.barcodeDao().deleteAllBarcodes()
                            db.manualDao().DeleteAllManual()
                            db.verifydao().deleteCN()
                            db.restorebarcodedao().deleteAllBarcodes()

                            for (model in finalList!!) {
                                val cn= CN(challan = "", box =model.BOXES.toString(),cn =model.CNNO, city = "", weight = "0")
                                db.verifydao().inserbarcode(cn)
                                val cn1: String = model.CNNO
                                for (currentBox in 1..model.BOXES.toInt()) {
                                    var bar = if (currentBox < 10) {
                                        cn1 + "000" + currentBox
                                    } else if (currentBox >= 10 && currentBox < 100) {
                                        cn1 + "00" + currentBox
                                    } else if (currentBox >= 100 && currentBox < 1000) {
                                        cn1 + "0" + currentBox
                                    } else {
                                        cn1 + currentBox
                                    }
                                    val barcode= Barcode(barcode=bar )
                                    db.barcodeDao().inserbarcode(barcode)
                                    val barcodem= RestoreBarcode(barcode=bar )
                                    db.restorebarcodedao().inserbarcode(barcodem)
                                }
                            }
                            finish()
                            startActivity(Intent(this@PickUpChallan, RestoreActivity::class.java).putExtra("value",3))

                        }



                         /* val intent =
                            Intent(
                                this@PickUpChallan,
                                ChallanCreation::class.java
                            )
                        intent.putExtra("from", "offline_challan")
                        startActivity(intent)*/
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog: DialogInterface?, which: Int -> }.show()
            }
            else {
              Utils.showDialog(this@PickUpChallan, "error","Please select CN",R.drawable.ic_error_outline_red_24dp)
            }
        }
    }
    fun getData(){
       if(Utils.haveInternet(this)){
           //   final String URL = AppConfig.ANDROID_API_PATH + "/offline_challan.php?bcode="+"1314";// +data[1];
           val mod: CommonMod = CommonMod()
           mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
          //  mod.bcode="2001"//OmOperation.getPreferences(Constants.BCODE,"")
           lifecycleScope.launch {
               val response=ApiClient.getClient().create(ServiceInterface::class.java).offline_challan(Utils.getheaders(),mod)
               if(response.code()==200){
                  if(response.body()!!.error.equals("false")){

                      //  JSONArray array = jsonObject.getJSONArray("details");
                      if (response.body()!!.details.size > 0) {
                          for (i in 0 until response.body()!!.details.size) {
                              // JSONObject object = array.getJSONObject(i);
                              val model = OfflineChallanListModel(
                                  response.body()!!.details.get(i).CN_NO!!,
                                  response.body()!!.details.get(i).BOX!!
                              )
                              list!!.add(model)
                          }
                          adapter.notifyDataSetChanged()
                          binding.emptyListImg.setVisibility(View.GONE)
                      } else {
                          binding.emptyListImg.setVisibility(View.VISIBLE)
                      }
                  }
               }
               else{
                   binding.emptyListImg.setVisibility(View.VISIBLE)
               }


           }
       }
    }

    override fun onclick(model: OfflineChallanListModel, flag: Boolean) {
        if (flag) {
            finalList!!.remove(model)
        } else {
            finalList!!.add(model)
        }
    }
}