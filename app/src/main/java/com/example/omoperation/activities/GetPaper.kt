package com.example.omoperation.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.GatePassInAdap
import com.example.omoperation.databinding.ActivityUnloadingPlanBinding
import com.example.omoperation.model.gatepass.submitResp
import com.example.omoperation.model.gatepassin.GatePassInMod
import com.example.omoperation.model.gatepassin.Response
import com.example.omoperation.model.savegatepass.Cnlist
import com.example.omoperation.model.savegatepass.SaveDataPassMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.NetworkState
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.viewmodel.GetPaperViewMod
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import java.util.Locale

class GetPaper : AppCompatActivity()  , GatePassInAdap.GatePassInterface {
    lateinit var binding : ActivityUnloadingPlanBinding
    lateinit var myviewmod: GetPaperViewMod
    var    scan_gr_count: Int=0
    var    total_gr_count: Int=0
    var    total_weight: Double=0.0
    var    sacn_weight: Double=0.0
    var  isvehiclevalidate=false


    lateinit var pd : ProgressDialog
    lateinit var adapter : GatePassInAdap
    lateinit var myinterface : GatePassInAdap.GatePassInterface
    lateinit var myresp :   kotlin.collections.ArrayList<Response>

    var LORRY_NO=""
    private var speech: TextToSpeech? = null
    var checkcnlist=kotlin.collections.ArrayList<String>()
    lateinit var cp: CustomProgress



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.  setContentView(this, R.layout.activity_unloading_plan)
        init()


        myviewmod=ViewModelProvider(this).get(GetPaperViewMod::class.java)
        binding.viewmod=myviewmod
        binding.lifecycleOwner=this
      /*  binding.search.setOnClickListener {
            if(Utils.haveInternet(this)){
              //  lifecycleScope.launch {  myviewmod.finddata() }

               // lifecycleScope.launch {  finddata()  }

            }
        }
*/


        myviewmod.responsedata.observe(this, Observer {
            when(it){
                is NetworkState.Error -> {
                    cp.dismiss()
                    isvehiclevalidate=false
                    Utils.showDialog(this,it.title,it.message, R.drawable.ic_error_outline_red_24dp)

                }
                is NetworkState.Success<*> -> {
                    cp.dismiss()
                    val response=it.data as List<Response>
                    total_weight=0.0
                    sacn_weight=0.0
                    scan_gr_count=0
                    total_gr_count=0
                    total_gr_count= response.size
                    binding.barcodeCount.setText("Total GR = "+total_gr_count)
                    binding.edtGatePass.isEnabled=false
                    myresp= ArrayList()
                    if(response.size>0){
                        LORRY_NO=response.get(0).LORRY_NO.toString()
                        for(i in (response)){
                            myresp!!.add(Response(
                                i.CN_ACT_WT!!,
                                i.CN_NO!!,
                                i.CHALLAN_NO!!,
                                i.CN_PKG!!,
                                false))
                            total_weight=total_weight+i.CN_ACT_WT!!.toDouble()
                        }
                    }

                    adapter= GatePassInAdap(myresp,myinterface)
                    binding.recyCn.adapter=adapter
                    binding.tvTotWeight.setText("Total Weight = "+total_weight)
                }

                NetworkState.Loading -> {
                    cp.show()
                }

            }

        })

        binding.save.setOnClickListener { SubmitGatePass() }
        binding.searchBtn.setOnClickListener {
            if(myresp.size>0) SearchCN()
            else Utils.showDialog(this@GetPaper,"error","Data not found",R.drawable.ic_error_outline_red_24dp)

    }
        binding.searchByCode.setOnClickListener {
            if(myresp.size>0){
                val integrator = IntentIntegrator(this@GetPaper)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                integrator.setPrompt("Scan Barcode")
                integrator.setCameraId(0)
                integrator.setBeepEnabled(true)
                integrator.setBarcodeImageEnabled(true)
                integrator.initiateScan()
            }
            else Utils.showDialog(this@GetPaper,"error","Data not found",R.drawable.ic_error_outline_red_24dp)

        }
    }
    fun init(){
        cp=CustomProgress(this)
          pd= ProgressDialog(this)
        myinterface=this
        binding.destination.text=OmOperation.getPreferences(Constants.BCODE,"")
        binding.recyCn.setHasFixedSize(true)
        binding.recyCn.layoutManager=LinearLayoutManager(this)
        myresp= java.util.ArrayList()
        speech = TextToSpeech(applicationContext) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                speech!!.setLanguage(Locale.UK)
            }
        }

      /*  binding.cnText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 1) {
                    binding.cnText.setText(null)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
*/
        binding.cnText.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                if(myresp.size>0){
                   if(binding.cnText.text.toString().trimStart('0').equals(""))
                       Log.d("ashish","value blank")
                       else
                    SearchCN()
                }
                else Utils.showDialog(this,"error","Data not found",R.drawable.ic_error_outline_red_24dp)
                true // Return true to consume the event
            } else {
                false // Return false to let the system handle the action
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                binding.cnText.setText(result.contents)
                binding.cnText.setSelection(result.contents.length)
                SearchCN()
                //  Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
                //                getCNDetails();
            }
        }
    }
    private suspend fun finddata() {
        pd.show()
        val mod= GatePassInMod()
        mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
        mod.status="searchGetPaper"
        mod.gatePassNo=binding.edtGatePass.text.toString()
       val response= ApiClient.getClientsanchar().create(ServiceInterface::class.java).searchGetPaper(Utils.getheaders(),mod)
      if(response.code()==200){

          if(response.body()?.error.equals("false")){
              total_weight=0.0
              sacn_weight=0.0
              scan_gr_count=0
              total_gr_count=0
              total_gr_count= response.body()!!.response.size
              binding.barcodeCount.setText("Total GR = "+total_gr_count)

              myresp= ArrayList()
              for(i in (response.body()!!.response)){
                  myresp!!.add( Response(
                      i.CN_ACT_WT!!,
                      i.CN_NO!!,
                      i.CHALLAN_NO!!,
                      i.CN_PKG!!,
                      false))
                  total_weight=total_weight+i.CN_ACT_WT!!.toDouble()
              }
              adapter= GatePassInAdap(myresp,myinterface)
              binding.recyCn.adapter=adapter
              binding.tvTotWeight.setText("Total Weight = "+total_weight)
          }
          else{
              isvehiclevalidate=false
              Utils.showDialog(this,"error true",response.body()?.message,R.drawable.ic_error_outline_red_24dp)

          }
                }
        else {
          Utils.showDialog(this,"error code"+response.code(),response.message(),R.drawable.ic_error_outline_red_24dp)

      }
     pd.dismiss()

    }
    protected fun SubmitGatePass() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Submit")
        builder.setMessage("Do you really want to submit this?")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            if(myresp.size==0){
                Utils.showDialog(
                    this@GetPaper,
                    "Fail",
                    "Data not found...",
                    R.drawable.ic_error_outline_red_24dp
                )
            }
            else if(total_gr_count==
                scan_gr_count && myresp.size>0){
                submitData()
            }
            else{
                android.app.AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setIcon(R.drawable.ic_error_outline_red_24dp)
                    .setMessage("GR Diff. is "+(total_gr_count-scan_gr_count).toString()+" . Do you still want to submit")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog: DialogInterface, id: Int ->
                        submitData()
                        dialog.dismiss()
                    }
                    .setNeutralButton("Cancel") { dialog: DialogInterface, id: Int ->
                        dialog.dismiss()
                    }

                    .show()
                /*Utils.showDialog(
                    this@GetPaper,
                    "Alert",
                    "GR Diff. is "+(total_gr_count-scan_gr_count).toString()+"Do you still want to submit",
                    R.drawable.ic_error_outline_red_24dp
                )*/

            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()

        }
        builder.show()
    }

    private fun submitData() {
        val mod= SaveDataPassMod()
        mod.status="getPaperIn"
        mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
        mod.vehNo=LORRY_NO
        mod.ENTER_BY=OmOperation.getPreferences(Constants.EMP_CODE,"")
        mod.gateInNo=binding.edtGatePass.text.toString()
        /* mod.bcode="3508" //hard code
         mod.vehNo="HR55AK6826" //hard code
         mod.ENTER_BY="26647" //hard code*/
        val cnlist=ArrayList<Cnlist>()
        for(i in myresp){
            val mydata= Cnlist()
            mydata.ACT_WT=i.CN_ACT_WT
            mydata.PKG=i.CN_PKG
            mydata.CHLN_NO=i.CHALLAN_NO
            mydata.CN_NO=i.CN_NO
            if(i.isChecked == true){
                cnlist.add(mydata)
            }

        }
        mod.cnlist=cnlist

        pd.show()
        val call = ApiClient.getClientsanchar().create(ServiceInterface::class.java).GetePassSubmit(Utils.getheaders(),mod)
        call.enqueue(object : Callback<submitResp> {
            override fun onResponse(
                call: Call<submitResp>,
                response: retrofit2.Response<submitResp>
            ) {
                pd.dismiss()
                if (response.body()!!.error.equals("false")) {
                    Utils.showDialog(
                        this@GetPaper,
                        "Success",
                        response.body()!!.response,
                        R.drawable.ic_success
                    )
                    myresp= ArrayList()
                    adapter= GatePassInAdap(myresp!!,myinterface)
                    binding.recyCn.adapter=adapter
                } else {

                    Utils.showDialog(
                        this@GetPaper,
                        "Fail",
                        response.body()!!.response,
                        R.drawable.ic_error_outline_red_24dp
                    )

                }

            }
            override fun onFailure(call: Call<submitResp>, t: Throwable) {
                //   TODO("Not yet implemented")
                pd.dismiss()
                Utils.showDialog(
                    this@GetPaper,
                    "Fail",
                    t.toString(),
                    R.drawable.ic_error_outline_red_24dp
                )
            }
        })
    }

    override fun GatePassValue(cn_num: String) {
        for(i in 0 until myresp.count()){
            if(cn_num.equals(myresp.get(i).CN_NO) && !myresp.get(i).isChecked!! ){
                scan_gr_count=scan_gr_count+1
                sacn_weight=sacn_weight+ myresp.get(i).CN_ACT_WT!!.toDouble()
                myresp.set(i,
                    Response(
                        myresp.get(i).CN_ACT_WT!!,
                        myresp.get(i).CN_NO!!,
                        myresp.get(i).CHALLAN_NO!!,myresp.get(i).CN_PKG!!,
                        true))

            }
        }
        binding.tvSacWeight.setText("Scanned Weight = "+sacn_weight)
        binding.GRCount.setText("Scanned GR = "+scan_gr_count)
        adapter.notifyDataSetChanged()
    }
    fun SearchCN(){
        var isfind : Boolean=false
        var currentCn=binding.cnText.text.toString().trimStart('0')
        for(i in 0 until myresp.count()){
           // currentCn=
            if(binding.cnText.text.toString().trimStart('0').equals(myresp.get(i).CN_NO) && !myresp.get(i).isChecked!! ){
                checkcnlist.add(binding.cnText.text.toString().trimStart('0'))
                isfind=true
                scan_gr_count=scan_gr_count+1
                sacn_weight=sacn_weight+ myresp.get(i).CN_ACT_WT!!.toDouble()
                myresp.set(i,
                    Response(
                        myresp.get(i).CN_ACT_WT!!,
                        myresp.get(i).CN_NO!!,
                        myresp.get(i).CHALLAN_NO!!,
                        myresp.get(i).CN_PKG!!,
                        true))

                myresp.add(0,  Response(
                    myresp.get(i).CN_ACT_WT!!,
                    myresp.get(i).CN_NO!!,
                    myresp.get(i).CHALLAN_NO!!,
                    myresp.get(i).CN_PKG!!,
                    true))
                myresp.removeAt(i+1)
                break
            }
        }
        if(isfind){
            isfind=false
            speak("OK ")
            binding.cnText.setText("")
            binding.cnText.requestFocus()
        }
        else{
            if(checkcnlist.contains(currentCn)){
                speak("Already SCANNED")
                showAlert("Already Scanned","CN Number\n"+currentCn,currentCn,2)
            }
            else{
                speak("nOT IN LIST ")
                showAlert("Not in List ","CN Number\n"+currentCn,currentCn,2)
            }
            binding.cnText.setText("")
            binding.cnText.requestFocus()
        }
        binding.tvSacWeight.setText("Scanned Weight = "+sacn_weight)
        binding.GRCount.setText("Scanned GR = "+scan_gr_count)
        adapter.notifyDataSetChanged()
    }
    fun showAlert(title: String?, msg: String, bar: String?, flag: Int) {
        val successAlert = android.app.AlertDialog.Builder(this@GetPaper).create()
        successAlert.setCancelable(false)
        val inflater =
            applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view1 = inflater.inflate(R.layout.error_dialog, findViewById(R.id.success_dialog))
        successAlert.setView(view1)
        when (flag) {

            2 -> {
                //  speak(msg)
                val header1 = view1.findViewById<RelativeLayout>(R.id.header)
                header1.setBackgroundColor(Color.parseColor("#8B0000"))
               // val img1 = view1.findViewById<ImageView>(R.id.img)
                //img1.setImageDrawable(resources.getDrawable(R.drawable.ic_cancel_black_24dp))
                val img_text1 = view1.findViewById<TextView>(R.id.img_text)
                img_text1.text = title
                val barcode_text1 = view1.findViewById<TextView>(R.id.msg_text)
                barcode_text1.text = bar
                if (bar != null) {
                    barcode_text1.visibility = View.VISIBLE
                } else {
                    barcode_text1.visibility = View.GONE
                }
                val msg_text1 = view1.findViewById<TextView>(R.id.msg_text)
                msg_text1.setTextColor(Color.parseColor("#8B0000"))
                msg_text1.text = msg
                val ok1 = view1.findViewById<Button>(R.id.ok_btn)
                val btn_print = view1.findViewById<Button>(R.id.print_btn)
                btn_print.visibility = View.GONE
                ok1.setBackgroundColor(Color.parseColor("#8B0000"))
                ok1.setOnClickListener { view: View? -> successAlert.dismiss() }
                successAlert.show()
            }

            else -> {}
        }
    }
    private fun speak(msg: String) {
        speech!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking()
            exitByBackKey()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }


    protected fun exitByBackKey() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Do you really want to exit from this screen?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
            finish()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()

        }
        builder.show()
    }
}