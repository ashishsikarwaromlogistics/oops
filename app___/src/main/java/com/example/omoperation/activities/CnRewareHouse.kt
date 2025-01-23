package com.example.omoperation.activities

import android.app.AlertDialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ReWarehouseBarcodeListAdapter
import com.example.omoperation.databinding.ActivityCnRewareHouseBinding
import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.rewarehouse.BarcodeRe
import com.example.omoperation.model.rewarehouse.CnRe
import com.example.omoperation.model.rewarehouse.Detail
import com.example.omoperation.model.rewarehouse.ReWarehouseMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.LinkedList
import java.util.Locale

class CnRewareHouse : AppCompatActivity(), ReWarehouseBarcodeListAdapter.OnDeleteBarcodeListener  {
    lateinit var binding:ActivityCnRewareHouseBinding


    private var formattedDateTime: String? = null


    private var mAdapter: ReWarehouseBarcodeListAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private val retrofit: Retrofit? = null
    private var barcodeList: ArrayList<String>? = null
    private var verifycnlist: LinkedList<String>? = null

    private var reasoncodename: List<Detail>? = null
    private var cnList: LinkedHashSet<String>? = null

    private var GRRecords: HashMap<String, Int>? = null
    var barcodedatatime: HashMap<String, String>? = null

    var barcount: HashMap<String, Int>? = null
    var cnreason: HashMap<String, String>? = null
    var cncount: HashMap<String, Int>? = null

    var mp= HashMap<String, Int> ()
    private var speech: TextToSpeech? = null
    var restoreData: HashMap<String, String>? = null

    private val rsrncode= HashMap<String, String> ()
    val cp by lazy { CustomProgress(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil. setContentView(this,R.layout.activity_cn_reware_house)
        val  title : TextView = findViewById(R.id.title)
        title.setText("CN ReWare House")
        // DATE AND TIME
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formattedDateTime = dateFormat.format(currentDate)

        barcodeList = java.util.ArrayList()
        verifycnlist = LinkedList()
        GRRecords = java.util.HashMap()
        barcodedatatime = java.util.HashMap()
        cnreason = java.util.HashMap()
        cncount =
            java.util.HashMap<String, Int>()
        reasoncodename = java.util.ArrayList()
        mp =
            java.util.HashMap<String, Int>()


        cnList = java.util.LinkedHashSet()
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
        binding.barcodeText.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                try {
                    var barCode =
                         binding.barcodeText.getText().toString().trim { it <= ' ' }
                     binding.barcodeText.setText("")
                    if (barCode.startsWith("O")) {
                        barCode = barCode.substring(1, barCode.length)
                        barCode = Utils.revertTransform(barCode)
                    }
                    if (!barCode.isEmpty()) {
                        if (barCode.length > 9 && barCode.length <= 20) {
                            val CNNo = barCode.substring(0, barCode.length - 4)
                            if (verifycnlist!!.contains(CNNo)) {
                                if (!barcodeList!!.contains(barCode)) {
                                    addbarcodetolist(barCode)
                                } else {
                                    clearBarcodeEdittext()
                                    //show error barcode already exist
                                }
                            } else {
                                checkCN(barCode)
                            }
                        } else {
                            // showAlert("INVALID BARCODE", "You are trying to add a invalid barcode", barCode, 2,"Invalid Barcode");
                        }
                    }
                } catch (e: Exception) {
                    Utils.showDialog(this,"error",e.message,R.drawable.ic_error_outline_red_24dp)

                    e.printStackTrace()
                }
                clearBarcodeEdittext()
                return@setOnEditorActionListener true
            } else {
                clearBarcodeEdittext()
                return@setOnEditorActionListener false
            }
        }
        //disableSearchViewActionMode(binding.search)
        binding.search!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                mAdapter!!.filter.filter(query)
                return false
            }
        })

        binding.submitBtn.setOnClickListener(View.OnClickListener { if (Utils.haveInternet(this@CnRewareHouse)) submitReport() })

        binding.mRecyclerView!!.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        binding.mRecyclerView.setLayoutManager(mLayoutManager)
        mAdapter = ReWarehouseBarcodeListAdapter(barcodeList, this@CnRewareHouse, this)
        binding.mRecyclerView.setAdapter(mAdapter)
        speech = TextToSpeech(applicationContext) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                speech!!.setLanguage(Locale.US)
            }
        }
    }
    private fun addbarcodetolist(barCode: String) {
        barcodeList!!.add(barCode)
        val valu = barCode.substring(0, barCode.length - 4)
        mp.put( valu, mp!!.get(valu)!!.toInt()+1 )
        updateListView()
    }

    private fun clearBarcodeEdittext() {
         binding.barcodeText.requestFocus()
         binding.barcodeText.setText("")
    }
    private fun checkCN(barcode: String) {


        val mod = CommonMod()
        mod.emp_code==OmOperation.getPreferences(Constants.EMP_CODE,"")//(data.get(3))
        mod.cn_no=(barcode.substring(0, barcode.length - 4))
        mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
        mod.status="reason"
        lifecycleScope.launch {
         val response= ApiClient.getClient().create(ServiceInterface::class.java).cnValidateRewh(Utils.getheaders(), mod)
        if(response.code()==200){
            if (response.body()!!.error.equals("false")) {
                verifycnlist!!.add(barcode.substring(0, barcode.length - 4))
                mp.put(
                    barcode.substring(0, barcode.length - 4),
                    0
                )
                // addBarcodeToList(CN, Integer.parseInt(response.body().getPkg().trim()),response.body().getCity()==null?"":response.body().getCity());
                val reasonlist: List<Detail> = response.body()!!.getDetail()
                val reasoncode = java.util.ArrayList<String>()
                val reasonname = java.util.ArrayList<String>()
                for (i in reasonlist.indices) {
                    reasoncode.add(reasonlist[i].reasonCode)
                    reasonname.add(reasonlist[i].reasonName)
                    rsrncode.put(
                        reasonlist[i].reasonName, reasonlist[i].reasonCode
                    )
                }
                val selectedItem = arrayOfNulls<String>(1)
                val builder = AlertDialog.Builder(this@CnRewareHouse)
                builder.setTitle("Select an Item")
                // Set items to display in the list
                builder.setItems(
                    reasonname.toTypedArray<String>()
                ) { dialog, which ->
                    // Handle item click
                    selectedItem[0] = reasonname[which]
                    val rscode: String = rsrncode.get(selectedItem[0]).toString()
                    Log.d("Item Select", rscode)
                    cnreason!![barcode.substring(0, barcode.length - 4)] = rscode
                    Log.d(barcode.substring(0, barcode.length - 4), rscode)
                    // Do something with the selected item
                }

                // Create and show the dialog
                val alertDialog = builder.create()
                alertDialog.show()
                addbarcodetolist(barcode)
                clearBarcodeEdittext()
            } else {
                clearBarcodeEdittext()
                // showAlert("ERROR FOUND",response.body().getResponse(), null, 2,CN.substring(0, CN.length() - 4));
                //  ShowToast.showCenterLongToast(ReWarehouseScanning.this, response.body().getResponse());
            }
        }

     }

    }

    private fun submitReport() {
        cp.show()
        val barcodelists: MutableList<BarcodeRe> = java.util.ArrayList<BarcodeRe>()
        for (i in barcodeList!!.indices) {
            val re = BarcodeRe()
            re.setBarcode(barcodeList!![i])
            re.setTime(formattedDateTime)
            barcodelists.add(re)
        }
        // Collections.sort(barcodeList);
        // countFreq(barcodeList,barcodeList.size());
        val cnre: MutableList<CnRe> = java.util.ArrayList<CnRe>()
        for (i in verifycnlist!!.indices) {
            val keyy = verifycnlist!![i]
            val reason = cnreason!![keyy]
            val barcount: Int =
                mp.get(keyy)!!.toInt()
            val rw = CnRe()
            rw.setCNNo(keyy)
            // rw.setBarcode(barcount.toString());
            if (barcount != null) {
                rw.setBarcode(barcount.toString())
            } else {
                rw.setBarcode("26")
            }
            rw.setReason(reason)
            cnre.add(rw)


        }
        val mod = ReWarehouseMod()
        mod.setBcode(OmOperation.getPreferences(Constants.BCODE,""))
        mod.setEmpCode(OmOperation.getPreferences(Constants.EMP_CODE,""))
        val imeinum: String = Utils.getDeviceID(this)
        mod.setImei(imeinum)
        mod.setBarcodelist(barcodelists)
        mod.setCnlist(cnre)
        lifecycleScope.launch {
           val response= ApiClient.getClient().create(ServiceInterface::class.java).cnRewh(Utils.getheaders(),mod)
      if(response.code()==200){
          cp.dismiss()
          if(response.body()!!.error.equals("false")){
              Utils.showDialog(this@CnRewareHouse,"Success","",R.drawable.ic_success)
                //showAlert("SUCCESS", response.body().getResponse(), null, 1, "")
          } else {
              Utils.showDialog(this@CnRewareHouse,"error","",R.drawable.ic_error_outline_red_24dp)
              //  showAlert("ERROR OCCURRED", response.body().getResponse(), null, 2, "")
          }
      }
            else cp.dismiss()
        }


    }
    private fun updateListView() {
        runOnUiThread {
            Collections.reverse(barcodeList)
            mAdapter = ReWarehouseBarcodeListAdapter(barcodeList, this, this)
            binding.mRecyclerView!!.setAdapter(mAdapter)
            mAdapter!!.notifyDataSetChanged()
        }
    }


    override fun onDelete(barcode: String, lastGR: String) {
        val res = barcodeList!!.remove(barcode)
        if (res) {
            //barcodeCount.setText(String.valueOf(barcodeList.size()));
            //GRCount.setText(String.valueOf(barcodeList.size() == 0 ? "0" : AppController.getIntanceHandler().getCurrentGRCount(lastGR)));
            val CN_NO = barcode.substring(0, barcode.length - 4)

        } else {
            Toast.makeText(this, "can't remove", Toast.LENGTH_SHORT).show()
        }
//        this.mAdapter.notifyDataSetChanged();
        //        this.mAdapter.notifyDataSetChanged();
        updateListView()
    }
}