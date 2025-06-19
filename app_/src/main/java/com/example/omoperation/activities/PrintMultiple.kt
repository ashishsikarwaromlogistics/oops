package com.example.omoperation.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.window.Dialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ListAdapter
import com.example.omoperation.databinding.ActivityBarcodePrintBinding
import com.example.omoperation.databinding.ActivityPrintMultipleBinding
import com.example.omoperation.databinding.DialogBoxBinding
import com.example.omoperation.model.cn_enquery.Myquery
import com.example.omoperation.model.print.PrintBarcodeMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.collections.ArrayList

class PrintMultiple : AppCompatActivity() {
    lateinit var binding: ActivityPrintMultipleBinding
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var bluetoothDevice: BluetoothDevice? = null
    val cp by lazy {
        CustomProgress(this)
    }
    lateinit var mDeviceList: ArrayList<BluetoothDevice>

    private val spinner_item_location = -1
    var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
    lateinit var BTMAC: String
    val items = ArrayList<String>()
    private lateinit var progressDialog: ProgressDialog
    private var date: String? = null
    private var time: String? = null
    val df1: SimpleDateFormat = SimpleDateFormat("hh:mm a")
    val df2: SimpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
    val c: Calendar = Calendar.getInstance()
    var isvalid=false
    var iszebraprint=false
    var STICKER_COUNT=""
    val boxes=ArrayList<Int>()
    var totalno=0
    var myprintcn=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_print_multiple)
        binding.inputBranchCode.setText(OmOperation.getPreferences(Constants.BCODE,""))
        time = df1.format(c.getTime())
        date = df2.format(c.getTime())

        binding.edtRemarks.setOnClickListener { openDialog() }
        binding.llprint.visibility= View.VISIBLE
        mDeviceList = ArrayList()
        getPairedDevices()

        binding.inputCn.setOnFocusChangeListener(View.OnFocusChangeListener { view: View?, b: Boolean ->
            if (!b) {
                if (binding.inputCn.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Enter CN Number", Toast.LENGTH_SHORT).show()
                } else if (binding.inputBranchCode.text.toString() == null) {
                    Toast.makeText(this, "Select Branch", Toast.LENGTH_SHORT).show()
                } else {
                    getCNDetails(binding.inputCn.getText().toString().trim { it <= ' ' })
                }
            }
        })
        binding.inputCn.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputCn.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Enter CN Number", Toast.LENGTH_SHORT).show()
                } else if (binding.inputBranchCode.text.toString() == null) {
                    Toast.makeText(this, "Select Branch", Toast.LENGTH_SHORT).show()
                } else {
                    getCNDetails(binding.inputCn.getText().toString().trim { it <= ' ' })
                }
                true
            } else {
                false
            }
        }
        binding.imgSearch.setOnClickListener {
            if (binding.inputCn.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter CN Number", Toast.LENGTH_SHORT).show()
            } else if (binding.inputBranchCode.text.toString() == null) {
                Toast.makeText(this, "Select Branch", Toast.LENGTH_SHORT).show()
            } else {
                getCNDetails(binding.inputCn.getText().toString().trim { it <= ' ' })
            }
        }

        binding.btnPrint.setOnClickListener {
            if(!isvalid){
                Utils.showDialog(this@PrintMultiple,"error","CN not validate",R.drawable.ic_error_outline_red_24dp)
                return@setOnClickListener
            }
            else if(boxes.size==0){
                Utils.showDialog(this@PrintMultiple,"error","Box is not added",R.drawable.ic_error_outline_red_24dp)
                return@setOnClickListener
            }
            else if(binding.edtRemarks.text.toString().isEmpty()|| binding.edtRemarks.text.toString().isBlank()){
                Utils.showDialog(this@PrintMultiple,"error","Please Add remarks first.",R.drawable.ic_error_outline_red_24dp)
                return@setOnClickListener
            }
            else if(isConnected()){
                lifecycleScope.launch {
                    var currentBox=0;
                    //var cn=binding.inputCn.text.toString();
                    var cn=myprintcn
                    for(i in 0 until boxes.size){
                        currentBox=boxes.get(i).toInt()
                        var bar: String = if (currentBox < 10) {
                            cn + "000" + currentBox
                        } else if (currentBox >= 10 && currentBox < 100) {
                            cn + "00" + currentBox
                        } else if (currentBox >= 100 && currentBox < 1000) {
                            cn + "0" + currentBox
                        } else {
                            cn + currentBox
                        }
                        bar = "O" + Utils.transformNumber(bar.toLong())

                        printdata(binding.inputNoOfBox.text.toString(),bar,myprintcn,binding.inputFrom.text.toString(),binding.inputTo.text.toString(),currentBox.toString(),binding.manualNo.text.toString())

                    }
                    savesticker()


                }
            }
            else {
                Utils.showDialog(this@PrintMultiple,"error","Please Connect Printer first",R.drawable.ic_error_outline_red_24dp)
                return@setOnClickListener
            }
        }


        binding.rbZebra.setOnClickListener { iszebraprint=true }
        binding.rbOther.setOnClickListener { iszebraprint=false }
        binding.boxadd.setOnClickListener { if(isvalid)addbox() else Utils.showDialog(this,"error","please validate cn first ",R.drawable.ic_error_outline_red_24dp) }
    }

    private fun getCNDetails(cn: String) {
        if(Utils.haveInternet(this)){
            cp.show()
            val URL = ServiceInterface.omapi+"cn_enquiry.php?status=omstaff&cn_no=" + cn

            ApiClient.getClient().create(ServiceInterface::class.java).cnenquery(URL, Utils.getheaders()).enqueue(object :
                retrofit2.Callback<Myquery> {
                override fun onResponse(
                    call: Call<Myquery>,
                    response: Response<Myquery>
                ) {
                    cp.dismiss()
                    totalno=0
                    if(response.code()==200){
                        if(response.body()?.error.equals("false")){
                            myprintcn=response.body()?.cn_enquiry?.get(0)?.CNNO.toString()
                            isvalid=true
                            STICKER_COUNT=response.body()?.cn_enquiry?.get(0)?.STICKER_COUNT.toString()
                            binding.inputFrom.setText(response.body()?.cn_enquiry?.get(0)?.BFROM.toString())
                            binding.inputTo.setText(response.body()?.cn_enquiry?.get(0)?.BTO.toString())
                            binding.manualNo.setText(response.body()?.cn_enquiry?.get(0)?.CN_REMARKS.toString()?:"-")
                            binding.inputNoOfBox.setText(response.body()?.cn_enquiry?.get(0)?.NO_OF_PKG.toString())
                            totalno=Utils.check_null_Int(binding.inputNoOfBox.text.toString())
                            binding.inputCn.isEnabled=false
                            //binding.inputFrom.setText(response.body()?.cn_enquiry?.get(0)?.BFROM.toString())
                        }
                        else {
                            isvalid=false
                            Utils.showDialog(this@PrintMultiple,"error","Invalid CN ",R.drawable.ic_error_outline_red_24dp)
                        }
                    }

                }

                override fun onFailure(call: Call<Myquery>, t: Throwable) {
                    cp.dismiss()
                    totalno=0
                    isvalid=false
                    Utils.showDialog(this@PrintMultiple,"onFailure",t.message,R.drawable.ic_error_outline_red_24dp)

                }

            })

        }
    }


    fun printdata( box: String,bar : String,cn:String,from:String,to:String,currentBox:String,manualNO:String){
        /*  val ccmm = arrayOf<String>(
              "SIZE 71.2 mm, 50.8 mm\r\n",
              "GAP 3 mm, 0 mm\r\n",
              "DIRECTION 0,0\r\n",
              "REFERENCE 0,0\r\n",
              "OFFSET 0 mm\r\n",
              "SET PEEL OFF\r\n",
              "SET CUTTER OFF\r\n",
              "SET PARTIAL_CUTTER OFF\r\n",
              "SET TEAR ON\r\n",
              "CLS\r\n",
              "BARCODE 225,295,\"128\",50,0,0,2,2,\"" + bar
                      + "\"\r\n",
              ("QRCODE 330,40, H, 4, M, 0, M2,\"B0046"
                      + bar +
                      "\"\r\n"),
              "CODEPAGE 1252\r\n",
              ("TEXT 10,140,\"ROMAN.TTF\",0,2,12,\"" + cn
                      + "\"\r\n"),
              ("TEXT 10,180,\"ROMAN.TTF\",0,2,9,\"" + from
                      + "\"\r\n"),
              (" TEXT 10,220,\"ROMAN.TTF\",0,2,11,\""
                      + to + "\"\r\n"),
              ("TEXT 10,260,\"ROMAN.TTF\",0,2,11,\"PKT   : "
                      + currentBox + " of " + box + "\"\r\n"),
              ("TEXT 200,260,\"ROMAN.TTF\",0,2,9,\"R/NO : "
                      + (if (manualNO.length > 20) manualNO.substring(
                  0,
                  19
              ) else manualNO) + "\"\r\n"),
              ("TEXT 10,340,\"ROMAN.TTF\",0,2,9,\"TIME : " + time
                      + "\"\r\n"),
              ("TEXT 10,300,\"ROMAN.TTF\",0,2,9,\"DATE : " + date
                      + "\"\r\n"), "PRINT 1,1\r\n"
          )*/
        val ccmm = arrayOf<String>(
            "SIZE 71.2 mm, 50.8 mm\r\n",
            "GAP 3 mm, 0 mm\r\n",
            "DIRECTION 0,0\r\n",
            "REFERENCE 0,0\r\n",
            "OFFSET 0 mm\r\n",
            "SET PEEL OFF\r\n",
            "SET CUTTER OFF\r\n",
            "SET PARTIAL_CUTTER OFF\r\n",
            "SET TEAR ON\r\n",
            "CLS\r\n",
            "BARCODE 225,295,\"128\",50,0,0,2,2,\"" + bar
                    + "\"\r\n",
            "QRCODE 330,40, H, 4, M, 0, M2,\"B0046"
                    + bar +
                    "\"\r\n",
            "CODEPAGE 1252\r\n",

        //    "BOX 8,135,300,170,3\r\n",//x y w , h from y axis,radius
            /*("TEXT 10,140,\"ROMAN.TTF\",0,2,12,\"" + cn
                    + "\"\r\n"),*/
            ("TEXT 10,140,\"ROMAN.TTF\",0,2,11,\"CN:"
                    + cn  + "\"\r\n"),
            "TEXT 540,20,\"ROMAN.TTF\",0,2,12,\"" + STICKER_COUNT
                    + "\"\r\n",
            // "BOX 8,175,300,205,3\r\n",
            ("TEXT 10,180,\"ROMAN.TTF\",0,2,9,\"from" + from
                    + "\"\r\n"),
            // "BOX 8,215,400,250,3\r\n",
            (" TEXT 10,220,\"ROMAN.TTF\",0,2,11,\"to"
                    + to + "\"\r\n"),
            //  "BOX 8,255,400,290,3\r\n",
            ("TEXT 10,260,\"ROMAN.TTF\",0,2,11,\"PKT   : "
                    + currentBox + " of " + box + "\"\r\n"),
            ("TEXT 200,260,\"ROMAN.TTF\",0,2,9,\"R/NO : "
                    + (if (manualNO.length > 20) manualNO.substring(
                0,
                19
            ) else manualNO) + "\"\r\n"),
            // "BOX 8,335,200,365,3\r\n",
            ("TEXT 10,340,\"ROMAN.TTF\",0,2,9,\"TIME : " + time
                    + "\"\r\n"),
            // "BOX 8,295,200,325,3\r\n",
            ("TEXT 10,300,\"ROMAN.TTF\",0,2,9,\"DATE : " + date
                    + "\"\r\n"), "PRINT 1,1\r\n"
        )
        //
        val zebraprint = arrayOf(
            "! U1 setvar \"media.sense_mode\" \"gap\"\r\n",
            "! U1 setvar \"media.tof\" \"0\"\r\n",
            "! 15 203 203 440 1\r\n",
            "PW 639\r\n",
            "PREFEED 0\r\n",
            "POSTFEED 0\r\n",
            "WAIT 0\r\n",
            "PRESENT-AT 32 8\r\n",
            "PRE-TENSION 0\r\n",
            "POST-TENSION 0\r\n",
            "SPEED 5\r\n",
            "TONE 100\r\n",
            "NO-PACE\r\n",
            "PACE\r\n",
            "ON-FEED FEED\r\n",
            "BT OFF\r\n",
            "B QR 333 15 15 M 2 U 4\r\n",
            "MA,$bar\r\n",  //"MA,QR code "+bar+"\r\n" ,
            // "B QR 333 5 15 M 2 U 6\r\n",
            //  "MA,"+bar+"\r\n",  //"MA,QR code "+bar+"\r\n" ,

            "ENDQR\r\n",
            "T 5 0 10 155 CN " + cn + ", R/NO :" + (if (manualNO.length > 20) manualNO.substring(
                0,
                19
            ) else manualNO) + "\r\n",
            "T 5 0 10 260 Date " + date + "Time " + time + " ,PKT " + currentBox + "of " + box + "\r\n",  // "T 5 0 230 257($number * ${arg.box})\r\n",
            // "T 5 0 230 257($pkg_serial)\r\n",
            "BT 7 0 3\r\n",
            "B 128 2 30 42 10 290 $bar\r\n",  //   "T 5 0 10 170 From ${arg.from} \r\n",  //


            "T 5 0 10 180 From $from\r\n",
            "T 5 0 10 220 To $to\r\n",
            "PRINT\r\n",


            )

        if(iszebraprint){
            for (command in zebraprint) {
                outputStream!!.write(command.toByteArray())
                outputStream!!.flush()
            }
        }
        else{
            try{
                for (command in ccmm) {
                    outputStream!!.write(command.toByteArray())
                    outputStream!!.flush()
                    //  publishProgress(currentBox)
                }
            }
            catch (e:Exception){
                Utils.showDialog(this,"${e.toString()}","Unexpected error(Printer not connected properly)",R.drawable.ic_error_outline_red_24dp)
            }

        }



    }

    fun savesticker(){
        // https://api.omlogisti    cs.co.in/barcode_print.php
        var myboxes=""
        lifecycleScope.launch {
            for(i in 0 until boxes.size){
             if(i==boxes.size-1)
                 myboxes=myboxes+boxes.get(i).toString()
                 else
             myboxes=myboxes+boxes.get(i).toString()+"-"
            }
            val mod=
                PrintBarcodeMod(
                    OmOperation.getPreferences(Constants.BCODE,""),
                    binding.inputNoOfBox.text.toString(),
                    binding.inputCn.text.toString(),
                    OmOperation.getPreferences(Constants.EMP_CODE,""),
                    binding.inputFrom.text.toString(),
                    BTMAC,
                    binding.inputTo.text.toString(),
                    binding.edtRemarks.text.toString()+myboxes
                )

            val response= ApiClient.getClient().create(ServiceInterface::class.java).barcode_print(Utils.getheaders(),mod)
            if(response.code()==200){
                if(response.body()?.error.equals("false")){
                    Toast.makeText(this@PrintMultiple,"Printed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }

    private fun getPairedDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
        } else {
            if (!bluetoothAdapter!!.isEnabled) {
                // Optionally, you can prompt the user to enable Bluetooth here
            }

            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter!!.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                items.add(deviceName)
                val deviceAddress = device.address // MAC address
                println("Paired Device: $deviceName - $deviceAddress")
                // You can add this device information to a list to display in the UI
            }
            mDeviceList.addAll(pairedDevices!!)
        }
        binding.spinnerText.setOnClickListener {
            if (items.isNotEmpty()) {
                showDialogWithList(items)
            } else {
                Utils.showDialog(this, "Error", "No device is paired", R.drawable.ic_error_outline_red_24dp)
            }
        }
    }

    private fun showDialogWithList(items: List<String>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_list, null)
        val listView = dialogView.findViewById<ListView>(R.id.listView)
        val adapter = ListAdapter(this, items)
        listView.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select an Item")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        listView.setOnItemClickListener { _, _, position, _ ->
            // Handle item click
            val selectedItem = items[position]
            binding.spinnerText.setText(selectedItem)
            showProgressDialog()
            GlobalScope.launch {
                connectDevice(position)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private suspend fun connectDevice(position: Int) = withContext(Dispatchers.IO) {
        if (mDeviceList.isEmpty()) {
            // dismissProgressDialog()
            return@withContext
        }
        bluetoothDevice = mDeviceList[position]

        if (bluetoothDevice?.bondState == BluetoothDevice.BOND_NONE) {
            try {
                createBond(bluetoothDevice!!)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PrintMultiple, "Failed to pair device", Toast.LENGTH_SHORT).show()
                }
                return@withContext
            }
        }

        withContext(Dispatchers.Main) {
            if (!isConnected()) {
                openBluetoothPrinter()
                binding.btnConnect.text = "Connected"
                dismissProgressDialog()
            } else {
                dismissProgressDialog()
                disconnect()
                binding.btnConnect.text = "Disconnected"
            }
        }
    }

    private fun isConnected(): Boolean {
        return bluetoothSocket != null
    }

    private fun disconnect() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
        } catch (e: IOException) {
            println(e.message)
        }
    }

    @Throws(Exception::class)
    private fun createBond(device: BluetoothDevice) {
        try {
            val cl = Class.forName("android.bluetooth.BluetoothDevice")
            val method = cl.getMethod("createBond")
            method.invoke(device)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun openBluetoothPrinter() {
        try {
            BTMAC = bluetoothDevice?.address.toString()
            bluetoothDevice = bluetoothAdapter?.getRemoteDevice(BTMAC)
            val uuidString = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
            bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(uuidString)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            inputStream = bluetoothSocket?.inputStream
        } catch (ex: Exception) {
            BTMAC=""
            ex.printStackTrace()
        }
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this).apply {
            setTitle("Progress")
            setMessage("Connecting...")
            setCancelable(false)
            show()
        }
    }
  /*  fun savesticker(){
        // https:// api. omlogistics. co. in/ barcode_print. php

        lifecycleScope.launch {
            val mod=
                PrintBarcodeMod(
                    OmOperation.getPreferences(Constants.BCODE,""),
                    binding.inputNoOfBox.text.toString(),
                    binding.inputCn.text.toString(),
                    OmOperation.getPreferences(Constants.EMP_CODE,""),
                    binding.inputFrom.text.toString(),
                    BTMAC,
                    binding.inputTo.text.toString(),""

                )

            val response= ApiClient.getClient().create(ServiceInterface::class.java).barcode_print(
                Utils.getheaders(),mod)
            if(response.code()==200){
                if(response.body()?.error.equals("false")){
                    Toast.makeText(this@PrintMultiple,"Printed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/


    private fun dismissProgressDialog() {
        progressDialog.dismiss()
    }


    fun openDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        val options = arrayOf(
            "Due To Sticker Not Scan",
            "Due To Sticker Not Available",
            "Due To Sticker Torn"
        )
        builder.setItems(options) { dialog, which ->
            val  selectedOption = options[which]
            binding.edtRemarks.setText(selectedOption)
            dialog.dismiss()
        }
        builder.show()
    }

    fun addbox(){
        val d= Dialog(this@PrintMultiple)
        val binding1 = DialogBoxBinding.inflate(layoutInflater) // Replace `DialogBoxBinding` with your actual binding class name
        d.setContentView(binding1.root)
        d.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(), // 90% of the screen width
            (resources.displayMetrics.heightPixels * 0.5).toInt()// Wrap content for height
        )
        //d.setContentView(R.layout.dialog_box)
        binding1.tvbox.setText("Total Box are ${totalno}.")
        binding1.tvclose.setOnClickListener { d.dismiss() }
        binding1.done.setOnClickListener { d.dismiss() }
        binding1.remove.setOnClickListener {
            boxes.remove(binding1.edtbox.text.toString().toInt())
            binding1.edtbox.setText("")
            binding.tvboxes.setText(boxes.toString())
            binding1.list.setText(""+boxes.toString())
        }
        binding1.list.setText(""+boxes.toString())
        binding1.submit.setOnClickListener {
            if(totalno<Utils.check_null_Int(binding1.edtbox.text.toString())){
                Utils.showDialog(this,"error","You cann't enter value more than ${totalno}",R.drawable.ic_error_outline_red_24dp)
            }
            else if(binding1.edtbox.text.length==0) Utils.showDialog(this,"error","please enter box numner ",R.drawable.ic_error_outline_red_24dp)
            else if(binding1.edtbox.text.toString().toInt()<1){
                Utils.showDialog(this,"error","box number is not valid",R.drawable.ic_error_outline_red_24dp)

            }
            else if(boxes.contains(binding1.edtbox.text.toString().toInt())){
                Utils.showDialog(this,"error","box already added",R.drawable.ic_error_outline_red_24dp)

            }
            else {
               // Utils.showDialog(this,"Success","Added",R.drawable.ic_success)
                boxes.add(binding1.edtbox.text.toString().toInt())
                binding1.edtbox.setText("")
                binding.tvboxes.setText(boxes.toString())
                binding1.list.setText(""+boxes.toString())
               // d.dismiss()
            }

        }
        d.show()
    }

}
//123465
