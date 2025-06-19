package com.example.omoperation.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.adapters.RestoreAdapter
import com.example.omoperation.databinding.ActivityRestoreBinding
import com.example.omoperation.model.RestoreCN
import com.example.omoperation.model.dataclass.CNWithBoxes
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestoreActivity : AppCompatActivity(), RestoreAdapter.SendValue {
    lateinit var title: TextView
    lateinit var binding: ActivityRestoreBinding
    lateinit var db: AppDatabase
    lateinit var adapter: RestoreAdapter

    //  lateinit var mylist : List<CNWithBoxes>
    private var mylist: MutableList<CNWithBoxes> = mutableListOf()
    private var restorecnlist: MutableList<RestoreCN> = mutableListOf()
    var value = 0//0 finish 1=loading 2=avr 4=avr witjout gate
    lateinit var cp: CustomProgress
    var isdelete=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restore)
        cp = CustomProgress(this)
        title = findViewById(R.id.title)
        title.setText("Restore Data")
        db = AppDatabase.getDatabase(this)
        //mylist=ArrayList()
        try {
            isdelete= intent.getStringExtra("isdelete").toString()
           if(isdelete.equals("NO")){
               OmOperation.savePreferences(Constants.bajajlist_with_realtion,"")
               OmOperation.savePreferences(Constants.BAJAJ,"")
               lifecycleScope.launch {
                   db.restorebarcodedao().deleteAllBarcodes()
                   db.barcodeDao().deleteAllBarcodes()
                   db.verifydao().deleteCN()
                   db.manualDao().DeleteAllManual()

               }

           }


        }catch (e:Exception){}


        restorecnlist = ArrayList()
        value = intent.getIntExtra("value", 0)//
        //mylist.add(CNWithBoxes("1234", "2", "2"))


        //  (mylist as ArrayList<CNWithBoxes>).add(CNWithBoxes("1234","2","2"))
        adapter = RestoreAdapter(restorecnlist, this)
        binding.recyRestore.setHasFixedSize(true)
        binding.recyRestore.layoutManager = LinearLayoutManager(this)
        binding.recyRestore.adapter = adapter
        //    binding=DataBindingUtil.setContentView(this,R.layout.activity_restore)
        binding.checkbox.setOnClickListener {
            for (i in 0 until restorecnlist.size)
                restorecnlist.set(
                    i,
                    RestoreCN(restorecnlist.get(i).cn, restorecnlist.get(i).box, true)
                )
            adapter.notifyDataSetChanged()

        }
        lifecycleScope.launch {
            cp.show()
            db.barcodeDao().deleteAllBarcodes()
             if(isdelete.equals("NOT NOW")){}
            else{
                 val a = db.restorebarcodedao().getAll()
                 for (i in 0 until a.size) {
                     val barcodem = Barcode(barcode = a.get(i).barcode,timestamp=a.get(i).timestamp)
                     db.barcodeDao().inserbarcode(barcodem)

                 }

                 mylist.clear()
                 mylist.addAll(db.barcodeDao().cnwithboxes())

                 Log.d("RestoreActivity", "List size: ${mylist.size}")
                 for (i in 0 until mylist.size) {
                     restorecnlist.add(RestoreCN(mylist.get(i).cn, mylist.get(i).box, false))
                 }
                 adapter.notifyDataSetChanged()
            }


            withContext(Dispatchers.Main) {
                if (mylist.size < 1)
                    if (value == 1) {
                        startActivity(Intent(this@RestoreActivity, ChallanCreation::class.java))
                        finish()
                    } else if (value == 2) {
                        startActivity(Intent(this@RestoreActivity, AVR::class.java))
                        finish()
                    }else if (value == 3) {
                        startActivity(Intent(this@RestoreActivity, ChallanCreation::class.java).putExtra("type","offlinechallan"))
                        finish()
                    }else if (value == 4) {
                        startActivity(Intent(this@RestoreActivity, AVRWithtGate::class.java).putExtra("type","offlinechallan"))
                        finish()
                    }else if (value == 5) {
                        startActivity(Intent(this@RestoreActivity, PicUpAvr::class.java).putExtra("type","avrpickup"))
                        finish()
                    }
            }
            cp.dismiss()

        }
        binding.restore.setOnClickListener {
            lifecycleScope.launch {
                val deleteddata = ArrayList<String>()
                for (i in 0 until restorecnlist.size) {
                    if (!restorecnlist.get(i).restore) {
                        deleteddata.add(restorecnlist.get(i).cn)
                    }

                }
                db.barcodeDao().deleteuncheckdata(deleteddata)
                adapter.notifyDataSetChanged()
            }
            AlertDialog.Builder(this)
                .setTitle("Success")
                .setIcon(R.drawable.ic_success)
                .setMessage("Restore Data")
                .setCancelable(false)
                .setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog: DialogInterface, id: Int ->
                        if (value == 1)
                            startActivity(Intent(this, ChallanCreation::class.java))
                        else if (value == 2)
                            startActivity(Intent(this, AVR::class.java))
                        else if(value==3)
                            startActivity(Intent(this, ChallanCreation::class.java).putExtra("type","offlinechallan"))
                        else if(value==4)
                            startActivity(Intent(this, AVRWithtGate::class.java).putExtra("type","offlinechallan"))
                        else if (value == 5) {
                            startActivity(Intent(this@RestoreActivity, PicUpAvr::class.java).putExtra("type","avrpickup"))

                        }
                       dialog.dismiss()
                        finish()

                    })
                .show()
        }
        binding.deleteall.setOnClickListener {
            confirmation()
        }
        /*binding.cancel.setOnClickListener {
            lifecycleScope.launch {
                val deleteddata = ArrayList<String>()
                for (i in 0 until restorecnlist.size) {
                    if (!restorecnlist.get(i).restore) {
                        deleteddata.add(restorecnlist.get(i).cn)
                    }

                }
                db.barcodeDao().restoreselecteddata(deleteddata)
                adapter.notifyDataSetChanged()
            }
            if (value == 1)
                startActivity(Intent(this, ChallanCreation::class.java))
             else if (value == 3)
                startActivity(Intent(this, ChallanCreation::class.java).putExtra("type","offlinechallan"))
            else if (value == 2)
                startActivity(Intent(this, AVR::class.java))
           else if (value == 4)
                startActivity(Intent(this, AVRWithtGate::class.java))
            else if (value == 5) {
                startActivity(Intent(this@RestoreActivity, PicUpAvr::class.java).putExtra("type","avrpickup"))

            }
           else
            finish()
        }*/
        binding.cancel.setOnClickListener {
            lifecycleScope.launch {
                val deleteddata = ArrayList<String>()
                for (i in 0 until restorecnlist.size) {
                    if (!restorecnlist[i].restore) {
                        deleteddata.add(restorecnlist[i].cn)
                    }
                }
                db.barcodeDao().deleteuncheckdata(deleteddata)
                adapter.notifyDataSetChanged()

                // Now start activity after operation completes
                when (value) {
                    1 -> startActivity(Intent(this@RestoreActivity, ChallanCreation::class.java))
                    3 -> startActivity(Intent(this@RestoreActivity, ChallanCreation::class.java).putExtra("type", "offlinechallan"))
                    2 -> startActivity(Intent(this@RestoreActivity, AVR::class.java))
                    4 -> startActivity(Intent(this@RestoreActivity, AVRWithtGate::class.java))
                    5 -> startActivity(Intent(this@RestoreActivity, PicUpAvr::class.java).putExtra("type", "avrpickup"))
                    else -> finish()
                }
            }
        }

    }

    override fun chageststus(position: Int, restore: Boolean) {
        restorecnlist.set(
            position,
            RestoreCN(restorecnlist.get(position).cn, restorecnlist.get(position).box, restore)
        )
        adapter.notifyDataSetChanged()

    }

    fun confirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this).apply {
            setTitle("Delete")
            setMessage("Are you sure you want to delete ALL data? This action cannot be undone, and no backup will be created.")

            // Handle the "Yes" button
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    DeleteAllData()
                }
            }

            // Handle the "No" button
            setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }

            setCancelable(true)
        }.create().show()
    }

    fun DeleteAllData() {
        lifecycleScope.launch {
            restorecnlist.clear()
            adapter.notifyDataSetChanged()
            /* db.restorebarcodedao().deleteAllBarcodes()
             db.barcodeDao().deleteAllBarcodes()
             db.manualDao().DeleteAllManual()
             db.verifydao().deleteCN()*/
            withContext(Dispatchers.IO) {
                db.restorebarcodedao().deleteAllBarcodes()
                db.barcodeDao().deleteAllBarcodes()
                db.manualDao().DeleteAllManual()
                db.verifydao().deleteCN()
            }
            when (value) {
                1 -> {
                    startActivity(Intent(this@RestoreActivity, ChallanCreation::class.java))
                }
                3 -> {
                    startActivity(Intent(this@RestoreActivity, ChallanCreation::class.java).putExtra("type","offlinechallan"))
                }

                2 -> startActivity(Intent(this@RestoreActivity, AVR::class.java))
                4 -> startActivity(Intent(this@RestoreActivity, AVRWithtGate::class.java))
                5 -> startActivity(Intent(this@RestoreActivity, PicUpAvr::class.java).putExtra("type","avrpickup"))

                else -> finish()
            }


        }
    }
}