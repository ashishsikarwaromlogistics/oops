package com.example.omoperation.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.AVRAdapter
import com.example.omoperation.databinding.ActivityBarcodeScanningBinding
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.model.MIS
import com.example.omoperation.model.avr.Barcodelist
import com.example.omoperation.model.barcode_load.BarcodeMod
import com.example.omoperation.model.barcode_load.Cn
import com.example.omoperation.model.clientbox.ClientBoxMod
import com.example.omoperation.model.cnvaridate.CnValidateMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.Barcode
import com.example.omoperation.room.tables.CN
import com.example.omoperation.room.tables.RestoreBarcode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.LinkedList
import java.util.Locale
import java.util.concurrent.ExecutorService

class BarcodeScanning : AppCompatActivity() , AVRAdapter.RemoveBarcode, TextToSpeech.OnInitListener {
    lateinit var binding: ActivityBarcodeScanningBinding
    lateinit var  bundle : Bundle
    lateinit var  db : AppDatabase      //32*100----> 10dp;//280
    private lateinit var imei: String
    lateinit var barcodelist: ArrayList<String>
    lateinit var cnlist: LinkedList<String>
    lateinit var displayedData: ArrayList<String>
    lateinit var cp:CustomProgress
    lateinit var adapter: AVRAdapter
    lateinit var doc_remarks:String
    lateinit var doc:String
    lateinit var cnmap: HashMap<String,String>
    lateinit var cnmapbox: HashMap<String,Int>
    private lateinit var textToSpeech: TextToSpeech//
    var bajajlist= ArrayList<String>()
    var bajajlist_with_realtion= HashMap<String,String>()

    var totalweight: Double=0.0
    var isscroll=false
    var cureentgr=""
    var sealnumber=""//900 //  //  1090
    var lorryno=""//
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_barcode_scanning)
        //binding=DataBindingUtil.setContentView(this,R.layout.activity_avr)
        binding.tvtitle.setText("Challan"+OmOperation.getPreferences(Constants.BCODE,"")+"\n"+OmOperation.getPreferences(Constants.EMP_CODE,""))
        val toolbar: Toolbar = findViewById(R.id.toolbar)
      //  var bajajlist= ArrayList<String>()
       try{
           bajajlist= Gson().fromJson(
               OmOperation.getPreferences(Constants.BAJAJ, ""),
               object : TypeToken<ArrayList<String>>() {}.type
           )
           bajajlist_with_realtion= Gson().fromJson(
               OmOperation.getPreferences(Constants.bajajlist_with_realtion, ""),
               object : TypeToken<HashMap<String,String>>() {}.type
           )
       }
       catch (e:Exception){}

        writetextinback1("Barcode Scanning")
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, android.R.color.white))

        textToSpeech = TextToSpeech(this, this)

        imei=Utils.getDeviceID(this)
        setSupportActionBar(binding.toolbar)
        cp= CustomProgress(this)
        barcodelist= ArrayList()
        cnlist= LinkedList()
        cnmap= HashMap()
        cnmapbox= HashMap()
        displayedData= ArrayList()


        binding.recyAvr.setHasFixedSize(false)
        binding.recyAvr.layoutManager=LinearLayoutManager(this)
        adapter= AVRAdapter(this,this,displayedData)
        binding.recyAvr.adapter=adapter
        binding.barcodeText.setText("")
        binding.barcodeText.requestFocus()


        lifecycleScope.launch(Dispatchers.IO) {
            checkrestoredata()
        }
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.equals("")){
                    adapter.setfilter(false,null)
                }
                else{
                    if(newText!!.length>3){
                        lifecycleScope.launch {
                            adapter.setfilter(true,db.barcodeDao().getscan(newText!!) )
                        }
                    }


                }
                return true
            }



        })

        // in the below line, we are setting our imei to our text view.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = telephonyManager.imei
        }*/
       try{ imei=Utils.getDeviceIMEI(this)}
       catch (e:Exception){ imei="1234567"}
        imei=Utils.getDeviceIMEI(this)
        Log.d("ashish",imei)
       db=AppDatabase.getDatabase(this)
       // test()
       /* for (i in 0 until 1000){
            addtolocalbarcode("12345678${i}")
        }*/
        binding.addManualGRBtn.visibility=View.GONE
        binding.addManualGRBtn.setOnClickListener { startActivity(Intent(this,AddManual::class.java).putExtra("vno",lorryno)) }
        binding.barcodeText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 1) {
                    binding.barcodeText.text = null
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })//32628
        //        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BarcodeScanning.this);
//        alertDialog.setTitle("Notification");
        bundle = intent.extras!!
        lorryno= bundle.getString("lorry").toString()
        binding.submitBtn.setOnClickListener {
            val d = Dialog(this@BarcodeScanning)
            d.setContentView(R.layout.dialog_remarks)
            val spin = d.findViewById<Spinner>(R.id.spin_type)
            val msg = d.findViewById<TextView>(R.id.msg)
            val edtseal = d.findViewById<TextView>(R.id.edtseal)
            val edit_remarks = d.findViewById<EditText>(R.id.edit_remarks)
            val submit = d.findViewById<Button>(R.id.submit)
            msg.text = ("Total GRCount is " + binding.GRCount.getText().toString()) + ".Do you have all document regarding these GR?"
            val adapter =
                ArrayAdapter(this@BarcodeScanning, android.R.layout.simple_list_item_1, arrayOf("No", "YES"))
            spin.setAdapter(adapter)
            submit.setOnClickListener {
                if (edit_remarks.getText().toString()
                        .equals("", ignoreCase = true) && spin.selectedItemPosition == 0
                ) {
                    Utils.showDialog(
                        this@BarcodeScanning,
                        "Remarks Missing",
                        "Kindly add remarks",
                        R.drawable.ic_error_outline_red_24dp
                    )
                    return@setOnClickListener
                }
                else if (edtseal.getText().toString()
                        .equals("", ignoreCase = true) ) {
                    Utils.showDialog(
                        this@BarcodeScanning,
                        "Error",
                        "Please Add seal number",
                        R.drawable.ic_error_outline_red_24dp
                    )
                    return@setOnClickListener
                }
                sealnumber=edtseal.text.toString()
                doc_remarks=edit_remarks.text.toString()
                doc=spin.getSelectedItem().toString().substring(0, 1)
                submitData(null,null)

                d.dismiss()
            }
            d.show()

        }

        binding.barcodeText.setOnEditorActionListener { v, actionId, event ->
          //  if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 0) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                       validatebarcode()
                return@setOnEditorActionListener true
            } else {
                clearBarcodeEdittext()
                return@setOnEditorActionListener false
            }
        }

        binding.recyAvr.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == displayedData.size - 1) {
                    // Load more data
                    if(isscroll)
                        loadMoreData(2)
                    else isscroll=true
                   // loadMoreData(2)
                }
            }
        })

        binding.getmiss.setOnClickListener {
            startActivity(Intent(this@BarcodeScanning,MissingList::class.java))
        }




    }




    private fun validatebarcode() :Int {
        try {
            //viewmode.checkdata(binding.barcodeText.getText().toString().trim())
            var barCode = binding.barcodeText.getText().toString().trim()
            binding.barcodeText.setText("")
            /* if (barCode.startsWith("O")) {
                 barCode = barCode.substring(1, barCode.length)
                 barCode = Utils.revertTransform(barCode)
             }*/
            if (barCode.contains(getString(R.string.NBC_Sticker_Identification))) {
                val CustomerBarcode =
                    barCode.split(getString(R.string.NBC_Sticker_Identification).toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                barCode =
                    getString(R.string.NBC_Prefix) + CustomerBarcode[1] + CustomerBarcode[4]
            }
            else {
               /* if (barCode.startsWith("0")) {
                    barCode =
                        barCode.trim { it <= ' ' }.replaceFirst("^0+(?!$)".toRegex(), "")
                    val input = barCode
                    val breakspoints = input.split("~")
                    val breakspoint=breakspoints[0].split("-")
                    for (part in breakspoint) {
                        println(part)
                    }
                    if(breakspoint[1].length>4){
                        barCode=breakspoint[0]+breakspoint[1].substring(1,breakspoint[1].length)
                    }
                    else{
                        barCode=breakspoint[0]+ breakspoint[1]
                    }

                }
                else if (barCode.contains("-")) {
                    val input = barCode
                    val parts = input.split("-")

                    // Print each part
                    for (part in parts) {
                        println(part)
                    }

                    barCode=parts[1]+"0"+parts[2].substring(1,parts[2].length)
                    Log.d("ashish",barCode)
                    //barcode=00308H171013
                    *//* val builder = StringBuilder(barCode)
                     barCode = builder.deleteCharAt(builder.indexOf("-") + 1)
                         .deleteCharAt(builder.indexOf("-")).toString()
                         .replaceFirst("^0+(?!$)".toRegex(), "")*//*
                }*/
                if (barCode.contains("-")){
                    clearBarcodeEdittext()
                    val input = barCode
                    val parts = input.split("~")
                    if(bajajlist.contains(parts[0])){
                        showCustomBackgroundToast("Duplicate Barcode")
                        speak("Duplicate Barcode")
                    }
                    else  checkcnBajaj(parts[0])

                  //  clearBarcodeEdittext()
                    return 0
                }
                else{
                    barCode = barCode.substring(1, barCode.length)
                    barCode = Utils.revertTransform(barCode)
                    barCode=barCode.trimStart('0')
                }
            }
            if (barCode.isEmpty()) {
                binding.barcodeText.error = "Please Enter Barcode"
            }
            else if(barCode.length<9 || barCode.length>20){
                binding.barcodeText.error = "Please Enter valid Barcode"
            }
            else {
                if(barcodelist.contains(barCode)){
                    speak("Duplicate Barcode")
                    showCustomBackgroundToast("Duplicate Barcode")
                    // Toast.makeText(this@BarcodeScanning, "Duplicate Barcode", Toast.LENGTH_SHORT).show()
                }
                else if(cnlist.contains(barCode.substring(0,barCode.length-4))){
                    addtolocalbarcode(barCode)
                }
                else{
                    //  checkcn(barCode)
                    lifecycleScope.launch {
                        if(cureentgr.equals("")){
                            checkcn(barCode)
                        }//
                        else {
                            val a=  db.barcodeDao().getcurrengr(cureentgr)
                            if(cnmapbox.get(cureentgr)!!.toInt()>a){
                                speak("GR is missing")
                                val dailog=Dialog(this@BarcodeScanning)
                                dailog.setContentView(R.layout.error_dialog)
                                val img_text=dailog.findViewById<TextView>(R.id.img_text)
                                val msg_text=dailog.findViewById<TextView>(R.id.msg_text)
                                val ok_btn=dailog.findViewById<TextView>(R.id.ok_btn)
                                val print_btn=dailog.findViewById<TextView>(R.id.print_btn)
                                img_text.text = cureentgr+"\nSome boxes are missing "
                                msg_text.setText("Do you still want to continue")
                                ok_btn.setOnClickListener {

                                    checkcn(barCode)
                                    dailog.dismiss() }
                                print_btn.setOnClickListener { dailog.dismiss() }
                                dailog.show()
                            }
                            else checkcn(barCode)
                        }

                    }
                }

            }
        }
        catch (e: Exception) {
            Toast.makeText(this@BarcodeScanning, e.message, Toast.LENGTH_SHORT).show()
        }
        clearBarcodeEdittext()
        return 0
    }

    private fun checkcnBajaj(barCode: String) {
        cp.show()
        val input = barCode
        val parts = input.split("~")
        val mod = CnValidateMod().apply {
            bcode = OmOperation.getPreferences(Constants.BCODE, "")
            cn_no = parts[0]
            loading_plan = bundle.getString("loading_plan")
            from = intent.getStringExtra("from")
            to = bundle.getString("to")
        }
        ApiClient.getClient().create(ServiceInterface::class.java)
            .cn_validate_bajaj(Utils.getheaders(), mod)
            .enqueue(object : Callback<CommonRespS> {
                override fun onResponse(call: Call<CommonRespS>, response: Response<CommonRespS>) {
                    cp.dismiss()
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (body.error.equals("false", true)) {
                                if(barcodelist.contains(barCode)){}
                                else{
                                    bajajlist.add(barCode)//this will add barcode

                                    OmOperation.savePreferences(Constants.BAJAJ,Gson().toJson(bajajlist).toString())
                                    handleBajajResponse(barCode, body)
                                }

                            } else {

                                Utils.showDialog(
                                    this@BarcodeScanning,
                                    "Error ${response.code()}",
                                    body.response,
                                    R.drawable.ic_error_outline_red_24dp)
                            }
                        } ?: run {
                            Utils.showDialog(this@BarcodeScanning, "Error", "Response body is null", R.drawable.ic_error_outline_red_24dp)
                        }
                    } else {
                        Utils.showDialog(
                            this@BarcodeScanning,
                            "HTTP Error ${response.code()}",
                            response.message(),
                            R.drawable.ic_error_outline_red_24dp
                        )
                    }
                }

                override fun onFailure(call: Call<CommonRespS>, t: Throwable) {
                    cp.dismiss()
                    Utils.showDialog(this@BarcodeScanning, "Failure", t.localizedMessage ?: "Unknown error", R.drawable.ic_error_outline_red_24dp)
                }
            })

    }

    private fun clearBarcodeEdittext() {
        binding.barcodeText.setText("")
    }
   /* fun checkcn(barcode : String){
        if(Utils.haveInternet(this)){
           cp.show()
            val cn=barcode.substring(0,barcode.length-4)
            val mod= CnValidateMod()

            mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
            mod.cn_no=cn
            mod.loading_plan=bundle.getString("loading_plan")
            mod.from=intent.getStringExtra("from")//OmOperation.getPreferences(Constants.BCODE,"")
            mod.to=bundle.getString("to")
            ApiClient.getClient().create(ServiceInterface::class.java).
            cn_validate(Utils.getheaders(),mod).enqueue(object : Callback<CommonRespS> {
                override fun onResponse(
                    call: Call<CommonRespS>,
                    response: Response<CommonRespS>
                ) {
                    cp.dismiss()
                    if(response!=null){
                        if(response.code()==200){
                            if(response.body()?.error.equals("false",true)){
                                cureentgr=barcode.substring(0,barcode.length-4)
                                cnlist.add(barcode.substring(0,barcode.length-4))
                                binding.GRCount.setText(cnlist.size.toString())
                                cnmap.put(barcode.substring(0,barcode.length-4),response.body()!!.city.toString())
                                cnmapbox.put(barcode.substring(0,barcode.length-4),response.body()!!.pkg.toInt())
                                totalweight=totalweight+Utils.safedouble(response.body()!!.cn_wt.toString())
                                addtolocalbarcode(barcode)
                                lifecycleScope.launch {
                                    val cn= CN(challan = "", box =response.body()!!.pkg,cn =cn, city = response.body()!!.city?:"", weight = response.body()!!.cn_wt)
                                    db.verifydao().inserbarcode(cn)

                                }
                            }
                            else{

                                Utils.showDialog(this@BarcodeScanning,response.code().toString(),response.body()!!.response,R.drawable.ic_error_outline_red_24dp)

                            }
                        }
                        else{
                            Utils.showDialog(this@BarcodeScanning,response.code().toString(),response.message(),R.drawable.ic_error_outline_red_24dp)

                        }
                    }
                    else
                        Utils.showDialog(this@BarcodeScanning,"response is null ","Check Internet connection ",R.drawable.ic_error_outline_red_24dp)


                }

                override fun onFailure(call: Call<CommonRespS>, t: Throwable) {
                    cp.dismiss()
                    Utils.showDialog(this@BarcodeScanning,"onFailure","",R.drawable.ic_error_outline_red_24dp)
                }

            })
        }
    }*/
   fun checkcn(barcode: String) {

       if (!Utils.haveInternet(this)) {
           Utils.showDialog(this, "No Internet", "Please check your internet connection", R.drawable.ic_error_outline_red_24dp)
          return
       }
       cp.show()
       val cn = barcode.substring(0, barcode.length - 4)

       val mod = CnValidateMod().apply {
           bcode = OmOperation.getPreferences(Constants.BCODE, "")
           cn_no = cn
           loading_plan = bundle.getString("loading_plan")
           from = intent.getStringExtra("from")
           to = bundle.getString("to")
       }

       ApiClient.getClient().create(ServiceInterface::class.java)
           .cn_validate(Utils.getheaders(), mod)
           .enqueue(object : Callback<CommonRespS> {
               override fun onResponse(call: Call<CommonRespS>, response: Response<CommonRespS>) {
                   cp.dismiss()
                   if (response.isSuccessful) {
                       response.body()?.let { body ->
                           if (body.error.equals("false", true)) {
                               handleSuccessfulResponse(barcode, body)
                           }
                           /*else if(bundle.getString("to").equals("9999") &&
                               body.error.equals("true", true)&&
                               body.response.startsWith("E-way Bill Expired"))
                           {

                               AlertDialog.Builder(this@BarcodeScanning)
                                   .setTitle("Alert")
                                   .setIcon(android.R.drawable.ic_dialog_alert)
                                   .setMessage(response.body()?.response+". Do you still want to Add")
                                   .setCancelable(false)
                                   .setPositiveButton(
                                       "OK",
                                       DialogInterface.OnClickListener { dialog: DialogInterface?, id: Int -> dialog!!.dismiss()
                                           handleSuccessfulResponse(barcode, body)
                                       })
                                   .setNegativeButton(
                                       "Cancel",
                                       DialogInterface.OnClickListener { dialog: DialogInterface?, id: Int -> dialog!!.dismiss()
                                         //  handleSuccessfulResponse(barcode, body)
                                       })
                                   .show()
                           }*/
                           else {
                           Utils.showDialog(this@BarcodeScanning,"Error ${response.code()}",body.response,R.drawable.ic_error_outline_red_24dp)
                           }
                       } ?: run {
                           Utils.showDialog(this@BarcodeScanning, "Error", "Response body is null", R.drawable.ic_error_outline_red_24dp)
                       }
                   } else {
                       Utils.showDialog(this@BarcodeScanning,"HTTP Error ${response.code()}",response.message(),R.drawable.ic_error_outline_red_24dp)
                   }
               }

               override fun onFailure(call: Call<CommonRespS>, t: Throwable) {
                   cp.dismiss()
                   Utils.showDialog(this@BarcodeScanning, "Failure", t.localizedMessage ?: "Unknown error", R.drawable.ic_error_outline_red_24dp)
               }
           })
   }
    private fun handleBajajResponse(scanbarcode:String,  body: CommonRespS) {
        if(body.pkg.toInt() ==0){
            // Utils.showDialog(this,"")
            return
        }
        else{

            val cn =body.mcn
            cureentgr = cn


            cnmap[cn] = body.city ?: ""
            cnmapbox[cn] = body.pkg.toInt() ?: 0
            totalweight += Utils.safedouble(body.cn_wt ?: "0.0")




            lifecycleScope.launch {


                var   cnboxlist=  db.barcodeDao()gettotalboxofcn(cn)
                var mynextnumber=cn.toString()+"0001"
                val allNumbers = mutableSetOf<Long>()

                for(i in cnboxlist){
                    allNumbers.add(i.toLong())
                }

               try{
                   if (allNumbers.isEmpty()) mynextnumber =cn.toString()+"0001"
                   val min = allNumbers.minOrNull()!!
                   val max = allNumbers.maxOrNull()!!

                   // Check for missing numbers


                   // No missing, return next incremented number
                   var next = max + 1
                   while (allNumbers.contains(next)) {
                       next++ // just in case someone duplicated
                   }
                   mynextnumber=  next. toString()

                   for (i in min..max) {
                       if (i !in allNumbers) {
                           mynextnumber= i.toString() // first missing
                       }
                   }

                   if(!cnboxlist.contains(cn.toString()+"0001")){
                       mynextnumber=cn.toString()+"0001"
                   }

               }
               catch (e: Exception){}


                addtolocalbajaj(scanbarcode,mynextnumber,body.city.toString())
            }

            lifecycleScope.launch {
                if(cnlist.contains(cn)){
                   lifecycleScope.launch {
                       db.barcodeDao().appendToFindBox(cn,","+scanbarcode)
                   }
                }
                else{
                    totalweight += Utils.safedouble(body.cn_wt ?: "0.0")
                    binding.tvweight.setText("Weight:"+totalweight)
                     cnlist.add(cn)
                     binding.GRCount.setText(cnlist.size.toString())
                    val cnEntity = CN(
                        challan = "",
                        box = body.pkg,
                        cn = cn,
                        city = body.city ?: "",
                        weight = body.cn_wt,
                        findBox = scanbarcode
                    )
                    db.verifydao().inserbarcode(cnEntity)
                }

            }
        }
    }
    private fun addtolocalbajaj(scanbarcode: String,barcode: String,city:String) {
        if(displayedData.contains(barcode)){

        }
        else{
           // bajajlist_with_realtion.put(barcode,scanbarcode)//this will add barcode
            bajajlist_with_realtion[barcode]=scanbarcode//this will add barcode

          //  OmOperation.savePreferences(Constants.bajajlist_with_realtion,Gson().toJson(bajajlist_with_realtion).toString())
            OmOperation.savePreferences(Constants.bajajlist_with_realtion, Gson().toJson(bajajlist_with_realtion))
            writetextinback(city)
            speak(city)
            displayedData.add(0,barcode)
            barcodelist.add(0,barcode)
            binding.barcodeCount.setText(barcodelist.size.toString())
            adapter.notifyDataSetChanged()
            lifecycleScope.launch {
                val barcodem= Barcode(barcode=barcode , timestamp = Utils.getCurrentTimestamp() )
                db.barcodeDao().inserbarcode(barcodem)
                getcureentGR(barcode)
            }
            lifecycleScope.launch {
                val barcodem= RestoreBarcode(barcode=barcode )
                db.restorebarcodedao().inserbarcode(barcodem)

            }
          //  speak(cnmap.get(barcode.substring(0,barcode.length-4)).toString())
        }
    }
    private fun handleSuccessfulResponse(barcode: String, body: CommonRespS) {
        if(body.pkg.toInt() ==0){
           // Utils.showDialog(this,"")
          return
        }
        else{

            val cn = barcode.substring(0, barcode.length - 4)
            cureentgr = cn
            cnlist.add(cn)
            binding.GRCount.setText(cnlist.size.toString())


            cnmapbox[cn] = body.pkg.toInt() ?: 0
            try{
                cnmap[cn] = body?.city ?: ""
                totalweight += Utils.safedouble(body?.cn_wt ?: "0.0")

                lifecycleScope.launch {
                    val cnEntity = CN(
                        challan = "",
                        box = body.pkg,
                        cn = cn,
                        city = body?.city ?: "",
                        weight = body?.cn_wt?: "0.0"
                    )
                    db.verifydao().inserbarcode(cnEntity)
                }
               }
            catch (e: Exception){
                lifecycleScope.launch {
                    val cnEntity = CN(
                        challan = "",
                        box = body.pkg,
                        cn = cn,
                        city = "",
                        weight ="0"
                    )
                    db.verifydao().inserbarcode(cnEntity)
                }
                cnmap[cn] = "No city"

            }


            binding.tvweight.setText("weight:"+totalweight)
            addtolocalbarcode(barcode)


        }
    }

    private fun addtolocalbarcode(barcode: String) {
        if(displayedData.contains(barcode)){

        }
        else{
        displayedData.add(0,barcode)
        barcodelist.add(0,barcode)
        binding.barcodeCount.setText(barcodelist.size.toString())
        adapter.notifyDataSetChanged()
        lifecycleScope.launch {
            val barcodem= Barcode(barcode=barcode, timestamp = Utils.getCurrentTimestamp() )
            db.barcodeDao().inserbarcode(barcodem)
            getcureentGR(barcode)
        }
        lifecycleScope.launch {
            val barcodem= RestoreBarcode(barcode=barcode )
            db.restorebarcodedao().inserbarcode(barcodem)

        }
            writetextinback(cnmap.get(barcode.substring(0,barcode.length-4)).toString())
         speak(cnmap.get(barcode.substring(0,barcode.length-4)).toString())
        }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to US English
            val result = textToSpeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle error here
                // The language is not supported or the data is missing
            } else {
                //  speakButton.isEnabled = true
            }
        } else {
            // Initialization failed
        }
    }

    private fun speak(city: String) {
       try{
           textToSpeech.speak(city.lowercase(Locale.getDefault()), TextToSpeech.QUEUE_FLUSH, null, null)
       }
        catch (e: Exception){} }
    override fun onDestroy() {
        try{
            textToSpeech?.let {
                it.stop()
                it.shutdown()
            }
        }
        catch (e: Exception){}


        super.onDestroy()
    }

    fun submitData(status :Any?,remarks :Any?){
        cp.show()
        lifecycleScope.launch {
            val barcodelist = db.barcodeDao().getAll().map { barcodeEntity ->
                Barcodelist().apply {
                    barcode = barcodeEntity.barcode
                    time = barcodeEntity.timestamp
                }
            }

          //  val cnlist = db.barcodeDao().getcnboxes().map { cnentity ->
            val cnlist = db.barcodeDao().getcnboxesformiss().map { cnentity ->
                Cn().apply {
                    barcode = cnentity.box
                    cnNo = cnentity.cn
                    clienT_BOX_NO =cnentity.findBox

                }
            }

            val mod = BarcodeMod()
            mod.source =intent.getStringExtra("from")// OmOperation.getPreferences(Constants.BCODE, "")
            mod.remarks=remarks.toString()
           // mod.source = "1328"
            mod.destination = bundle.getString("to")
            mod.lorryNo = bundle.getString("lorry")
            mod.sealNo = sealnumber
            mod.driverName = bundle.getString("driverName")
            mod.driverMob = bundle.getString("driverMob")
            mod.touchingFlg = bundle.getString("touchingFlg")
            mod.weight = bundle.getString("_weight")
            mod.emp = OmOperation.getPreferences(Constants.EMP_CODE,"")
            mod.bcode = OmOperation.getPreferences(Constants.BCODE, "")
            mod.barcodelist = barcodelist
            mod.cnlist=cnlist
             //mod.imei=imei
             mod.imei=OmOperation.getPreferences2( Constants.SAVE_OLL, "")
            mod.loadingPlan = bundle.getString("loading_plan")
            mod.airbag = (bundle.getInt("airbag")).toString()
            mod.sheetbelt = (bundle.getInt("sheetbelt")).toString()
            mod.cargo = (bundle.getInt("cargo")).toString()
            mod.lashing = (bundle.getInt("lashingbelt")).toString()
            mod.isDoc=doc//send Y or N
            mod.docRemarks=doc_remarks //ADD REMARKS
            mod.setTouchingBranch(bundle.getString("touchingBranch"))
            mod.setRemarks(remarks.toString())
            mod.oda_station_code  =bundle.getString("oda_station_code")
            val URL: String = ServiceInterface.omapi + "barcode_loading.php?status=" + status
            val resp = withTimeoutOrNull(15000L) { // Set timeout to 15 seconds
                try {
                    ApiClient.getClient().create(ServiceInterface::class.java)
                        .loading_barcode(URL, Utils.getheaders(), mod)
                } catch (e: Exception) {
                    Utils.showDialog(this@BarcodeScanning,"error{${e.toString()}}","Net Connection is not working properly",R.drawable.ic_error_outline_red_24dp)
                    null // Return null on exception, e.g., network failure
                }
            }

            if (resp?.code() == 200) {
                cp.dismiss()
                if(resp.body()!!.error.toString().equals("false")){
                    Deletefrontend();
                    AlertDialog.Builder(this@BarcodeScanning)
                        .setTitle("Success")
                        .setIcon(R.drawable.ic_success)
                        .setMessage( resp.body()!!.response)
                        .setCancelable(false)
                        .setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { dialog: DialogInterface, id: Int ->
                                lifecycleScope.launch {
                                   /* db.barcodeDao().deleteAllBarcodes()
                                    db.manualDao().DeleteAllManual()
                                    //db.verifydao().deleteCN()
                                    val mybarcode=ArrayList<String>()
                                    val mycnlist=ArrayList<String>()
                                    for(i in barcodelist){
                                        mybarcode.add(i.barcode.toString())
                                        mycnlist.add(i.barcode.toString().substring(0,i.barcode.toString().length-4))
                                    }
                                    db.restorebarcodedao().delete_from_restore(mybarcode)
                                    db.verifydao().delete_from_verify(mycnlist)*/
                                    //db.().deleteAllBarcodes()


                                    val jsonString = OmOperation.getPreferences2(Constants.MISDATA, "[]")
                                    val listType = object : TypeToken<ArrayList<MIS>>() {}.type
                                    val nameObjects: ArrayList<MIS> = Gson().fromJson(jsonString, listType)
                                    nameObjects.add(MIS(OmOperation.getPreferences(Constants.EMP_CODE,""),
                                        OmOperation.getPreferences(Constants.EMPNAME,""),resp.body()!!.response,"challan"))
                                    val updatedJsonString = Gson().toJson(nameObjects)
                                    OmOperation.savePreferences2(Constants.MISDATA, updatedJsonString)
                                    dialog.dismiss()
                                    this@BarcodeScanning.finish()
                                }
                             })
                        .show()


                }
                else if(resp.body()!!.error.toString().equals("true")){
                     Utils.showDialog(
                        this@BarcodeScanning,
                        "error true",
                        resp.body()!!.response,
                        R.drawable.ic_error_outline_red_24dp
                    )
                }
                else{
                    showmissingDialogerror()
                }


            }
            else{
                cp.dismiss()
                Utils.showDialog(
                    this@BarcodeScanning,
                    "Fail",
                    "Data not submit due to break network Connection",
                    R.drawable.ic_error_outline_red_24dp
                )
            }


        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.barcode_scan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.missing -> {
                startActivity(Intent(this@BarcodeScanning,MissingList::class.java))
                true
            }
            R.id.clear ->{
                DeleteAllbarcode()
                true
            }
            R.id.duplicate ->{
                lifecycleScope.launch {
                    db.restorebarcodedao().deleteDuplicateBarcodes()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@BarcodeScanning, "Duplicate barcodes deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            R.id.bajajmiss ->{
                startActivity(Intent(this@BarcodeScanning, BajajMiss::class.java))

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showmissingDialogerror(){
        val warningAlert = android.app.AlertDialog.Builder(this@BarcodeScanning).create()
        warningAlert.setCancelable(false)
        val inflater1 =
            applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view2 = inflater1.inflate(R.layout.warning_dialog, findViewById(R.id.warning_dialog))
        warningAlert.setView(view2)
        val remarksEdittext = view2.findViewById<Spinner>(R.id.remarksEdittext)
        val remarksAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.challan_missing_remarks2,
            android.R.layout.simple_spinner_item
        )
        remarksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        remarksEdittext.setAdapter(remarksAdapter)

        val yes_btn = view2.findViewById<Button>(R.id.yes_btn)
        val excess_matereial = view2.findViewById<EditText>(R.id.excess_matereial)
        remarksEdittext.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (i == 2) {
                    excess_matereial.visibility = View.VISIBLE
                    excess_matereial.setInputType(InputType.TYPE_CLASS_NUMBER)
                } else {
                    excess_matereial.visibility = View.GONE
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        yes_btn.setOnClickListener { v: View? ->
            if (remarksEdittext.selectedItemPosition == 0) {
                Toast.makeText(this,"Please select remarks",Toast.LENGTH_SHORT).show()
            } else if (remarksEdittext.selectedItemPosition == 2 && excess_matereial.getText()
                    .toString().equals("", ignoreCase = true)
            ) {
                Utils.showDialog(this,"error","Please enter Material Excess with GR * box , example 12345*4",R.drawable.ic_error_outline_red_24dp)

            } else {
                val remarks =
                    remarksEdittext.getSelectedItem().toString().trim { it <= ' ' }
                     warningAlert.dismiss()
                //  submitChallan2(bar, "y", remarks);  new type
                submitData("y",remarks + "   " + excess_matereial.getText().toString()
                )
            }
        }
        val no_btn = view2.findViewById<Button>(R.id.no_btn)
        val btn_copy2 = view2.findViewById<Button>(R.id.btn_copy)
        btn_copy2.visibility = View.GONE
        no_btn.setOnClickListener { v: View? -> warningAlert.dismiss() }
        warningAlert.show()
    }
    private  fun checkrestoredata() {
        lifecycleScope.launch {
            val mybarcode= db.barcodeDao().getAll()
            if(mybarcode.isNotEmpty()){
                Utils.showDialog(this@BarcodeScanning,"Restore Data",
                    "Total Barcode is ${mybarcode.size.toString()}",R.drawable.ic_success)
                withContext(Dispatchers.IO) {
                    val list = mutableListOf<String>()
                    for (i in mybarcode) {
                        list.add(i.barcode.toString())
                    }
                    withContext(Dispatchers.Main) {
                        barcodelist.addAll(list)
                        displayedData.addAll(list)
                        loadMoreData(1)
                        binding.barcodeCount.setText(mybarcode.size.toString())
                    }
                }

            }
        }
        lifecycleScope.launch {
            val mycn = withContext(Dispatchers.IO) { db.verifydao().getAll() }
            val selectedcn = withContext(Dispatchers.IO) { db.barcodeDao().cnwithboxes().map { it.cn } }

            if (mycn.isNotEmpty()) {
                val list = mutableListOf<String>()

                for (i in mycn) {
                    val cn = i.cn.toString()
                    if (selectedcn.contains(cn)) {
                        list.add(cn)
                        cnmap[cn] = i.city.toString()
                        cnmapbox[cn] = i.box.toInt()
                        totalweight += Utils.safedouble(i.weight.toString())
                    }

                }

                cnlist.addAll(list)
                binding.GRCount.setText(cnlist.size.toString())
                binding.tvweight.setText("weight:"+totalweight)
            }
        }

        /*lifecycleScope.launch {
            val mycn= db.verifydao().getAll()
            val selectedcn= db.barcodeDao().cnwithboxes()
            val finlacn=  mutableListOf<String>()

                if(mycn.size>0){
                    withContext(Dispatchers.IO) {
                        val list = mutableListOf<String>()
                        for (i in mycn) {
                            if(selectedcn.contains(i.cn.toString()))
                            list.add(i.cn.toString())
                            cnmap.put(i.cn.toString(),i.city.toString())
                            cnmapbox.put(i.cn.toString(),i.box.toInt())
                            totalweight=totalweight+Utils.safedouble(i.weight.toString())
                        }
                        withContext(Dispatchers.Main) {
                            cnlist.addAll(list)
                            binding.GRCount.setText(cnlist.size.toString())
                        }
                    }

                }




            if(mycn.size>0){
                withContext(Dispatchers.IO) {
                    val list = mutableListOf<String>()
                    for (i in mycn) {
                        list.add(i.cn.toString())
                        cnmap.put(i.cn.toString(),i.city.toString())
                        cnmapbox.put(i.cn.toString(),i.box.toInt())
                        totalweight=totalweight+Utils.safedouble(i.weight.toString())
                    }
                    withContext(Dispatchers.Main) {
                        cnlist.addAll(list)
                        binding.GRCount.setText(cnlist.size.toString())
                    }
                }

            }
        }*/


    }
    var  currentPage=0;
    var  pageSize=50;
    fun loadMoreData(a : Int) {
       /* val start: Int = currentPage * pageSize
        val end: Int = Math.min(start + pageSize, barcodelist.size)
        if (start < end) {
            val newData: List<String> = barcodelist.subList(start, end)
            displayedData.addAll(newData)
            if(a==1){
                adapter= AVRAdapter(this,this,displayedData)
                binding.recyAvr.adapter=adapter
            }
            adapter.notifyDataSetChanged()
            currentPage++
        }*/
        adapter.notifyDataSetChanged()
    }
    fun DeleteAllbarcode(){
        val alertBox = android.app.AlertDialog.Builder(this@BarcodeScanning)
        alertBox.setMessage("Do you really want to Delete all Data").setCancelable(false)
            .setPositiveButton(
                "YES"
            ) { dialog, which ->
                displayedData.clear()
                barcodelist.clear()
                cnlist.clear()
                cnmap.clear()
                cnmapbox.clear()
                barcodelist.clear()
                OmOperation.savePreferences(Constants.BAJAJ,"")
                OmOperation.savePreferences(Constants.bajajlist_with_realtion,"")
                bajajlist.clear()
                binding.GRCount.setText("0")
                binding.barcodeCount.setText("0")
                adapter.notifyDataSetChanged()
                lifecycleScope.launch {
                    db.barcodeDao().deleteAllBarcodes()
                    db.manualDao().DeleteAllManual()
                    db.verifydao().deleteCN()
                    db.restorebarcodedao().deleteAllBarcodes()
                }

            }.setNegativeButton("NO", null)

        val alert = alertBox.create()
        alert.show()





    }
    fun Deletefrontend(){
        OmOperation.savePreferences(Constants.bajajlist_with_realtion,"")
        OmOperation.savePreferences(Constants.BAJAJ,"")
        barcodelist.clear()
        displayedData.clear()
        barcodelist.clear()
        cnlist.clear()
        cnmap.clear()
        cnmapbox.clear()
        binding.GRCount.setText("0")
        binding.barcodeCount.setText("0")
        adapter.notifyDataSetChanged()

    }

    override fun removebarcode(barcode: String) {
        val alertBox = android.app.AlertDialog.Builder(this@BarcodeScanning)
        alertBox.setMessage("Do you really want to remove this $barcode?").setCancelable(false)
            .setPositiveButton(
                "YES"
            ) { dialog, which ->
                try{
                    val a= bajajlist_with_realtion.get(barcode)
                    bajajlist.remove(a)
                    bajajlist_with_realtion.remove(barcode)
                    OmOperation.savePreferences(Constants.bajajlist_with_realtion, Gson().toJson(bajajlist_with_realtion))
                    OmOperation.savePreferences(Constants.BAJAJ,Gson().toJson(bajajlist))
                }
                catch (e:Exception){

                }
                barcodelist.remove(barcode)
                displayedData.remove(barcode)
                adapter.notifyDataSetChanged()
                lifecycleScope.launch {
                    db.barcodeDao().deleteBarcode(barcode)
                    db.restorebarcodedao().deleteBarcode(barcode)
                    var scancnbox:ArrayList<String>  = db.barcodeDao().checkcnexist(barcode.substring(0,barcode.length-4)) as ArrayList<String>
                    if(scancnbox.size==0){
                       val weight= db.verifydao().getweight(barcode.substring(0,barcode.length-4))
                        totalweight =totalweight- Utils.safedouble(weight ?: "0.0")
                        binding.tvweight.setText("weight:"+totalweight)


                        db.verifydao().deletecn(barcode.substring(0,barcode.length-4))

                        cnlist.remove(barcode.substring(0,barcode.length-4))
                        binding.GRCount.setText(cnlist.size.toString())

                    }

                    binding.barcodeCount.setText(barcodelist.size.toString())
                    adapter.notifyDataSetChanged()
                } }.setNegativeButton("NO", null)

        val alert = alertBox.create()
        alert.show()


    }
    fun getcureentGR(barcode: String){


        lifecycleScope.launch {
            val a=  db.barcodeDao().getcurrengr(barcode.substring(0,barcode.length-4))
            binding.crgr.setText(a.toString())
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitapp()
        }
        return true
    }

    fun exitapp(){
        androidx.appcompat.app.AlertDialog.Builder(this).apply {
            setTitle("Exit Challan")
            setMessage("Are you sure you want to exit from this Screen?")

            // Handle the "Yes" button
            setPositiveButton("Yes") { _, _ ->
                // Close the app
                finish()
            }

            // Handle the "No" button
            setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }

            setCancelable(true)
        }.create().show()
    }
    private fun showCustomBackgroundToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        val view = toast.view

        // Get the TextView from the default Toast view
        val textView = view?.findViewById<TextView>(android.R.id.message)
        textView?.setTextColor(Color.WHITE) // Change text color if needed

        // Change the background of the Toast
        view?.setBackgroundResource(R.drawable.toast_background)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
    private fun writetextinback1(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val text: TextView = layout.findViewById(R.id.toast_text)
        text.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.view = layout
        toast.show()
    }
    private fun writetextinback(message: String) {
       try{
           /*val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
           val view = toast.view

           // Get the TextView from the default Toast view
           val textView = view?.findViewById<TextView>(android.R.id.message)
           textView?.setTextColor(Color.WHITE) // Change text color if needed

           // Change the background of the Toast
           view?.setBackgroundResource(R.drawable.green_dark)
           toast.setGravity(Gravity.CENTER, 0, 0)
           toast.show()
*/
           val inflater = layoutInflater
           val layout = inflater.inflate(R.layout.custom_toast, null)
           val text: TextView = layout.findViewById(R.id.toast_text)
           text.text = message

           val toast = Toast(applicationContext)
           toast.duration = Toast.LENGTH_LONG
           toast.setGravity(Gravity.CENTER, 0, 0)
           toast.view = layout
           toast.show()
       }
       catch (e:Exception){}
        }
     fun test(){
        lifecycleScope.launch {
            val barcodelist = db.barcodeDao().getAll().map { barcodeEntity ->
                Barcodelist().apply {
                    barcode = barcodeEntity.barcode
                    time = barcodeEntity.timestamp
                }
            }

            val mybarcode=ArrayList<String>()
            val mycnlist=ArrayList<String>()

            for(i in barcodelist){
                mybarcode.add(i.barcode.toString())
                mycnlist.add(i.barcode.toString().substring(0,i.barcode.toString().length-4))
            }
            db.restorebarcodedao().delete_from_restore(mybarcode)
            db.verifydao().delete_from_verify(mycnlist)


        }
    }




}
//