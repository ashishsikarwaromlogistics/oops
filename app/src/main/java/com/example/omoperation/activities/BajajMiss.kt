package com.example.omoperation.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.CustomProgress
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.activities.BarcodeScanning
import com.example.omoperation.adapters.MyExpandableListAdapter
import com.example.omoperation.databinding.ActivityBajajMissBinding
import com.example.omoperation.model.barcode_load.Cn
import com.example.omoperation.model.clientbox.ClientBoxMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.room.AppDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class BajajMiss : AppCompatActivity() {
    lateinit var cp:CustomProgress
    lateinit var  db : AppDatabase
    lateinit var  binding : ActivityBajajMissBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= DataBindingUtil.setContentView(this,R.layout.activity_bajaj_miss)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db=AppDatabase.getDatabase(this)
        cp= CustomProgress(this)
        checkmissing()
    }
    fun checkmissing(){

        cp.show()
        lifecycleScope.launch {


            val cnlist = db.barcodeDao().getcnboxesformiss().map { cnentity ->
                Cn().apply {
                    barcode = cnentity.box
                    cnNo = cnentity.cn
                    clienT_BOX_NO = cnentity.findBox


                }
            }

            val mod = ClientBoxMod(cnlist)


            val URL: String = ServiceInterface.omapi + "find_client_missing_box.php"
            val resp = withTimeoutOrNull(15000L) { // Set timeout to 15 seconds
                try {
                    ApiClient.getClient().create(ServiceInterface::class.java)
                        .find_client_missing_box(URL, Utils.getheaders(), mod)
                } catch (e: Exception) {
                    Utils.showDialog(this@BajajMiss,"error{${e.toString()}}","Net Connection is not working properly",R.drawable.ic_error_outline_red_24dp)
                    null // Return null on exception, e.g., network failure
                }
            }

            if (resp?.code() == 200) {
                cp.dismiss()
                if(resp.body()!!.error.toString().equals("false")){
                    val cnList1 = resp.body()?.cn_list
                    val cnnum = mutableListOf<String>()
                    val listDataChild: HashMap<String, List<String>> = hashMapOf()
                    for (i in 0 until cnList1!!.size) {
                        val headervalue=cnList1.get(i).cn_no+"--"+resp.body()?.cn_list!!.get(i).missingpkg
                          cnnum.add(headervalue)
                          val clientBarcode = cnList1.get(i).client_barcode
                          val items = clientBarcode.split(",")
                        listDataChild.put(headervalue,items)

                    }


                    if (!cnList1.isNullOrEmpty()) {


                      /*  val listDataHeader = listOf("Fruits", "Vegetables", "Beverages")

                        val listDataChild = hashMapOf(
                            "Fruits" to listOf("Apple", "Banana", "Mango"),
                            "Vegetables" to listOf("Carrot", "Broccoli", "Spinach"),
                            "Beverages" to listOf("Water", "Juice", "Soda")
                        )*/

                        val adapter2 = MyExpandableListAdapter(this@BajajMiss, cnnum, listDataChild)
                        binding.expandableListView.setAdapter(adapter2)

                      //  val clientBarcode = cnList[0].client_barcode ?: ""
                      //  val items = clientBarcode.split(",")
                      /*  val adapter = ArrayAdapter(
                            this@BajajMiss,                     // Context
                            R.layout.simple_list_item_1,    // Custom layout with a TextView
                            R.id.label,              // ID of TextView inside that layout
                            cnnum                    // List of items
                        )
                        binding.list.adapter = adapter
                        binding.list.setOnItemClickListener { _, _, position, _ ->
                            val selectedItem = cnList1[position].cn_no
                              val clientBarcode = cnList1[position].client_barcode ?: ""
                               val items = clientBarcode.split(",")
                            val adapter = ArrayAdapter(
                                this@BajajMiss,                     // Context
                                R.layout.simple_list_item_1,    // Custom layout with a TextView
                                R.id.label,              // ID of TextView inside that layout
                                items                    // List of items
                            )
                            binding.list2.adapter = adapter
                        }*/
                    }
                }
                else if(resp.body()!!.error.toString().equals("true")){
                    Utils.showDialog(
                        this@BajajMiss,
                        "error true",
                       "data not found",
                        R.drawable.ic_error_outline_red_24dp
                    )
                }



            }
            else{
                cp.dismiss()
                Utils.showDialog(
                    this@BajajMiss,
                    "Fail",
                    "Data not submit due to break network Connection",
                    R.drawable.ic_error_outline_red_24dp
                )
            }


        }

    }
}