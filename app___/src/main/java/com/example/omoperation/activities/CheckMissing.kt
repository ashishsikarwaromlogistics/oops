package com.example.omoperation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.CheckMissingAdap
import com.example.omoperation.databinding.ActivityCheckMissingBinding
import com.example.omoperation.model.cn_enquery.Myquery
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.room.AppDatabase
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import kotlin.properties.Delegates
//3082412000978 //challn number //29799391 //delivery
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

        binding.recyExtra.setHasFixedSize(false)
        binding.recyExtra.layoutManager=LinearLayoutManager(this)


        cn=intent!!.getStringExtra("cn").toString()
        totalbox=intent!!.getIntExtra("totalbox",0)

        db=AppDatabase.getDatabase(this)
        findValue()

    }

    private fun findValue() {
        lifecycleScope.launch {
            totalbox=db.verifydao().getbox(cn).toInt()
            /*if(totalbox==0){
                totalbox=db.verifydao().getbox(cn).toInt()
            }*/

            if(totalbox==0){
                val URL = ServiceInterface.omapi+"cn_enquiry.php?status=omstaff&cn_no=" + cn
                ApiClient.getClient().create(ServiceInterface::class.java).cnenquery(URL, Utils.getheaders()).enqueue(object :
                    retrofit2.Callback<Myquery> {
                    override fun onResponse(
                        call: Call<Myquery>,
                        response: Response<Myquery>
                    ) {
                        if(response.body()!=null){
                            if(response.body()!!.error.equals("false")){
                                try{
                                    lifecycleScope.launch {
                                        totalbox=response.body()!!.cn_enquiry.get(0).NO_OF_PKG.toInt()
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


                                        val extrabarcode=db.barcodeDao().Extrascan(cn,totalbox)
                                        binding.recyExtra.adapter=CheckMissingAdap(extrabarcode)
                                        binding.tvntscan.setText("Not Scan=${notbarcode.size}")
                                        binding.tvextra.setText("Extra Scan{${extrabarcode.size}}")
                                    }
                                }
                                catch (e:Exception){
                                    totalbox=0;
                                }

                            }
                        }
                    }

                    override fun onFailure(call: Call<Myquery>, t: Throwable) {

                        Utils.showDialog(this@CheckMissing,"onFailure ",t.message,
                            R.drawable.ic_error_outline_red_24dp)
                    }

                })
            }
            else {
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


                val extrabarcode=db.barcodeDao().Extrascan(cn,totalbox)
                binding.recyExtra.adapter=CheckMissingAdap(extrabarcode)
                binding.tvntscan.setText("Not Scan=${notbarcode.size}")
                binding.tvextra.setText("Extra Scan{${extrabarcode.size}}")
            }





        }
        lifecycleScope.launch {
            val allbarcode=db.barcodeDao().getscan(cn)
            val adapter2=CheckMissingAdap(allbarcode)
            binding.recyScan.adapter=adapter2
            binding.tvscan.setText("SCAN {${allbarcode.size}}")
        }
    }
}