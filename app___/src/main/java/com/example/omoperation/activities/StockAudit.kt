package com.example.omoperation.activities

import android.app.Activity
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
import com.example.omoperation.databinding.ActivityStockAuditBinding
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.model.audit.AuditMod
import com.example.omoperation.model.avr.Barcodelist
import com.example.omoperation.model.barcode_load.Cn
import com.example.omoperation.model.cnvaridate.CnValidateMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.Barcode
import com.example.omoperation.room.tables.CN
import com.example.omoperation.room.tables.RestoreBarcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.LinkedList
import java.util.Locale

class StockAudit : AppCompatActivity() , AVRAdapter.RemoveBarcode, TextToSpeech.OnInitListener{
    lateinit var binding : ActivityStockAuditBinding
    lateinit var  db : AppDatabase
    var imei=""
    lateinit var barcodelist: ArrayList<String>
    lateinit var cnlist: LinkedList<String>
    lateinit var displayedData: ArrayList<String>
    lateinit var cp: CustomProgress
    lateinit var adapter: AVRAdapter
    lateinit var doc_remarks:String
    lateinit var doc:String
    lateinit var cnmap: HashMap<String,String>
    lateinit var cnmapbox: HashMap<String,Int>
    private lateinit var textToSpeech: TextToSpeech//
    var totalweight: Double=0.0
    var isscroll=false
    var cureentgr=""
    var sealnumber=""//900 //  //  1090
    var lorryno=""//
   // lateinit var  bundle : Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.  setContentView(this,R.layout.activity_stock_audit)
        init()

    }
    private fun init(){
        binding.tvtitle.setText(" Online Audit ")
        val toolbar: Toolbar = findViewById(R.id.toolbar)
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
        binding.recyAvr.layoutManager= LinearLayoutManager(this)
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
        try{ imei= Utils.getDeviceIMEI(this)}
        catch (e:Exception){ imei="1234567"}
        imei= Utils.getDeviceIMEI(this)
        db=AppDatabase.getDatabase(this)
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
       // bundle = intent.extras!!
        lorryno= "OM"
        binding.submitBtn.setOnClickListener {
            val alertBox = android.app.AlertDialog.Builder(this)
            alertBox.setMessage("Select Status ?").setCancelable(false)
                .setPositiveButton(
                    "Pending"
                ) { dialog, which ->
                    submitData("1")

                    // val activityManager = con.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                    //  activityManager.restartPackage(con.packageName)

                }.setNegativeButton("Complete"){
                        dialog, which ->
                    submitData("2")
                }

            val alert = alertBox.create()
            alert.show()

        }

        binding.barcodeText.setOnEditorActionListener { v, actionId, event ->
            //  if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 0) {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                try {
                    //viewmode.checkdata(binding.barcodeText.getText().toString().trim())
                    var barCode = binding.barcodeText.getText().toString().trim()
                    binding.barcodeText.setText("")
                    if (barCode.startsWith("O")) {
                        barCode = barCode.substring(1, barCode.length)
                        barCode = Utils.revertTransform(barCode)
                    }
                    if (barCode.contains(getString(R.string.NBC_Sticker_Identification))) {
                        val CustomerBarcode =
                            barCode.split(getString(R.string.NBC_Sticker_Identification).toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        barCode =
                            getString(R.string.NBC_Prefix) + CustomerBarcode[1] + CustomerBarcode[4]
                    } else {
                        if (barCode.contains("-")) {
                            val builder = StringBuilder(barCode)
                            barCode = builder.deleteCharAt(builder.indexOf("-") + 1)
                                .deleteCharAt(builder.indexOf("-")).toString()
                                .replaceFirst("^0+(?!$)".toRegex(), "")
                        } else if (barCode.startsWith("0")) {
                            barCode =
                                barCode.trim { it <= ' ' }.replaceFirst("^0+(?!$)".toRegex(), "")
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
                                        val dailog= Dialog(this@StockAudit)
                                        dailog.setContentView(R.layout.error_dialog)
                                        val img_text=dailog.findViewById<TextView>(R.id.img_text)
                                        val msg_text=dailog.findViewById<TextView>(R.id.msg_text)
                                        val ok_btn=dailog.findViewById<TextView>(R.id.ok_btn)
                                        val print_btn=dailog.findViewById<TextView>(R.id.print_btn)
                                        img_text.setText(cureentgr+"\nSome boxes are missing ")
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
                    Toast.makeText(this@StockAudit, e.message, Toast.LENGTH_SHORT).show()
                }
                clearBarcodeEdittext()
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
            startActivity(Intent(this@StockAudit,MissingList::class.java))
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
    private fun clearBarcodeEdittext() {
        binding.barcodeText.setText("")
    }
    fun checkcn(barcode : String){
        if(Utils.haveInternet(this)){
            cp.show()
            val cn=barcode.substring(0,barcode.length-4)
            val mod= CnValidateMod()
            mod.status= "sa"
            ApiClient.getClient().create(ServiceInterface::class.java).
            cn_validate(Utils.getheaders(),mod).enqueue(object : Callback<CommonRespS> {
                override fun onResponse(
                    call: Call<CommonRespS>,
                    response: Response<CommonRespS>
                ) {
                    cp.dismiss()
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

                            Utils.showDialog(this@StockAudit,response.code().toString(),response.body()!!.response,R.drawable.ic_error_outline_red_24dp)

                        }
                    }
                    else{
                        Utils.showDialog(this@StockAudit,response.code().toString(),response.message(),R.drawable.ic_error_outline_red_24dp)

                    }
                }

                override fun onFailure(call: Call<CommonRespS>, t: Throwable) {
                    cp.dismiss()
                    Utils.showDialog(this@StockAudit,"onFailure","",R.drawable.ic_error_outline_red_24dp)
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
            binding.barcodeCount.setText(barcodelist.size.toString())
            adapter.notifyDataSetChanged()
            lifecycleScope.launch {
                val barcodem= Barcode(barcode=barcode )
                db.barcodeDao().inserbarcode(barcodem)
                getcureentGR(barcode)
            }
            lifecycleScope.launch {
                val barcodem= RestoreBarcode(barcode=barcode )
                db.restorebarcodedao().inserbarcode(barcodem)

            }
            speak(cnmap.get(barcode.substring(0,barcode.length-4)).toString())
        }
    }
    private fun speak(city : String){
        textToSpeech.speak(city.toLowerCase(), TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.barcode_scan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.missing -> {
                startActivity(Intent(this@StockAudit,MissingList::class.java))
                true
            }
            R.id.clear ->{
                DeleteAllbarcode()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    fun showmissingDialogerror(){
        val warningAlert = android.app.AlertDialog.Builder(this@StockAudit).create()
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
                //submitData("y",remarks + "   " + excess_matereial.getText().toString()
               // )
            }
        }
        val no_btn = view2.findViewById<Button>(R.id.no_btn)
        val btn_copy2 = view2.findViewById<Button>(R.id.btn_copy)
        btn_copy2.visibility = View.GONE
        no_btn.setOnClickListener { v: View? -> warningAlert.dismiss() }
        warningAlert.show()
    }
    private suspend fun checkrestoredata() {
        lifecycleScope.launch {
            val mybarcode= db.barcodeDao().getAll()
            if(mybarcode.size>0){
                Utils.showDialog(this@StockAudit,"Restore Data",
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
            val mycn= db.verifydao().getAll()
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
        }


    }
    fun loadMoreData(a : Int) {

        adapter.notifyDataSetChanged()
    }
    fun DeleteAllbarcode(){
        val alertBox = android.app.AlertDialog.Builder(this@StockAudit)
        alertBox.setMessage("Do you really want to Delete all Data").setCancelable(false)
            .setPositiveButton(
                "YES"
            ) { dialog, which ->
                displayedData.clear()
                barcodelist.clear()
                cnlist.clear()
                cnmap.clear()
                cnmapbox.clear()
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
        val alertBox = android.app.AlertDialog.Builder(this@StockAudit)
        alertBox.setMessage("Do you really want to remove this $barcode?").setCancelable(false)
            .setPositiveButton(
                "YES"
            ) { dialog, which -> barcodelist.remove(barcode)
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
    fun submitData(status :String){
       startActivity(Intent(this,StockDocScan::class.java))
       /* //status=1 for pending .2 fro complete
        cp.show()
        lifecycleScope.launch {
            val barcodelist = db.barcodeDao().getAll().map { barcodeEntity ->
                Barcodelist().apply {
                    barcode = barcodeEntity.barcode
                    time = barcodeEntity.timestamp
                }
            }

            val cnlist = db.barcodeDao().getcnboxes().map { cnentity ->
                Cn().apply {
                    barcode = cnentity.box
                    cnNo = cnentity.cn

                }
            }
            val mod1=AuditMod()
            mod1.audit_status=status //1 for pending 2 for complete
            mod1.auditNo="" //1 for pending 2 for complete
            mod1.barcodelist=ArrayList<String>()
            mod1.docList=ArrayList<String>()
            mod1.deliveredCNList=ArrayList<String>()
            mod1.deliveredDocList=ArrayList<String>()
            mod1.cnlist=cnlist
            mod1.emp=OmOperation.getPreferences(Constants.EMP_CODE,"")
            mod1.image_list=ArrayList<String>()
           val resp = withTimeoutOrNull(15000L) { // Set timeout to 15 seconds
                try {
                    ApiClient.getClient().create(ServiceInterface::class.java)
                        .online_audit_scanning( Utils.getheaders(), mod1)
                } catch (e: Exception) {
                    Utils.showDialog(this@StockAudit,"error{${e.toString()}}","Net Connection is not working properly",R.drawable.ic_error_outline_red_24dp)
                    null // Return null on exception, e.g., network failure
                }
            }



            //val resp =
            if (resp?.code() == 200) {
                cp.dismiss()
                if(resp.body()!!.error.toString().equals("false")){
                    Deletefrontend();
                    AlertDialog.Builder(this@StockAudit)
                        .setTitle("Success")
                        .setIcon(R.drawable.ic_success)
                        .setMessage( resp.body()!!.response)
                        .setCancelable(false)
                        .setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { dialog: DialogInterface, id: Int ->
                                lifecycleScope.launch {
                                    db.barcodeDao().deleteAllBarcodes()
                                    db.manualDao().DeleteAllManual()
                                    //db.verifydao().deleteCN()
                                    val mybarcode=ArrayList<String>()
                                    val mycnlist=ArrayList<String>()
                                    for(i in barcodelist){
                                        mybarcode.add(i.barcode.toString())
                                        mycnlist.add(i.barcode.toString().substring(0,i.barcode.toString().length-4))
                                    }
                                    db.restorebarcodedao().delete_from_restore(mybarcode)
                                    db.verifydao().delete_from_verify(mycnlist)
                                    //db.().deleteAllBarcodes()
                                    dialog.dismiss()
                                    this@StockAudit.finish()
                                }
                            })
                        .show()


                }
                else if(resp.body()!!.error.toString().equals("true")){
                    Utils.showDialog(
                        this@StockAudit,
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
                    this@StockAudit,
                    "Fail {${resp!!.code()}}",
                    resp!!.message(),
                    R.drawable.ic_error_outline_red_24dp
                )
            }


        }*/

    }
}