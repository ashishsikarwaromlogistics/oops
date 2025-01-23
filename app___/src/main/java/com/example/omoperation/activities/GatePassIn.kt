package com.example.omoperation.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.model.gatepass.submitResp
import com.example.omoperation.model.gatepassin.GatePassInMod
import com.example.omoperation.model.gatepassin.GatePassInResp
import com.example.omoperation.model.savegatepass.Cnlist
import com.example.omoperation.model.savegatepass.SaveDataPassMod
import com.example.omoperation.model.tally.Detail
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.adapters.GatePassInAdap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.Locale

class GatePassIn : AppCompatActivity() , GatePassInAdap.GatePassInterface {



    lateinit var destination : TextView

    lateinit var btn_validate : ImageButton
    lateinit var save : AppCompatButton
    lateinit var cn_text : EditText
    lateinit var searchBtn : ImageButton
    lateinit var searchByCode : ImageButton
    lateinit var recy_cn : RecyclerView
    lateinit var edt_gate_pass : EditText
    var items     = arrayOf("Branch", "Hub/Region", "All","Route")

    lateinit var pd : ProgressDialog
    lateinit var detail: ArrayList<Detail>
    lateinit var barcodeCount: TextView
    lateinit var    GRCount: TextView
    lateinit var    tv_tot_weight: TextView
    lateinit var    tv_sac_weight: TextView
    var    scan_gr_count: Int=0
    var    total_gr_count: Int=0
    var    total_weight: Double=0.0
    var    sacn_weight: Double=0.0
    var  isvehiclevalidate=false

    lateinit var adapter : GatePassInAdap
    lateinit var myinterface : GatePassInAdap.GatePassInterface
    lateinit var myresp :   kotlin.collections.ArrayList<com.example.omoperation.model.gatepassin.Response>
    var gatepass_num=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorPrimaryDark)

        }
        setContentView(R.layout.activity_gate_pass_in)
        myinterface=this

        init()


        searchByCode.setOnClickListener {
           /* val integrator = IntentIntegrator(this@GatePassIn)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan Barcode")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()*/
        }


        searchBtn.setOnClickListener {
            SearchCN()
        }
        cn_text.setOnEditorActionListener(TextView.OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED &&  cn_text.text.toString().length>0) {
                SearchCN()
                cn_text.setText("")
                cn_text.requestFocus()
                closekeyboard()
                return@OnEditorActionListener true
            } else {
                cn_text.setText("")
                cn_text.requestFocus()
                return@OnEditorActionListener true
            }
        })

        save.setOnClickListener {
            SubmitGatePass();
        }

    }
    private var speech: TextToSpeech? = null
    private fun init(){
        pd= ProgressDialog(this)
        speech = TextToSpeech(applicationContext) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                speech!!.setLanguage(Locale.UK)
            }
        }
        cn_text=findViewById(R.id.cn_text)
        /*  try {
              cn_text!!.showSoftInputOnFocus=false
              cn_text.setOnClickListener {  cn_text!!.showSoftInputOnFocus=true}
          }catch (e : Exception){}*/
        myresp= ArrayList()
        adapter= GatePassInAdap(myresp!!,myinterface)
        searchBtn=findViewById(R.id.searchBtn)
        searchByCode=findViewById(R.id.searchByCode)
        destination=findViewById(R.id.destination)
        save=findViewById(R.id.save)
        btn_validate=findViewById(R.id.btn_validate)
        edt_gate_pass=findViewById(R.id.edt_gate_pass)
        recy_cn=findViewById(R.id.recy_cn)
        recy_cn.setHasFixedSize(true)
        recy_cn.layoutManager= LinearLayoutManager(this)
        destination.setText(OmOperation.getPreferences(Constants.BCODE,""))
        //  destination.setText("1338")


        barcodeCount=findViewById(R.id.barcodeCount)
        GRCount=findViewById(R.id.GRCount)

        tv_tot_weight=findViewById(R.id.tv_tot_weight)
        tv_sac_weight=findViewById(R.id.tv_sac_weight)


        btn_validate.setOnClickListener {
            if(Utils.haveInternet(this)){
                if(!edt_gate_pass.text.toString().equals("")){

                    findData()
                }
                else{
                    Toast.makeText(this,"Add Gate Number", Toast.LENGTH_SHORT).show()
                }
            }
        }

        myresp= ArrayList()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                cn_text.setText(result.contents)
                cn_text.setSelection(result.contents.length)
                SearchCN()
                //  Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
                //                getCNDetails();
            }
        }*/
    }
    private fun speak(msg: String) {
        speech!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
    }
    var checkcnlist=kotlin.collections.ArrayList<String>()
    fun SearchCN(){
        var isfind : Boolean=false
        var currentCn=""
        for(i in 0 until myresp.count()){
            currentCn= cn_text.text.toString()
            if(cn_text.text.toString().equals(myresp.get(i).CN_NO) && !myresp.get(i).isChecked!! ){
                checkcnlist.add(cn_text.text.toString())
                isfind=true
                scan_gr_count=scan_gr_count+1
                sacn_weight=sacn_weight+ myresp.get(i).CN_ACT_WT!!.toDouble()
                myresp.set(i,
                    com.example.omoperation.model.gatepassin.Response(
                        myresp.get(i).CN_ACT_WT!!,
                        myresp.get(i).CN_NO!!,
                        myresp.get(i).CHALLAN_NO!!,
                        myresp.get(i).CN_PKG!!,
                        true
                    )
                )

                myresp.add(0, com.example.omoperation.model.gatepassin.Response(
                    myresp.get(i).CN_ACT_WT!!,
                    myresp.get(i).CN_NO!!,
                    myresp.get(i).CHALLAN_NO!!,
                    myresp.get(i).CN_PKG!!,
                    true
                )
                )
                myresp.removeAt(i+1)
                break
            }
        }
        if(isfind){
            isfind=false
            speak("OK ")
            cn_text.setText("")
            cn_text.requestFocus()
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
            cn_text.setText("")
            cn_text.requestFocus()
        }
        tv_sac_weight.setText("Scanned Weight = "+sacn_weight)
        GRCount.setText("Scanned GR = "+scan_gr_count)
        adapter.notifyDataSetChanged()
    }


    private fun findData() {
        gatepass_num=edt_gate_pass.text.toString()
        val mod= GatePassInMod()
        mod.status="searchgatein"
        mod.bcode=destination.text.toString()//"3508"//
        mod.gatePassNo=gatepass_num//"350823000880" //

        // mod.bcode="1920"
        //  mod.gatePassNo="19202320009167"
        //  mod.bcode="3508"//
        //mod.gatePassNo="350823000880" //
        ApiClient.getClientsanchar().create(ServiceInterface::class.java).
        GetePass(Utils.getheaders(),mod).enqueue(object : Callback<GatePassInResp>{
            override fun onResponse(
                call: Call<GatePassInResp>,
                response: Response<GatePassInResp>
            ) {
                pd.dismiss()
                if(response.body()!!.error.equals("false")) {
                    total_weight=0.0
                    sacn_weight=0.0
                    scan_gr_count=0
                    total_gr_count=0
                    total_gr_count= response.body()!!.response.size
                    barcodeCount.setText("Total GR = "+total_gr_count)

                    myresp= ArrayList()
                    for(i in (response.body()!!.response)){
                        myresp!!.add(
                            com.example.omoperation.model.gatepassin.Response(
                                i.CN_ACT_WT!!,
                                i.CN_NO!!,
                                i.CHALLAN_NO!!,
                                i.CN_PKG!!,
                                false
                            )
                        )
                        total_weight=total_weight+i.CN_ACT_WT!!.toDouble()
                    }

                    adapter= GatePassInAdap(myresp!!,myinterface)
                    recy_cn.adapter=adapter
                    tv_tot_weight.setText("Total Weight = "+total_weight)
                } else {
                    isvehiclevalidate=false
                    Utils.showDialog(
                        this@GatePassIn,
                        "Fail",
                        response.body()!!.message,
                        R.drawable.ic_error_outline_red_24dp
                    )


                }
            }

            override fun onFailure(call: Call<GatePassInResp>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(
                    this@GatePassIn,
                    t.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })


    }

    override fun GatePassValue(cn_num : String) {
        for(i in 0 until myresp.count()){
            if(cn_num.equals(myresp.get(i).CN_NO) && !myresp.get(i).isChecked!! ){
                scan_gr_count=scan_gr_count+1
                sacn_weight=sacn_weight+ myresp.get(i).CN_ACT_WT!!.toDouble()
                myresp.set(i,
                    com.example.omoperation.model.gatepassin.Response(
                        myresp.get(i).CN_ACT_WT!!,
                        myresp.get(i).CN_NO!!,
                        myresp.get(i).CHALLAN_NO!!, myresp.get(i).CN_PKG!!,
                        true
                    )
                )

            }
        }
        tv_sac_weight.setText("Scanned Weight = "+sacn_weight)
        GRCount.setText("Scanned GR = "+scan_gr_count)
        adapter.notifyDataSetChanged()
    }

    private fun closekeyboard(){
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
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


    protected fun SubmitGatePass() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Submit")
        builder.setMessage("Do you really want to submit this?")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            if(myresp.size==0){

                Utils.showDialog(
                    this@GatePassIn,
                    "Fail",
                    "Data not found...",
                    R.drawable.ic_error_outline_red_24dp
                )
            }
            else if(total_gr_count==scan_gr_count && myresp.size>0){
                pd.show()
                val mod= SaveDataPassMod()
                mod.status="genGatePass"
                mod.bcode=destination.text.toString()
                mod.vehNo=gatepass_num
                mod.ENTER_BY=OmOperation.getPreferences(Constants.EMP_CODE,"")
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
                    cnlist.add(mydata)
                }
                mod.cnlist=cnlist
                ApiClient.getClientsanchar().create(ServiceInterface::class.java).GetePassSubmit(Utils.getheaders(),mod)
                    .enqueue(object : Callback<submitResp>{
                        override fun onResponse(
                            call: Call<submitResp>,
                            response: Response<submitResp>
                        ) {
                            pd.show()
                            if (response.body()!!.error.equals("false")) {
                                Utils.showDialog(
                                    this@GatePassIn,
                                    "Success",
                                    response.body()!!.response,
                                    R.drawable.ic_success
                                )
                                myresp= ArrayList()
                                adapter= GatePassInAdap(myresp!!,myinterface)
                                recy_cn.adapter=adapter
                            }
                            else {

                                Utils.showDialog(
                                    this@GatePassIn,
                                    "Fail",
                                    response.body()!!.response,
                                    R.drawable.ic_error_outline_red_24dp
                                )

                            }
                        }

                        override fun onFailure(call: Call<submitResp>, t: Throwable) {
                            pd.dismiss()
                            Utils.showDialog(
                                this@GatePassIn,
                                "Fail",
                                t.toString(),
                                R.drawable.ic_error_outline_red_24dp
                            )
                        }

                    })


            }
            else{
                Utils.showDialog(
                    this@GatePassIn,
                    "Fail",
                    "Please Checked all boxes",
                    R.drawable.ic_error_outline_red_24dp
                )

            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()

        }
        builder.show()
    }

    fun showAlert(title: String?, msg: String, bar: String?, flag: Int) {
        val successAlert = android.app.AlertDialog.Builder(this@GatePassIn).create()
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
              //  img1.setImageDrawable(resources.getDrawable(R.drawable.ic_cancel_black_24dp))
                val img_text1 = view1.findViewById<TextView>(R.id.img_text)
                img_text1.text = title
                val msg_text = view1.findViewById<TextView>(R.id.msg_text)
                msg_text.text = bar
                if (bar != null) {
                    msg_text.visibility = View.VISIBLE
                } else {
                    msg_text.visibility = View.GONE
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

}