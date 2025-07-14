package com.example.omoperation.activities

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.activities.BarcodeScanning
import com.example.omoperation.adapters.AVRAdapter
import com.example.omoperation.adapters.CustomGrList
import com.example.omoperation.databinding.ActivityAvrBinding
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.model.MIS
import com.example.omoperation.model.cnvalidate.CnValidateResp
import com.example.omoperation.model.cnvaridate.CnValidateMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.NetworkState
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.Barcode
import com.example.omoperation.room.tables.CN
import com.example.omoperation.room.tables.RestoreBarcode
import com.example.omoperation.viewmodel.AvrViewMod
import com.example.omoperation.model.avr.AvrMod
import com.example.omoperation.model.avr.AvrResp
import com.example.omoperation.model.avr.Barcodelist
import com.example.omoperation.model.avr.Cnlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.LinkedList
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AVR : AppCompatActivity() , AVRAdapter.RemoveBarcode, TextToSpeech.OnInitListener,
    CustomGrList.ADDGRREMOVE {
    lateinit var binding : ActivityAvrBinding
    lateinit var cp : CustomProgress
    @Inject
    lateinit var viewmode : AvrViewMod
    lateinit var barcodelist: ArrayList<String>
    lateinit var grdoc:List<kotlin.collections.ArrayList<String>>
    lateinit var grwithotdoc:ArrayList<kotlin.collections.ArrayList<String>>
    lateinit var cnlist: LinkedList<String>
    lateinit var cnmap: HashMap<String,String>
    lateinit var cnmapbox: HashMap<String,Int>
    lateinit var displayedData: ArrayList<String>
    lateinit var adapter: AVRAdapter
    lateinit var db: AppDatabase
    lateinit var pd: ProgressDialog
    lateinit var doc_remarks:String
    var cureentgr:String=""
    lateinit var doc:String
    private lateinit var textToSpeech: TextToSpeech
   var isscroll=false
    var isgateverify=false

    var bajajlist= ArrayList<String>()
    var bajajlist_with_realtion= HashMap<String,String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_avr)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, android.R.color.white))
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
        textToSpeech = TextToSpeech(this, this)
        //binding=new DataBindingU setContentView(R.layout.activity_avr);
        setSupportActionBar(binding.toolbar)
      // viewmode=ViewModelProvider(this).get(AvrViewMod::class.java)
        db= AppDatabase.getDatabase(this)
        binding.tvTitle.setText("AVR "+OmOperation.getPreferences(Constants.BCODE,"")+"\n"+OmOperation.getPreferences(Constants.EMP_CODE,""))
        binding.viewmod=viewmode
        binding.lifecycleOwner=this
        barcodelist= ArrayList()
        cnlist= LinkedList()
        cnmap= HashMap()
        cnmapbox= HashMap()
        displayedData= ArrayList()
        pd= ProgressDialog(this)

     /*   for(i in 1 until 1000){
            lifecycleScope.launch {
                val barcodem= RestoreBarcode(barcode="12345678${i}" )
                db.restorebarcodedao().inserbarcode(barcodem)

            }
        }*/
        init()
        observeViewModel()
        binding.recyAvr.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == displayedData.size - 1) {
                    // Load more data
                   if(isscroll)
                    loadMoreData(2)
                    else isscroll=true
                }
            }
        })
        binding.submitBtn.setOnClickListener {
            submitData()
        }
        binding.addBtn.setOnClickListener {
          /*  val intent = Intent(this, OcrScanAct::class.java)
            resultLauncher.launch(intent)
            startActivityForResult(Intent(this,OcrScanAct::class.java),1)
        */}

        //binding.gateNo.setText("13932420005664")//1309241025421
      /*  var cn="1309241025422"
        for (currentBox in 2 until 101) {
            var bar: String =
                if (currentBox < 10) {
                cn + "000" + currentBox
            } else if (currentBox >= 10 && currentBox < 100) {
                cn + "00" + currentBox
            } else if (currentBox >= 100 && currentBox < 1000) {
                cn + "0" + currentBox
            } else {
                cn + currentBox
            }

           testlocalbarcode(bar)
        }*/

       // submitData()

    }

    private   fun submitData() {
       /* if(binding.gateNo.text.toString().equals("")|| binding.lorryNo.text.toString().equals("")){
            Utils.showDialog(
                this@AVR,"error ","Gate Entry is not verified",
                R.drawable.ic_error_outline_red_24dp
            )
            return
        }*/
        val d = Dialog(this@AVR)
        d.setContentView(R.layout.dialog_remarks)
        val spin = d.findViewById<Spinner>(R.id.spin_type)
        val selectgr = d.findViewById<TextView>(R.id.selectgr)
        val msg = d.findViewById<TextView>(R.id.msg)
        val edit_remarks = d.findViewById<EditText>(R.id.edit_remarks)
        val submit = d.findViewById<Button>(R.id.submit)
        msg.text = ("Total GRCount is " + binding.GRCount.getText()
            .toString()) + ".Do you have all document regarding these GR?"
        val adapter =
            ArrayAdapter(this@AVR, android.R.layout.simple_list_item_1, arrayOf("No", "YES"))
        spin.setAdapter(adapter)
        grdoc=ArrayList()
        grwithotdoc=ArrayList()
       lifecycleScope.launch {
           grdoc = db.barcodeDao().getcnlischallan().map { cnentity ->
              ArrayList<String>().apply {
                  add(cnentity.cn.toString()) // Add cnentity.cn to the ArrayList
              }
          }

      }
        selectgr.setOnClickListener {
            val d=Dialog(this)
            d.setContentView(R.layout.selected_gr_d)
            val window = d.window
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

// Optionally, hide the status bar and make it immersive
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

// If you want to hide navigation bar as well:
            window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            val listView=d.findViewById<ListView>(R.id.listView)
            val tvok=d.findViewById<TextView>(R.id.tvok)
            val checkedItems = MutableList(grdoc.size) { false }

// Create an ArrayAdapter to bind the data to the ListView
           // val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, grwithotdoc)
            val adapter = CustomGrList(this,this, grdoc, checkedItems)
            listView.adapter = adapter
// Set the adapter to the ListView
            listView.adapter = adapter
            tvok.setOnClickListener {
                if(grwithotdoc.size>0)
                selectgr.setText(grwithotdoc.toString())
                else  selectgr.setText("Click here to get GR List")
                d.dismiss() }
            d.show()
        }

        submit.setOnClickListener {
            if (grwithotdoc.size==0 && spin.selectedItemPosition == 0
            ) {
                Utils.showDialog(
                    this@AVR,
                    "Remarks Missing",
                    "Kindly add remarks",
                    R.drawable.ic_error_outline_red_24dp
                )
                return@setOnClickListener
            }
            doc_remarks=edit_remarks.text.toString()
            doc=spin.getSelectedItem().toString().substring(0, 1)
            subimtData( null, null)

            d.dismiss()
        }
        d.show()

    }

    fun subimtData(missing: Any?, remarks: Any?){

        lifecycleScope.launch {
            if(Utils.haveInternet(this@AVR)){
                cp.show()
                val barcodelist = db.barcodeDao().getAll().map { barcodeEntity ->
                    Barcodelist().apply {
                        barcode = barcodeEntity.barcode
                        time = barcodeEntity.timestamp
                    }
                }

                val cnlist = db.barcodeDao().getcnlischallanmis().map { cnentity ->
                    Cnlist().apply {
                        barcode = cnentity.boxes
                        CN_No = cnentity.cn
                        CHALLAN_NO = cnentity.challan
                        CLIENT_BOX_NO = cnentity.find_box
                        paperstatus=if (grwithotdoc.any { it.contains(cnentity.cn) })  "N" else "Y"
                    }
                }

                val mod= AvrMod()
                mod.barcodelist=barcodelist
                mod.cnlist=cnlist
                mod.avr_seal=binding.spinSeal.text.toString()  //OK,BROKEN
                mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
                mod.emp=OmOperation.getPreferences(Constants.EMP_CODE,"")
                mod.gate_no=binding.gateNo.text.toString()
              //  mod.imei= Utils.getDeviceIMEI(this@AVR)
                mod.imei= OmOperation.getPreferences2( Constants.SAVE_OLL, "")
                mod.missing_status=missing//if packet is short give remarks otherwise null
                mod.remarks=remarks//if packet is short give remarks otherwise null
                mod.airbag=binding.edtAir.text.toString()
                mod.sheetbelt=binding.edtSheet.text.toString()
                mod.cargo=binding.edtCargo.text.toString()
                mod.lashingbelt=binding.edtLashing.text.toString()
                mod.status="unloading"
                mod.vehicle_no=binding.lorryNo.text.toString()
                mod.doc=doc//send Y or N
                mod.doc_remarks=doc_remarks //ADD REMARKS
                ApiClient.getClient().create(ServiceInterface::class.java).AVR_SUBMIT_CHALLAN(Utils.getheaders(),mod)
                    .enqueue(object :Callback<AvrResp>{
                        override fun onResponse(call: Call<AvrResp>, response: Response<AvrResp>) {
                            cp.dismiss()
                            if(response.code()==200){
                                if(response.body()!!.error.equals("false")){
                                    Deletefrontend()
                                   // Utils.showDialog(this@AVR,"Success",response.body()!!.response,R.drawable.ic_success)
                                    android.app.AlertDialog.Builder(this@AVR)
                                        .setTitle("Success")
                                        .setIcon(R.drawable.ic_success)
                                        .setMessage(response.body()!!.response)
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog: DialogInterface, id: Int ->

                                                dialog.dismiss()
                                            DeleteAllbarcode()
                                            finish()})
                                        .show()
                                    try{
                                        val jsonString = OmOperation.getPreferences2(Constants.MISDATA, "[]")
                                        val listType = object : TypeToken<ArrayList<MIS>>() {}.type
                                        val nameObjects: ArrayList<MIS> = Gson().fromJson(jsonString, listType)
                                        nameObjects.add(
                                            MIS(OmOperation.getPreferences(Constants.EMP_CODE,""),
                                                OmOperation.getPreferences(Constants.EMPNAME,""),response.body()!!.response,"challan")
                                        )
                                        val updatedJsonString = Gson().toJson(nameObjects)
                                        OmOperation.savePreferences2(Constants.MISDATA, updatedJsonString)
                                    }
                                    catch (e:Exception){}


                                }
                                else if(response.body()!!.error.equals("true")){
                                    Utils.showDialog(this@AVR,"error",response.body()!!.response,R.drawable.ic_error_outline_red_24dp)
                                }
                                else{
                                    showmissingDialogerror()
                                }
                            }
                            else{
                                Utils.showDialog(this@AVR,response.code().toString(),response.message(),R.drawable.ic_error_outline_red_24dp)
                            }}

                        override fun onFailure(call: Call<AvrResp>, t: Throwable) {
                            cp.dismiss()
                            Utils.showDialog(this@AVR,"onFailure","",R.drawable.ic_error_outline_red_24dp)

                        }

                    })
            }
        }


    }
   fun showmissingDialogerror(){
       val warningAlert = android.app.AlertDialog.Builder(this@AVR).create()
       warningAlert.setCancelable(false)
       val inflater1 =
           applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
       val view2 = inflater1.inflate(R.layout.warning_dialog, findViewById(R.id.warning_dialog))
       warningAlert.setView(view2)
       val remarksEdittext = view2.findViewById<Spinner>(R.id.remarksEdittext)
       val remarksAdapter = ArrayAdapter.createFromResource(
           this,
           R.array.challan_missing_remarks,
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
               subimtData("y",remarks + "   " + excess_matereial.getText().toString()
               )
           }
       }
       val no_btn = view2.findViewById<Button>(R.id.no_btn)
       val btn_copy2 = view2.findViewById<Button>(R.id.btn_copy)
       btn_copy2.visibility = View.GONE
       no_btn.setOnClickListener { v: View? -> warningAlert.dismiss() }
       warningAlert.show()
   }

   fun init() {
       cp= CustomProgress(this)
       binding.recyAvr.setHasFixedSize(true)
       binding.recyAvr.layoutManager=LinearLayoutManager(this)
       val sealOptions = arrayOf("OK", "BROKEN")

       binding.spinSeal.setOnClickListener {
           val items = arrayOf<CharSequence>("OK", "BROKEN")
           // }
           val builder = AlertDialog.Builder(this@AVR)
           builder.setItems(items) { dialog: DialogInterface, item: Int ->
               binding.spinSeal.text = items[item]
               dialog.dismiss()
           }
           builder.show()
       }

       lifecycleScope.launch {
           cp.show()
           checkrestoredata()
           cp.dismiss()
       }
       adapter= AVRAdapter(this,this,displayedData)
       binding.recyAvr.adapter=adapter

      binding.edtAir.setOnClickListener(View.OnClickListener {
            opend(
                binding.edtAir
            )
        })
      binding.edtLashing.setOnClickListener(View.OnClickListener { opend(binding.edtLashing) })
      binding.edtSheet.setOnClickListener(View.OnClickListener { opend(binding.edtSheet) })
      binding.edtCargo.setOnClickListener(View.OnClickListener { opend(binding.edtCargo) })
       binding.search.setOnQueryTextListener(object : OnQueryTextListener,
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


       binding.barcodeText.addTextChangedListener(object : TextWatcher {
           override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
           override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
               if (charSequence.length == 1) {
                   binding.barcodeText.text = null
               }
           }

           override fun afterTextChanged(editable: Editable) {}
       })
       binding.barcodeText.setOnEditorActionListener { v, actionId, event ->
           if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 0) {
               try {
                   //viewmode.checkdata(binding.barcodeText.getText().toString().trim())
                   var barCode = binding.barcodeText.getText().toString().trim()
                   binding.barcodeText.setText("")
                   val avr = binding.gateNo.getText().toString().trim()
                   binding.barcodeText.setText("")
                   /*if (barCode.startsWith("O")) {
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
                      /*   if (barCode.startsWith("0"))
                       {
                           barCode =barCode.trim { it <= ' ' }.replaceFirst("^0+(?!$)".toRegex(), "")
                           val input = barCode
                           //val breakspoint = input.split("-")

                           val breakspoints = input.split("~")
                           val breakspoint=breakspoints[0].split("-")

                           for (part in breakspoint) {
                               println(part)
                           }
                           if(breakspoint[1].length>4){
                               val result: String = breakspoint[1].replace("[A-Za-z]", "")
                               barCode=breakspoint[0]+result
                           }
                           else{
                               barCode=breakspoint[0]+ breakspoint[1]
                           }
                       }
                       else if (barCode.contains("-")) {


                          *//* val builder = StringBuilder(barCode)
                           barCode = builder.deleteCharAt(builder.indexOf("-") + 1)
                               .deleteCharAt(builder.indexOf("-")).toString()
                               .replaceFirst("^0+(?!$)".toRegex(), "")*//*
                           val input = barCode
                           val parts = input.split("-")

                           // Print each part
                           for (part in parts) {
                               println(part)
                               val input = barCode
                               val breakspoint = input.split("-")
                               for (part in breakspoint) {
                                   println(part)
                               }
                               if(breakspoint[1].length>4){
                                   barCode=breakspoint[0]+breakspoint[1].substring(0,breakspoint[1].length-1)
                               }
                               else{
                                   barCode=breakspoint[0]+ breakspoint[1]
                               }          }

                           barCode=parts[1]+"0"+parts[2].substring(1,parts[2].length)
                           Log.d("ashish",barCode);
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
                           return@setOnEditorActionListener true
                           //  clearBarcodeEdittext()

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
                        //   Toast.makeText(this@AVR, "Duplicate Barcode", Toast.LENGTH_SHORT).show()
                       }
                       else if(cnlist.contains(barCode.substring(0,barCode.length-4))){
                           addtolocalbarcode(barCode)
                       }
                       else{
                               lifecycleScope.launch {
                                   if(cureentgr.equals("")){
                                       checkcn(barCode)
                                   }//
                                   else {
                                       val a=  db.barcodeDao().getcurrengr(cureentgr)
                                       if(cnmapbox.get(cureentgr)!!.toInt()>a){
                                           speak("GR is missing")
                                           val dailog=Dialog(this@AVR)
                                           dailog.setContentView(R.layout.error_dialog)
                                           val img_text=dailog.findViewById<TextView>(R.id.img_text)
                                           val msg_text=dailog.findViewById<TextView>(R.id.msg_text)
                                           val ok_btn=dailog.findViewById<TextView>(R.id.ok_btn)
                                           val print_btn=dailog.findViewById<TextView>(R.id.print_btn)
                                           img_text.setText(cureentgr+"\nSome boxes are missing ")
                                           msg_text.setText("Do you still want to continue")
                                           ok_btn.setOnClickListener {
                                               dailog.dismiss()
                                               checkcn(barCode)}
                                           print_btn.setOnClickListener { dailog.dismiss() }
                                           dailog.show()
                                       }
                                       else checkcn(barCode)
                                   }

                           }
                        //   checkcn(barCode)



                       }

                   }
               }
               catch (e: Exception) {
                   Toast.makeText(this@AVR, e.message, Toast.LENGTH_SHORT).show()
               }
               clearBarcodeEdittext()
               return@setOnEditorActionListener true
           } else {
               clearBarcodeEdittext()
               return@setOnEditorActionListener false
           }
       }
       binding.addFloatingBtn.visibility=View.GONE
      binding.addFloatingBtn.setOnClickListener {
          startActivity(Intent(this,AddManual::class.java).putExtra("avr",binding.gateNo.text.toString()))
      }


       binding.filldetails.setOnClickListener {
           if(isgateverify){
               binding.avrdetail.visibility=View.GONE
               binding.barcodedata.visibility=View.VISIBLE
               binding.submitBtn.visibility=View.VISIBLE
               val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
               val clip = ClipData.newPlainText("label","please don't do this ")
                clipboard.setPrimaryClip(clip)
           }
           else
               Utils.showDialog(this,"error","Please Verify Gate Entry Number",R.drawable.ic_error_outline_red_24dp)

       }

       binding.getmiss.setOnClickListener {
           startActivity(Intent(this@AVR,MissingList::class.java))
       }
   }
    fun checkcnBajaj(barcode : String){
        if(Utils.haveInternet(this)){
            //val input = barcode
           // val parts = input.split("~")
            pd.show()
            val cn1=barcode//parts[0]
            val mod= CnValidateMod()
            mod.avr=binding.gateNo.text.toString()
            mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
            mod.cn_no=cn1
            ApiClient.getClient().create(ServiceInterface::class.java).
            cn_validate1_bajaj(Utils.getheaders(),mod).enqueue(object : Callback<CnValidateResp>{
                override fun onResponse(
                    call: Call<CnValidateResp>,
                    response: Response<CnValidateResp>
                ) {
                    pd.dismiss()
                    if(response.isSuccessful){
                        response.body()?.let { body->
                            if (body.error.equals("false", true)) {
                                if(bajajlist.contains(barcode)){}
                                else{
                                    cureentgr= response.body()!!.mcn//here we take only manual number
                                    bajajlist.add(barcode)
                                    OmOperation.savePreferences(Constants.BAJAJ,Gson().toJson(bajajlist).toString())
                                    cnmap.put(cureentgr,response.body()!!.city.toString())
                                    cnmapbox.put(cureentgr,response.body()!!.pkg.toInt())
                                    binding.GRCount.setText(cnlist.size.toString())
                                    lifecycleScope.launch {
                                        var   cnboxlist=  db.barcodeDao()gettotalboxofcn(cureentgr)
                                        var mynextnumber=cureentgr.toString()+"0001"
                                        val allNumbers = mutableSetOf<Long>()

                                        for(i in cnboxlist){
                                            allNumbers.add(i.toLong())
                                        }

                                        try{
                                            if (allNumbers.isEmpty()) mynextnumber =cureentgr.toString()+"0001"
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

                                            if(!cnboxlist.contains(cureentgr.toString()+"0001")){
                                                mynextnumber=cureentgr.toString()+"0001"
                                            }

                                        }
                                        catch (e: Exception){}


                                        addtolocalbajaj(barcode,mynextnumber,body.city.toString())


                                    }
                                    lifecycleScope.launch {
                                        if(!cnlist.contains(cureentgr)) {
                                            cnlist.add(cureentgr)
                                            val cn = CN(
                                                challan = response.body()!!.challan,
                                                box = response.body()!!.pkg,
                                                cn = cureentgr,
                                                city = response.body()!!.city ?: "",
                                                weight = "0",
                                                findBox = barcode
                                            )
                                            db.verifydao().inserbarcode(cn)
                                        }
                                        else{
                                            db.barcodeDao().appendToFindBox(cureentgr,","+barcode)
                                            /*  val cn = CN(
                                                  challan = response.body()!!.challan,
                                                  box = response.body()!!.pkg,
                                                  cn = cureentgr,
                                                  city = response.body()!!.city ?: "",
                                                  weight = "0"
                                              )
                                              db.verifydao().inserbarcode(cn)*/
                                        }
                                    }
                                }

                            }
                            else Utils.showDialog(this@AVR, "Error", body.response, R.drawable.ic_error_outline_red_24dp)

                        }?: run {
                            Utils.showDialog(this@AVR, "Error", "Response body is null", R.drawable.ic_error_outline_red_24dp)
                        }
                    }
                    else
                        Utils.showDialog(
                            this@AVR,
                            "HTTP Error ${response.code()}",
                            response.message(),
                            R.drawable.ic_error_outline_red_24dp
                        )

                }

                override fun onFailure(call: Call<CnValidateResp>, t: Throwable) {
                    pd.dismiss()
                    Utils.showDialog(this@AVR,"onFailure","",R.drawable.ic_error_outline_red_24dp)
                }

            })
        }
    }
    private fun addtolocalbajaj(scanbarcode :String ,barcode: String,city:String) {
        if(displayedData.contains(barcode)){

        }
        else{
            bajajlist_with_realtion[barcode]=scanbarcode//this will add barcode

            //  OmOperation.savePreferences(Constants.bajajlist_with_realtion,Gson().toJson(bajajlist_with_realtion).toString())
            OmOperation.savePreferences(Constants.bajajlist_with_realtion, Gson().toJson(bajajlist_with_realtion))

            writetextinback(city)
            speak(city)
            displayedData.add(0,barcode)
            barcodelist.add(0,barcode)
            //    displayedData= ArrayList(barcodelist.subList(0, minOf(barcodelist.size, 50)))

            binding.barcodeCount.setText(barcodelist.size.toString())
            adapter.notifyDataSetChanged()
            lifecycleScope.launch {
                val barcodem=Barcode(barcode=barcode , timestamp = Utils.getCurrentTimestamp())
                db.barcodeDao().inserbarcode(barcodem)
                getcureentGR(barcode)
            }
            lifecycleScope.launch {
                val barcodem= RestoreBarcode(barcode=barcode )
                db.restorebarcodedao().inserbarcode(barcodem)

            }
            cnmap.get(barcode.substring(0, barcode.length - 4))?.let {
                speak(it.toString())
                writetextinback(it.toString())
            } ?: speak("Unknown barcode")
            //  speak(cnmap.get(barcode.substring(0,barcode.length-4)).toString())
        }



    }

    fun checkcn(barcode : String){
        if(Utils.haveInternet(this)){
            pd.show()
            val cn=barcode.substring(0,barcode.length-4)
            val mod= CnValidateMod()
            mod.avr=binding.gateNo.text.toString()
            mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
            mod.cn_no=cn
            ApiClient.getClient().create(ServiceInterface::class.java).
            cn_validate1(Utils.getheaders(),mod).enqueue(object : Callback<CnValidateResp>{
                override fun onResponse(
                    call: Call<CnValidateResp>,
                    response: Response<CnValidateResp>
                ) {
                    pd.dismiss()
                    if(response.isSuccessful){
                       response.body()?.let { body->
                           if (body.error.equals("false", true)) {
                               cureentgr=barcode.substring(0,barcode.length-4)
                               cnlist.add(barcode.substring(0,barcode.length-4))
                               cnmap.put(barcode.substring(0,barcode.length-4),response.body()!!.city.toString())
                               cnmapbox.put(barcode.substring(0,barcode.length-4),response.body()!!.pkg.toInt())
                               binding.GRCount.setText(cnlist.size.toString())
                               addtolocalbarcode(barcode)
                               lifecycleScope.launch {
                                   val cn=CN(challan = response.body()!!.challan, box =response.body()!!.pkg,cn =cn, city = response.body()!!.city?:"", weight = "0")
                                   db.verifydao().inserbarcode(cn)

                               }
                           }
                           else {
                               Utils.showDialog(this@AVR,"Error" ,body.response, R.drawable.ic_error_outline_red_24dp)
                           }
                       }?: run {
                           Utils.showDialog(this@AVR, "Error", "Response body is null", R.drawable.ic_error_outline_red_24dp)
                       }
                    }
                    else
                        Utils.showDialog(
                            this@AVR,
                            "HTTP Error ${response.code()}",
                            response.message(),
                            R.drawable.ic_error_outline_red_24dp
                        )
                   /* if(response.code()==200){
                        if(response.body()?.error.equals("false",true)){
                            cureentgr=barcode.substring(0,barcode.length-4)
                            cnlist.add(barcode.substring(0,barcode.length-4))
                            cnmap.put(barcode.substring(0,barcode.length-4),response.body()!!.city.toString())
                            cnmapbox.put(barcode.substring(0,barcode.length-4),response.body()!!.pkg.toInt())
                            binding.GRCount.setText(cnlist.size.toString())
                            addtolocalbarcode(barcode)
                            lifecycleScope.launch {
                                val cn=CN(challan = response.body()!!.challan, box =response.body()!!.pkg,cn =cn, city = response.body()!!.city?:"", weight = "0")
                                 db.verifydao().inserbarcode(cn)

                            }
                        }
                        else{
                           *//* cureentgr=barcode.substring(0,barcode.length-4)
                            cnlist.add(barcode.substring(0,barcode.length-4))
                            cnmap.put(barcode.substring(0,barcode.length-4),"")
                            cnmapbox.put(barcode.substring(0,barcode.length-4),55)
                            binding.GRCount.setText(cnlist.size.toString())
                            addtolocalbarcode(barcode)
                            lifecycleScope.launch {
                                val cn=CN(challan ="11111111111", box ="55",cn =cn, city = "", weight = "0")
                                db.verifydao().inserbarcode(cn)

                            }*//*
                            Utils.showDialog(this@AVR,response.code().toString(),response.body()!!.response,R.drawable.ic_error_outline_red_24dp)

                        }
                    }
                    else{
                        Utils.showDialog(this@AVR,response.code().toString(),response.message(),R.drawable.ic_error_outline_red_24dp)

                    }*/
                }

                override fun onFailure(call: Call<CnValidateResp>, t: Throwable) {
                    pd.dismiss()
                    Utils.showDialog(this@AVR,"onFailure","",R.drawable.ic_error_outline_red_24dp)
                }

            })
        }
    }
    private fun addtolocalbarcode(barcode: String) {
        if(displayedData.contains(barcode)){

        }
        else{
            displayedData.add(0,barcode)
            barcodelist.add(0,barcode)
        //    displayedData= ArrayList(barcodelist.subList(0, minOf(barcodelist.size, 50)))

            binding.barcodeCount.setText(barcodelist.size.toString())
            adapter.notifyDataSetChanged()
            lifecycleScope.launch {
                val barcodem=Barcode(barcode=barcode , timestamp = Utils.getCurrentTimestamp())
                db.barcodeDao().inserbarcode(barcodem)
                getcureentGR(barcode)
            }
            lifecycleScope.launch {
                val barcodem= RestoreBarcode(barcode=barcode )
                db.restorebarcodedao().inserbarcode(barcodem)

            }
            cnmap.get(barcode.substring(0, barcode.length - 4))?.let {
                speak(it.toString())
                writetextinback(it.toString())
            } ?: speak("Unknown barcode")
          //  speak(cnmap.get(barcode.substring(0,barcode.length-4)).toString())
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

    private fun speak(city : String){
        textToSpeech.speak(city.lowercase(Locale.getDefault()), TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    fun observeViewModel(){
        viewmode.data.observe(this, Observer {
            when(it){
                is NetworkState.Success<*>->{
                    cp.dismiss()
                    /*var resp: CommonRespS = it.data as CommonRespS
                    binding.date.setText(resp.date)
                    binding.lorryNo.setText(resp.lorry_no)*/
                    isgateverify=true
                    binding.gateNo.isEnabled=false
                    var resp: CommonRespS = it.data as CommonRespS
                   // resp.lorry_no
                    binding.tvTitle.setText("AVR "+OmOperation.getPreferences(Constants.BCODE,"")+"("+resp.lorry_no+")"+"\n"
                            +OmOperation.getPreferences(Constants.EMP_CODE,""))
                }
                is NetworkState.Error->{
                    cp.dismiss()
                    isgateverify=false
                   // binding.lorryNo.setText("OM")
                    Utils.showDialog(this@AVR,it.title,it.message,R.drawable.ic_error_outline_red_24dp)

                }
                is NetworkState.Loading->{
                    isgateverify=false
                    cp.show()
                }

            }

        })

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
    private   fun checkrestoredata() {
      lifecycleScope.launch {
         val mybarcode= db.barcodeDao().getAll()
          if(mybarcode.size>0){
           Utils.showDialog(this@AVR,"Restore Data",
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

                    }

                }

                cnlist.addAll(list)
                binding.GRCount.setText(cnlist.size.toString())
            }
        }



    }
    private fun clearBarcodeEdittext() {
        binding.barcodeText.setText("")
    }


    private fun opend(textView: TextView) {
        val items = arrayOf<CharSequence>("0", "1", "2", "3", "4", "5")
        // }
        val builder = AlertDialog.Builder(this@AVR)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            textView.text = items[item]
            dialog.dismiss()
        }
        builder.show()
    }

    fun DeleteAllbarcode(){
        val alertBox = android.app.AlertDialog.Builder(this@AVR)
        alertBox.setMessage("Do you really want to Delete all Data").setCancelable(false)
            .setPositiveButton(
                "YES"
            ) { dialog, which ->
                displayedData.clear()
                barcodelist.clear()
                cnlist.clear()
                cnmap.clear()
                cnmapbox.clear()
                OmOperation.savePreferences(Constants.BAJAJ,"")
                OmOperation.savePreferences(Constants.bajajlist_with_realtion,"")
                bajajlist.clear()
                binding.GRCount.setText("0")
                binding.crgr.setText("0")
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
          barcodelist.clear()
        OmOperation.savePreferences(Constants.BAJAJ,"")
        displayedData.clear()
        barcodelist.clear()
        cnlist.clear()
        cnmap.clear()
        cnmapbox.clear()
        binding.GRCount.setText("0")
        binding.barcodeCount.setText("0")
        adapter.notifyDataSetChanged()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.barcode_scan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.missing -> {
               startActivity(Intent(this@AVR,MissingList::class.java))
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
                        Toast.makeText(this@AVR, "Duplicate barcodes deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            R.id.bajajmiss ->{
                startActivity(Intent(this@AVR, BajajMiss::class.java))

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun removebarcode(barcode: String) {
        val alertBox = android.app.AlertDialog.Builder(this@AVR)
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
                   db.verifydao().deletecn(barcode.substring(0,barcode.length-4))
                   cnlist.remove(barcode.substring(0,barcode.length-4))
                   binding.GRCount.setText(cnlist.size.toString())
               }
                    binding.barcodeCount.setText(barcodelist.size.toString())
                   adapter.notifyDataSetChanged()
                }


            }.setNegativeButton("NO", null)

        val alert = alertBox.create()
        alert.show()


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

   fun getcureentGR(barcode: String){
       lifecycleScope.launch {
         val a=  db.barcodeDao().getcurrengr(barcode.substring(0,barcode.length-4))
           binding.crgr.setText(a.toString())
       }

   }

    override fun onBackPressed() {
        super.onBackPressed()
        // Create an AlertDialog to show the confirmation message
        AlertDialog.Builder(this).apply {
            setTitle("Exit AVR")
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
    fun closeKeyboard(){
        binding.barcodeText.setOnClickListener {

        }


       /* val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.barcodeText.windowToken, 0)*/
    }

    override fun addgr(position: Int,ischecked : Boolean) {
        if(ischecked) grwithotdoc.add(grdoc.get(position))
        else  grwithotdoc.remove(grdoc.get(position))
        Log.d("ashish",grdoc.get(position).toString().substring(1,grdoc.get(position).toString().length-1))
        Log.d("ashish",grwithotdoc.size.toString())
    }
    private fun writetextinback(message: String) {
        try{
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
}



