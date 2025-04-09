package com.example.omoperation.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
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
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.GatePlanAdapter
import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.gatepass.GatePassMod
import com.example.omoperation.model.gatepass.GatePassResp
import com.example.omoperation.model.gatepass.submitResp
import com.example.omoperation.model.gatepassin.GatePassInResp
import com.example.omoperation.model.savegatepass.Cnlist
import com.example.omoperation.model.savegatepass.SaveDataPassMod
import com.example.omoperation.model.tally.Detail
import com.example.omoperation.model.vehicle.VehicleValidateMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
//5k +700+

class LoadingPlanByGate : AppCompatActivity() , GatePlanAdapter.GatePassInterface {
    lateinit var destination : TextView

    lateinit var btn_validate : ImageButton
    lateinit var save : AppCompatButton
    lateinit var cn_text : EditText
    lateinit var searchBtn : ImageButton
    lateinit var searchByCode : ImageButton
    lateinit var recy_cn : RecyclerView
    lateinit var edt_lorry_type : EditText
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

    lateinit var adapter : GatePlanAdapter
    lateinit var myinterface : GatePlanAdapter.GatePassInterface
    lateinit var myresp :   kotlin.collections.ArrayList<com.example.omoperation.model.gatepass.Response>
    var vehiclenum=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorPrimaryDark)

        }
        setContentView(R.layout.activity_loading_plan_by_gate)
        myinterface=this

        init()


        searchByCode.setOnClickListener {
            val integrator = IntentIntegrator(this@LoadingPlanByGate)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan Barcode")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
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
        adapter= GatePlanAdapter(myresp!!,myinterface)
        searchBtn=findViewById(R.id.searchBtn)
        searchByCode=findViewById(R.id.searchByCode)
        destination=findViewById(R.id.destination)
        save=findViewById(R.id.save)
        btn_validate=findViewById(R.id.btn_validate)
        edt_lorry_type=findViewById(R.id.edt_lorry_type)
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
                if(!edt_lorry_type.text.toString().equals("")){
                    ValidateVehicle();
                }
                else{
                    Toast.makeText(this,"Add Vehicle Details",Toast.LENGTH_SHORT).show()
                }
            }
        }

        myresp= ArrayList()
        /* for(i in 500 until 502 )
           myresp.add(
               com.omlogistics.deepak.omlogistics.model.gatepass.Response(
                   i.toString(),
                   "",
                   i.toString(),
                   "",false))
           adapter= GatePlanAdapter(myresp!!,myinterface)
           recy_cn.adapter=adapter  //hard code*/

        //  destination.setText("1314")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
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
        }
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
                sacn_weight=sacn_weight+ myresp.get(i).ACT_WT!!.toDouble()
                myresp.set(i,
                    com.example.omoperation.model.gatepass.Response(
                        myresp.get(i).ACT_WT!!,
                        myresp.get(i).CHLN_NO!!,
                        myresp.get(i).CN_NO!!,myresp.get(i).PKG!!,
                        true))

                myresp.add(0, com.example.omoperation.model.gatepass.Response(
                    myresp.get(i).ACT_WT!!,
                    myresp.get(i).CHLN_NO!!,
                    myresp.get(i).CN_NO!!,myresp.get(i).PKG!!,
                    true))
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

    fun ValidateVehicle(){
        /*val url =
            AppConfig.BASE_URL+"/vehicle_validate.php?lorryno=" + edt_lorry_type!!.text.toString().toUpperCase(
                    Locale.US
                ) + "&type=image_upload"*/
        pd.show()
        val mod= CommonMod()
        mod.lorryno= edt_lorry_type!!.text.toString()
            .toUpperCase(
                Locale.US
            )
        mod.type="Challan"
        lifecycleScope.launch {
            val resp= ApiClient.getClient().create(ServiceInterface::class.java).vehicle_validate_checklist(Utils.getheaders(),mod)
            if(resp.code()==200){
                pd.dismiss()
                if(resp.body()!!.error.toString().equals("false"))
                {
                    isvehiclevalidate=true
                    vehiclenum=edt_lorry_type!!.text.toString().toUpperCase()
                    pd.show()
                    cn_text.requestFocus()
                    findData()
                }
                else  {
                    vehiclenum=""
                    isvehiclevalidate=false
                    Toast.makeText(
                        this@LoadingPlanByGate,
                        "Invalid Vehicle number",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
            else{
                pd.dismiss()
                Toast.makeText(
                    this@LoadingPlanByGate,
                    resp.message(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
    private fun findData() {
        val mod= GatePassMod()
        mod.status="search"
        mod.bcode=destination.text.toString()
        //mod.bcode="503"
        mod.vehNo= vehiclenum
        /* mod.bcode="3508"//destination.text.toString()   //hard code
         mod.vehNo="HR55L9921"//edt_lorry_type.text.toString() //hard code*/
        ApiClient.getClientsanchar().create(ServiceInterface::class.java).
        GetePass(Utils.getheaders(),mod).enqueue(object :Callback<GatePassResp>{
            override fun onResponse(
                call: Call<GatePassResp>,
                response: Response<GatePassResp>
            ) {
                pd.dismiss()
                if (response.body()!!.error.equals("false")) {
                    total_weight=0.0
                    sacn_weight=0.0
                    scan_gr_count=0
                    total_gr_count=0
                    total_gr_count= response.body()!!.response.size
                    barcodeCount.setText("Total GR = "+total_gr_count)

                    myresp= ArrayList()
                    for(i in (response.body()!!.response)){
                        myresp!!.add(com.example.omoperation.model.gatepass.Response(
                            i.ACT_WT!!,
                            i.CHLN_NO!!,
                            i.CN_NO!!,i.PKG!!,
                            false))
                        total_weight=total_weight+i.ACT_WT!!.toDouble()
                    }

                    adapter= GatePlanAdapter(myresp!!,myinterface)
                    recy_cn.adapter=adapter
                    tv_tot_weight.setText("Total Weight = "+total_weight)
                }
                else {
                    isvehiclevalidate=false
                    Utils.showDialog(
                        this@LoadingPlanByGate,
                        "Fail",
                        response.body()!!.message,
                        R.drawable.ic_error_outline_red_24dp
                    )


                }
            }

            override fun onFailure(call: Call<GatePassResp>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(
                    this@LoadingPlanByGate,
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
                sacn_weight=sacn_weight+ myresp.get(i).ACT_WT!!.toDouble()
                myresp.set(i,
                    com.example.omoperation.model.gatepass.Response(
                        myresp.get(i).ACT_WT!!,
                        myresp.get(i).CHLN_NO!!,
                        myresp.get(i).CN_NO!!,myresp.get(i).PKG!!,
                        true))

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
                    this@LoadingPlanByGate,
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
                mod.vehNo=vehiclenum
                mod.ENTER_BY=OmOperation.getPreferences(Constants.EMP_CODE,"")
                /* mod.bcode="3508" //hard code
                 mod.vehNo="HR55AK6826" //hard code
                 mod.ENTER_BY="26647" //hard code*/
                val cnlist=ArrayList<Cnlist>()
                for(i in myresp){
                    val mydata=Cnlist()
                    mydata.ACT_WT=i.ACT_WT
                    mydata.PKG=i.PKG
                    mydata.CHLN_NO=i.CHLN_NO
                    mydata.CN_NO=i.CN_NO
                    cnlist.add(mydata)
                }
                mod.cnlist=cnlist
                ApiClient.getClientsanchar().create(ServiceInterface::class.java).
                GetePassSubmit(Utils.getheaders(),mod).enqueue(object : Callback<submitResp>{
                    override fun onResponse(
                        call: Call<submitResp>,
                        response: Response<submitResp>
                    ) {
                        pd.show()
                        if (response.body()!!.error.equals("false")) {
                            Utils.showDialog(
                                this@LoadingPlanByGate,
                                "Success",
                                response.body()!!.response,
                                R.drawable.ic_success
                            )
                            myresp= ArrayList()
                            adapter= GatePlanAdapter(myresp!!,myinterface)
                            recy_cn.adapter=adapter
                        }
                        else {
                            Utils.showDialog(
                                this@LoadingPlanByGate,
                                "Fail",
                                response.body()!!.response,
                                R.drawable.ic_error_outline_red_24dp
                            )

                        }
                    }

                    override fun onFailure(call: Call<submitResp>, t: Throwable) {
                        pd.dismiss()
                        Utils.showDialog(
                            this@LoadingPlanByGate,
                            "Fail",
                            t.toString(),
                            R.drawable.ic_error_outline_red_24dp
                        )
                    }

                })


            }
            else{
                Utils.showDialog(
                    this@LoadingPlanByGate,
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
        val successAlert = android.app.AlertDialog.Builder(this@LoadingPlanByGate).create()
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

}