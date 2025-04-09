package com.example.omoperation.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.omoperation.CustomProgress
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.model.print.CnEnquiry
import com.example.omoperation.model.print.PrintCNMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.omlogistics.deepak.omlogistics.model.print.PrintResp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.util.*


class PrintCN : AppCompatActivity() {

    lateinit var edt_cn: EditText
    lateinit var searchBtn: ImageButton
    lateinit var spinner_text: TextView
    lateinit var btn_connect: AppCompatButton
    lateinit var btn_print: AppCompatButton
    lateinit var card_spinner: CardView

    lateinit var bluetoothAdapter: BluetoothAdapter
    var bluetoothSocket: BluetoothSocket? = null
    var bluetoothDevice: BluetoothDevice? = null
    lateinit var mDeviceList: ArrayList<BluetoothDevice>
    lateinit var progressDialog: ProgressDialog

    /*lateinit var spinnerDialog: SpinnerDialog*/
    var spinner_item_location = -1
    var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
    var BTMAC: String? = null
    var apiInterface: ServiceInterface? = null

    val cp: CustomProgress by lazy { CustomProgress(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_cn)
        if (ContextCompat.checkSelfPermission(
                this@PrintCN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            != PackageManager.PERMISSION_GRANTED
            &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) {
            ActivityCompat.requestPermissions(
                this@PrintCN, arrayOf<String>(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return
        }
        init()
        edt_cn.setText(intent.getStringExtra("cn_no"))
        apiInterface = ApiClient.getClientsanchar().create(ServiceInterface::class.java)
        searchBtn.setOnClickListener {
            if (!edt_cn.text.toString().equals("")) {
                if (Utils.haveInternet(this)) {
                    cp.show()
                    //https://omsanchar.omlogistics.co.in/oracle/android_api/ewaybill/ewbSingle.php
                     val mod = PrintCNMod()
                    mod.cn_no = edt_cn.getText().toString()
                    mod.status = "cnPrint"
                     val call = apiInterface!!.printvalue(Utils.getheaders(), mod)
                    call!!.enqueue(object : Callback<PrintResp> {
                        override fun onResponse(
                            call: Call<PrintResp>,
                            response: Response<PrintResp>
                        ) {
                            cp.dismiss()
                            if (response.body()!!.error.equals("false", ignoreCase = true)) {
                                ShowDailog(response.body()!!.cn_enquiry)

                            } else {
                                Utils.showDialog(
                                    this@PrintCN,
                                    "Fail",
                                    response.body()!!.error,
                                    R.drawable.ic_error_outline_red_24dp
                                )
                            }
                        }

                        override fun onFailure(call: Call<PrintResp>, t: Throwable) {
                            Toast.makeText(this@PrintCN, t.message, Toast.LENGTH_SHORT).show()
                            cp.dismiss()
                        }
                    })
                }
            }
        }
        btn_print.setOnClickListener {
            if (!edt_cn.text.toString().equals("")) {
                if (Utils.haveInternet(this)) {
                    cp.show()
                    //https://omsanchar.omlogistics.co.in/oracle/android_api/ewaybill/ewbSingle.php
                     val mod = PrintCNMod()
                    mod.cn_no = edt_cn.getText().toString()
                    mod.status = "cnPrint"
                    apiInterface!!.printvalue(Utils.getheaders(), mod)!!.enqueue(object : Callback<PrintResp>{
                        override fun onResponse(
                            call: Call<PrintResp>,
                            response: Response<PrintResp>
                        ) {
                            cp.dismiss()
                            if (response.body()!!.error.equals("false", ignoreCase = true)) {

                                ShowDailog(response.body()!!.cn_enquiry)

                            } else {
                                Utils.showDialog(
                                    this@PrintCN,
                                    "Fail",
                                    response.body()!!.error,
                                    R.drawable.ic_error_outline_red_24dp
                                )
                            }
                        }

                        override fun onFailure(call: Call<PrintResp>, t: Throwable) {
                            Toast.makeText(this@PrintCN, t.message, Toast.LENGTH_SHORT).show()
                            cp.dismiss()
                        }

                    })

                }
            }
        }
    }

    private fun init() {

        progressDialog = ProgressDialog(this)
        edt_cn = findViewById(R.id.edt_cn)
        searchBtn = findViewById(R.id.searchBtn)
        spinner_text = findViewById(R.id.spinner_text)
        btn_connect = findViewById(R.id.btn_connect)
        btn_print = findViewById(R.id.btn_print)
        card_spinner = findViewById(R.id.card_spinner)

        mDeviceList = java.util.ArrayList()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (Utils.haveInternet(this)) {
            if (bluetoothAdapter == null) {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Bluetooth Notification")
                alertDialog.setCancelable(false)
                alertDialog.setMessage("Your Device doesn't have Bluetooth")
                alertDialog.setPositiveButton(
                    "Ok"
                ) { dialog: DialogInterface?, which: Int -> finish() }
                alertDialog.show()
            } else if (!bluetoothAdapter.isEnabled()) {
                val dialog = AlertDialog.Builder(this@PrintCN).create()
                dialog.setCancelable(false)
                val layoutInflater =
                    applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                @SuppressLint("InflateParams") val view =
                    layoutInflater.inflate(R.layout.bluetooth_dialog, null)
                dialog.setView(view)
                val okBtn = view.findViewById<Button>(R.id.okBtn)
                val cancel = view.findViewById<Button>(R.id.cancel_btn)
                okBtn.setOnClickListener { view1: View? ->
                    dialog.dismiss()
                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                    startActivity(intent)
                    finish()
                }
                cancel.setOnClickListener { view12: View? ->
                    dialog.dismiss()
                    finish()
                }
                dialog.show()
            } else {
                /*
                  Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH
                * */
                val pairedDevices = bluetoothAdapter
                    .getBondedDevices()
                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices)
                } else {
                    Toast.makeText(this, "Paired Devices Not Found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val dialog = AlertDialog.Builder(this@PrintCN).create()
            val layoutInflater =
                applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            @SuppressLint("InflateParams") val view =
                layoutInflater.inflate(R.layout.no_internet_dialog, null)
            dialog.setView(view)
            val okBtn = view.findViewById<Button>(R.id.okBtn)
            okBtn.setOnClickListener { view13: View? ->
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }

        spinner_text.setOnClickListener {
            // }
            val list = ArrayList<String>()
            list.add("Select Printer")
            for (i in mDeviceList) {
                list.add(i.name.toString())
            }
            val builder = AlertDialog.Builder(this@PrintCN)
            builder.setItems(
                list.toTypedArray(),
                DialogInterface.OnClickListener { dialog: DialogInterface, item: Int ->
                    spinner_item_location = item
                    if (item == 0) {
                        Toast.makeText(this@PrintCN, "Select Printer", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PrintCN, list.get(item), Toast.LENGTH_SHORT).show()
                        btn_connect.setEnabled(true)
                    }
                    spinner_text!!.setText(mDeviceList!!.get(spinner_item_location - 1).name)

                    dialog.dismiss()
                })
            builder.show()
        }
        // spinnerDialog = SpinnerDialog(this, getPairedDeviceArray(mDeviceList), "Select Printer")
        /*spinnerDialog.bindOnSpinerListener(OnSpinerItemClick { item: String?, i: Int ->
            spinner_text.setText(item)
            spinner_item_location = i
            if (spinner_text.getText().toString() == "Select Printer") {
                Toast.makeText(this@PrintCN, "Select Printer", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@PrintCN, item, Toast.LENGTH_SHORT).show()
                btn_connect.setEnabled(true)
            }
        })*/
        // card_spinner.setOnClickListener(View.OnClickListener { view: View? -> spinnerDialog.showSpinerDialog() })
        btn_connect.setOnClickListener(View.OnClickListener { view: View? ->
            if (spinner_text.getText() != "Select Printer") {
                /* val myTask: MyTask =
                     MyTask()
                 myTask.execute()*/
                progressDialog.show()
                Thread(Runnable {
                    if (mDeviceList == null || mDeviceList.size == 0) {
                        Toast.makeText( this@PrintCN, "Kindly Paired any bluetooth Printer",
                            Toast.LENGTH_SHORT) .show()
                        return@Runnable
                    }
                    bluetoothDevice = mDeviceList.get(spinner_item_location - 1)
                    bluetoothSocket = mDeviceList!!.get(spinner_item_location - 1)
                        .createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))

                    if (!bluetoothSocket!!.isConnected) {
                        try {
                            bluetoothSocket!!.connect()
                            outputStream = bluetoothSocket!!.getOutputStream()
                            inputStream = bluetoothSocket!!.getInputStream()
                        } catch (e: java.lang.Exception) {
                        }

                    } else {
                        //   bluetoothSocket!!.close()

                    }


                    runOnUiThread {
                        if (bluetoothSocket!!.isConnected) {
                            spinner_text!!.setText(mDeviceList!!.get(spinner_item_location - 1).name)
                            btn_connect!!.setText("Connected")

                        } else {

                            spinner_text!!.setText("Select Printer")
                            btn_connect!!.setText("Connect")
                            //   Toast.makeText(this,"DisConnected", Toast.LENGTH_SHORT).show()
                        }
                        progressDialog!!.dismiss()
                    }
                }).start()
            } else {
                Toast.makeText(this@PrintCN, "Select Printer", Toast.LENGTH_SHORT).show()
            }
        })

        /* btn_print.setOnClickListener {
             MyTaskPrint(response.body().cn_enquiry)
         }*/
    }

    private fun getPairedDeviceArray(data: java.util.ArrayList<BluetoothDevice>?): java.util.ArrayList<String>? {
        val list = java.util.ArrayList<String>()
        if (data == null) return list
        val size = data.size
        for (i in 0 until size) {
            list.add(data[i].name)
        }
        return list
    }

    fun isConnected(): Boolean {
        return bluetoothSocket != null
    }

    fun disconnect() {
        if (bluetoothSocket == null) {
            Toast.makeText(this@PrintCN, "Socket is not connected", Toast.LENGTH_SHORT).show()
        }
        try {
            bluetoothSocket!!.close()

        } catch (e: IOException) {
            println(e.message)
        }
    }

    @Throws(Exception::class)
    private fun createBond(device: BluetoothDevice) {
        try {
            val cl = Class.forName("android.bluetooth.BluetoothDevice")
            val par = arrayOf<Class<*>>()
            val method = cl.getMethod("createBond", *par)
            method.invoke(device)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun openBluetoothPrinter() {
        try {
            BTMAC = bluetoothDevice!!.address
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(BTMAC)
            val uuidSting = UUID
                .fromString("00001101-0000-1000-8000-00805f9b34fb")
            bluetoothSocket = bluetoothDevice!!
                .createRfcommSocketToServiceRecord(uuidSting)
            bluetoothSocket!!.connect()
            outputStream = bluetoothSocket!!.outputStream
            inputStream = bluetoothSocket!!.inputStream
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private fun MyTaskPrint(cnEnquiry: List<CnEnquiry>) {
        Thread(Runnable {
            try {
                val mycnr = cnEnquiry.get(0).CNR
                var mycnra = arrayOf("", "", "", "", "", "", "", "", "")
                var mycnraa = mycnr.split("*").toTypedArray()
                try {
                    for (i in 0 until mycnraa.size) {
                        mycnra[i] = mycnraa[i]
                    }
                } catch (e: java.lang.Exception) {

                }


                val mycne = cnEnquiry.get(0).CNE
                var mycnea = arrayOf("", "", "", "", "", "", "", "", "")
                var mycneaa = mycne.split("*").toTypedArray()
                try {
                    for (i in 0 until mycneaa.size) {
                        mycnea[i] = mycneaa[i]
                    }
                } catch (e: java.lang.Exception) {

                }

                val bmp = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.ic_error_outline_red_24dp
                )
                val size: Int = bmp.getRowBytes() * bmp.getHeight()
                val b: ByteBuffer = ByteBuffer.allocate(size)

                bmp.copyPixelsToBuffer(b)

                val bytes = ByteArray(size)

                try {
                    b.get(bytes, 0, bytes.size)
                } catch (e: BufferUnderflowException) {
                    // always happens//ZQ 120
                }








                val expressblck = arrayOf(

                    "SIZE 76 mm, 177 mm\r\n",
                    "BLINE 3 mm, 0 mm\r\n",
                    "SPEED 3\r\n",
                    "DENSITY 7\r\n",
                    "SET RIBBON OFF\r\n",
                    "DIRECTION 0,0\r\n",
                    "REFERENCE 0,0\r\n",
                    "OFFSET 0 mm\r\n",
                    "SET PEEL OFF\r\n",
                    "SET CUTTER OFF\r\n",
                    "SET PARTIAL_CUTTER OFF\r\n",
                    "SET TEAR ON\r\n",


                    "CLS\r\n",
                    "BOX 20,25,567,1395,5\r\n",
                    "CODEPAGE 1252\r\n",

                    "TEXT 15,42,\"ROMAN.TTF\",90,1,8,\"Sign of CNR\"\r\n",
                    "TEXT 15,474,\"ROMAN.TTF\",90,1,8,\"Sign of CNE\"\r\n",
                    "TEXT 15,931,\"ROMAN.TTF\",90,1,8,\"Digital Signature not required\"\r\n",
                    // "TEXT 20,42,\"ROMAN.TTF\",90,1,8,\"Signature\"\r\n",


                    //  "TEXT 460,41,\"ROMAN.TTF\",90,7,8,\"" +mycnra[1]+ "\"\r\n",
                    // "PUTBMP 460,41,\"XOM.BMP\"\r\n",
                    "PUTBMP 460,41,\"XOM.BMP\"\r\n",
                    "TEXT 397,42,\"ROMAN.TTF\",90,1,8,\"" + mycnra[0] + "\"\r\n",
                    "TEXT 361,43,\"ROMAN.TTF\",90,7,8,\"" + mycnra[1] + "\"\r\n",
                    "TEXT 336,43,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnra[2]
                    }" + "\"\r\n",
                    "TEXT 311,43,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnra[3]
                    }" + "\"\r\n",
                    "TEXT 285,43,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnra[4]
                    }" + "\"\r\n",
                    "TEXT 260,43,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnra[5]
                    }" + "\"\r\n",

                    "TEXT 228,43,\"ROMAN.TTF\",90,6,8,\"" + "${
                        mycnra[6]
                    }"
                            +
                            "\"\r\n",

                    "TEXT 398,474,\"ROMAN.TTF\",90,1,8,\"" + mycnea[0] + "\"\r\n",
                    "TEXT 363,473,\"ROMAN.TTF\",90,1,8,\"" + mycnea[1] + ",\"\r\n",
                    "TEXT 338,472,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnea[2]
                    }" + "\"\r\n",
                    "TEXT 312,474,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnea[3]
                    }" + "\"\r\n",
                    "TEXT 284,474,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnea[4]
                    }" + "\"\r\n",


                    "TEXT 257,474,\"ROMAN.TTF\",90,1,8,\"" + "${
                        mycnea[5]
                    }" + "\"\r\n",

                    "TEXT 228,474,\"ROMAN.TTF\",90,6,8,\"" + "${
                        mycnea[6]
                    }"
                            +
                            "\"\r\n",
                    /*  "TEXT 180,43,\"ROMAN.TTF\",90,6,8,\"" + "DIM 23*43*56  ,23*43*56 ,   23*43*56 " +
                              "\"\r\n",*/
                    "TEXT 180,43,\"ROMAN.TTF\",90,6,8,\"" + "DIM ${cnEnquiry.get(0).DIMEN}" +
                            "\"\r\n",

                    "TEXT 215,620,\"ROMAN.TTF\",90,6,9,\"" + "Consignee Sign. & Seal(POD)" +
                            "\"\r\n",
                    "TEXT 215,250,\"ROMAN.TTF\",90,6,9,\"" + "Consignor Signature" +
                            "\"\r\n",


                    "TEXT 151,41,\"ROMAN.TTF\",90,1,8,\"DOD/DACC\"\r\n",

                    "TEXT 120,41,\"ROMAN.TTF\",90,1,8,\"Booking Basis\"\r\n",//changed by sharad old PDC DATE
                    "TEXT 120,248,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).BOOKING_BASIS + "\"\r\n",   //changed by sharad old PDC DATE api now add yes
                    "TEXT 87,41,\"ROMAN.TTF\",90,1,8,\"E-Waybills\"\r\n", //
                    "TEXT 54,41,\"ROMAN.TTF\",90,6,8,\"Total Inv.Value\"\r\n", //
                    // "TEXT 54,41,\"ROMAN.TTF\",90,6,8,\"Gross Value\"\r\n", //
                    "TEXT 150,336,\"ROMAN.TTF\",90,6,8,\"COD/DOD Amt.\"\r\n",
                    "TEXT 150,493,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).COLL_AMT + "\"\r\n",
                    //   "TEXT 150,493,\"ROMAN.TTF\",90,1,8,\"" +cnEnquiry.get(0).DOD_AMT+"\"\r\n",  //ADD AMOUNT VIKAS SIR ON DATE 11-12-2023

                    "TEXT 119,336,\"ROMAN.TTF\",90,1,8,\"In Favour\"\r\n",
                    // "TEXT 88,336,\"ROMAN.TTF\",90,1,8,\"EWB No.\"\r\n",
                    // "TEXT 88,490,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).EWB_LIST + "\"\r\n",    //no of innv
                    "TEXT 54,243,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).TOTAL_INV_VAL + "\"\r\n",
                    // "TEXT 550,492,\"ROMAN.TTF\",90,1,8,\"Waybill No.\"\r\n",
                    "TEXT 565,492,\"ROMAN.TTF\",90,1,8,\"" + "BKG. BRANCH" + "\"\r\n",
                    "TEXT 540,492,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).FROM_BR + "\"\r\n",
                    "TEXT 506,483,\"ROMAN.TTF\",90,6,8,\"Transporter Id\"\r\n",    //changed by sharad old Booking Branch
                    "TEXT 470,470,\"ROMAN.TTF\",90,1,8,\"88AAACO4716C1ZV\"\r\n",    //changed by sharad Add tick
                    // "TEXT 465,489,\"ROMAN.TTF\",90,6,8,\"/\"\r\n",    //changed by sharad Add tick

                    "TEXT 432,633,\"ROMAN.TTF\",90,1,8,\"Consignee\"\r\n",


                    "TEXT 151,248,\"ROMAN.TTF\",90,6,9,\"-" + cnEnquiry.get(0).DOD_DACC + "\"\r\n",
                    "TEXT 120,493,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).IN_FAVOUR + "\"\r\n",

                    "TEXT 148,700,\"ROMAN.TTF\",90,1,8,\"Total Freight\"\r\n",    //by sharad oldFreight and new total freight
                    "TEXT 116,700,\"ROMAN.TTF\",90,1,8,\"GST\"\r\n",
                    "TEXT 87,700,\"ROMAN.TTF\",90,1,8,\"Grand Total\"\r\n", //by sharad Total and new Grand total
                    //  "TEXT 54,481,\"ROMAN.TTF\",90,1,8,\"Said To Contain\"\r\n",
                    "TEXT 52,705,\"ROMAN.TTF\",90,1,8,\"" + "ITEM DES.- "+cnEnquiry.get(0).SAID_TO_CONTAIN + "\"\r\n",
                    "TEXT 151,830,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).FREIGHT + "\"\r\n",
                    "TEXT 88,835,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).TOTAL + "\"\r\n",
                    "TEXT 117,835,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).GST + "\"\r\n",
                    //   "TEXT 470,507,\"0\",90,10,8,\"" +cnEnquiry.get(0).FROM_BR+"\"\r\n",
                    "TEXT 504,655,\"ROMAN.TTF\",90,8,8,\"Pick Up Date\"\r\n",
                    "TEXT 505,818,\"ROMAN.TTF\",90,6,8,\"EDD\"\r\n",
                    "TEXT 467,818,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).EDD +
                            "\"\r\n",
                    "TEXT 467,655,\"ROMAN.TTF\",90,7,8,\"" + cnEnquiry.get(0).PICKUP_DATE +
                            "\"\r\n",

                    "TEXT 398,931,\"ROMAN.TTF\",90,1,8,\"Freight Mode\"\r\n",                            //by sharad old Dest. Pincode
                    "TEXT 398,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).FRE_MODE + "\"\r\n",        //by sharad
                    "TEXT 364,931,\"ROMAN.TTF\",90,6,8,\"CFT RATE\"\r\n",                             //by sharad old Mode Of Payment new CFT RATE
                    "TEXT 364,1157,\"ROMAN.TTF\",90,6,8,\"" +"${if(cnEnquiry.get(0).CN_CFT_RATE!=null) cnEnquiry.get(0).CN_CFT_RATE else "-" }"+
                            "\"\r\n",                             //by sharad Add 8
                    "TEXT 337,931,\"ROMAN.TTF\",90,1,8,\"Booking Mode\"\r\n",
                    // "TEXT 306,931,\"ROMAN.TTF\",90,1,8,\"Dimension\"\r\n",            //4th colomn tag by sharad Add Dimension
                    "TEXT 306,931,\"ROMAN.TTF\",90,1,8,\"QTY\"\r\n",            //4th colomn tag by sharad Add Dimension
                    "TEXT 306,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).QTY + "\"\r\n",                     //4th colmn value  OMEXPRESS00000223
                    "TEXT 279,931,\"ROMAN.TTF\",90,1,8,\"No. of Pkg\"\r\n",
                    "TEXT 247,931,\"ROMAN.TTF\",90,1,8,\"Actual Weight\"\r\n",
                    "TEXT 217,931,\"ROMAN.TTF\",90,7,8,\"Chargeable wt\"\r\n",                        //Change by sharad old Gateway new Chargeable weight
                    "TEXT 216,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).CH_WT + "\"\r\n",
                    "TEXT 183,931,\"ROMAN.TTF\",90,1,8,\"Packing Type\"\r\n",
                    "TEXT 150,937,\"ROMAN.TTF\",90,1,8,\"Invoice No.\"\r\n",
                    // "TEXT 114,937,\"ROMAN.TTF\",90,1,7,\"" + cnEnquiry.get(0).INV_LIST + "\"\r\n",
                    "TEXT 130,937,\"ROMAN.TTF\",90,1,7,\"" +
                            "${ if(cnEnquiry.get(0).INV_LIST.length>20) cnEnquiry.get(0).
                            INV_LIST.substring(0,20)+""
                            else cnEnquiry.get(0).INV_LIST }" + "\"\r\n",

                    "TEXT 102,937,\"ROMAN.TTF\",90,1,7,\"" +
                            "${ if(cnEnquiry.get(0).INV_LIST.length>20 &&
                                cnEnquiry.get(0).INV_LIST.length<40)
                            {
                                cnEnquiry.get(0).INV_LIST.substring(20,cnEnquiry.get(0).INV_LIST.length)+""}
                            else if(cnEnquiry.get(0).INV_LIST.length>40){
                                cnEnquiry.get(0).INV_LIST.substring(20,40) }
                            else {
                                ""
                            }
                            }" + "\"\r\n",


                    "TEXT 74,937,\"ROMAN.TTF\",90,1,7,\"" +
                            "${ if(cnEnquiry.get(0).INV_LIST.length>40 &&
                                cnEnquiry.get(0).INV_LIST.length<60)
                            {
                                cnEnquiry.get(0).INV_LIST.substring(40,cnEnquiry.get(0).INV_LIST.length)+""}
                            else if(cnEnquiry.get(0).INV_LIST.length>60){
                                cnEnquiry.get(0).INV_LIST.substring(40,60)+"**" }
                            else {
                                ""
                            }
                            }" + "\"\r\n",





                    /*  "TEXT 114,937,\"ROMAN.TTF\",90,1,7,\"" +
                              "${ if(cnEnquiry.get(0).INV_LIST.length>20) cnEnquiry.get(0).
                              INV_LIST.substring(0,18)+"**"
                              else cnEnquiry.get(0).INV_LIST }" + "\"\r\n",*/


                    // "TEXT 434,1074,\"ROMAN.TTF\",90,1,8,\"Party Nm.{${cnEnquiry.get(0).BILLING_NAME}}\"\r\n",
                    "TEXT 434,950,\"ROMAN.TTF\",90,1,8,\"Bill To {${cnEnquiry.get(0).BILLING_NAME}}\"\r\n",

                    "TEXT 366,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).MODE_OF_PAYMENT + "\"\r\n",
                    "TEXT 335,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).SERVICE_LINE + "\"\r\n",


                    "TEXT 280,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).PKG + "\"\r\n",   //No. of PKG

                    "TEXT 247,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).ACT_WT + "\"\r\n",

                    "TEXT 181,1157,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).PACKING_TYPE + "\"\r\n",


                    "TEXT 159,1250,\"ROMAN.TTF\",90,1,8,\"" + "|" + "\"\r\n",
                    "TEXT 141,1250,\"ROMAN.TTF\",90,1,8,\"" + "|" + "\"\r\n",
                    "TEXT 121,1250,\"ROMAN.TTF\",90,1,8,\"" + "|" + "\"\r\n",
                    "TEXT 101,1250,\"ROMAN.TTF\",90,1,8,\"" + "|" + "\"\r\n",
                    "TEXT 81,1250,\"ROMAN.TTF\",90,1,8,\"" + "|" + "\"\r\n",
                    "TEXT 61,1250,\"ROMAN.TTF\",90,1,8,\"" + "|" + "\"\r\n",
                    "TEXT 45,1250,\"ROMAN.TTF\",90,1,8,\"" + "|" + "\"\r\n",

                    //hard code    "QRCODE 30,1130, H, 2, M, 0, M2,\"B0046upi://pay?pa=paytmqr1lgbdyt6wn@paytm&pn=Paytm\"\r\n",
                    //  "QRCODE 30,1130, H, 2, M, 0, M2,\"B0046"
                    "QRCODE 30,1130, H, 2, M, 0, M2,\"B0046"
                            + cnEnquiry.get(0).QR_STRING +
                            "\"\r\n",


                    "TEXT 53,1267,\"ROMAN.TTF\",90,1,8,\"Signature\"\r\n",
                    "BAR 60,25, 4, 1094\r\n",
                    //modfied "BAR 23,464, 542, 4\r\n",
                    //  "BAR 91,464, 482, 4\r\n",//3rd  vertical line
                    "BAR 91,464, 60, 4\r\n",//3rd  vertical line
                    "BAR 25,348, 35, 4\r\n",
                    "TEXT 52,370,\"ROMAN.TTF\",90,1,8,\"Part No.-${cnEnquiry.get(0).QTY.toString()}\"\r\n",
                    "BAR 180,464, 388, 4\r\n",//3rd  vertical line

                    "BAR 152,30, 4, 1363\r\n",
                    "BAR 401,27, 4, 1363\r\n",
                    // "BAR 89,25, 4, 1093\r\n",
                    "BAR 89,25, 4, 898\r\n",  //3nd line bottom in box
                    "BAR 437,30, 4, 1363\r\n",
                    "BAR 23,231, 131, 4\r\n",
                    //modified 12122024  "BAR 61,329, 93, 4\r\n",
                    "BAR 91,329, 62, 4\r\n",
                    // "BAR 120,27, 4, 1092\r\n",
                    "BAR 120,27, 4, 900\r\n",   //3rd line bottom in box
                    "BAR 180,27, 4, 900\r\n",   //new working
                    "BAR 63,826, 93, 4\r\n",
                    "BAR 61,922, 506, 4\r\n",
                    "BAR 64,691, 89, 4\r\n",

                    "BAR 22,676, 40, 4\r\n",
                    "BAR 24,1115, 379, 4\r\n",
                    "BAR 186,924, 4, 468\r\n",
                    "BAR 219,925, 4, 466\r\n",
                    "BAR 250,925, 4, 467\r\n",
                    "BAR 281,927, 4, 467\r\n",
                    "BAR 311,927, 4, 467\r\n",
                    "BAR 338,926, 4, 464\r\n",
                    "BAR 370,925, 4, 469\r\n",
                    // "TEXT 89,249,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).TOTAT_EWB + "\"\r\n",   //Total E way bills
                    // "TEXT 89,249,\"ROMAN.TTF\",90,1,8,\"" + "123456789087,  098765432145,   23456765432" + "\"\r\n",   //Total E way bills
                    //  "TEXT 89,249,\"ROMAN.TTF\",90,1,8,\"" + "${cnEnquiry.get(0).EWB_LIST}" + "\"\r\n",   //Total E way bills
                    "TEXT 89,249,\"ROMAN.TTF\",90,1,8,\"" + "${ if(cnEnquiry.get(0).EWB_LIST!=null){if(cnEnquiry.get(0).EWB_LIST.length>42) cnEnquiry.get(0).EWB_LIST.substring(0,40)+"**" else cnEnquiry.get(0).EWB_LIST} else "-"}  " + "\"\r\n",   //Total E way bills

                    "TEXT 433,131,\"ROMAN.TTF\",90,1,8,\"Consignor\"\r\n",
                    "BAR 441,646, 122, 4\r\n",
                    "BAR 440,806, 69, 4\r\n",
                    "BAR 508,468, 4, 459\r\n",
                    "BAR 473,468, 4, 459\r\n",
                    "TEXT 565,686,\"ROMAN.TTF\",90,1,8,\"" + "DLY BRANCH  " + "\"\r\n",
                    "TEXT 540,686,\"ROMAN.TTF\",90,1,8,\"" + cnEnquiry.get(0).TO_BR + "\"\r\n",


                  /*  "TEXT 560,160,\"ROMAN.TTF\",90,1,12,\"OM Logistics Ltd.\"\r\n",*/
                    "TEXT 560,30,\"ROMAN.TTF\",90,1,11,\"Om Logistics Supply Chain pvt. Ltd\"\r\n",
                    //0 "TEXT 508,41,\"ROMAN.TTF\",90,1,18,\"130, Transport Centre, Ring Road,\"\r\n",


                    "TEXT 530,160,\"ROMAN.TTF\",90,6,8,\"130, Transport Centre, Ring Road,\"\r\n",   //0 because of text prblems
                    // "TEXT 483,31,\"ROMAN.TTF\",90,6,8,\"130, Transport Centre, Ring Road,\"\r\n",   //0 because of text prblems
                    "TEXT 510,160,\"ROMAN.TTF\",90,6,8,\"Punjabi Bagh New Delhi 110035," + "\"\r\n",
                    "TEXT 490,160,\"ROMAN.TTF\",90,6,8,\"Gst- " + cnEnquiry.get(0).GSTN +
                            "\"\r\n",
                    "TEXT 470,160,\"ROMAN.TTF\",90,6,8,\"omgroup@omlogistics.co.in " +
                            "\"\r\n",


                    // "BARCODE 543,974,\"128M\",64,0,90,4,8,\"!105100001427857\"",
                    // """BARCODE 255,295,"128",50,1,0,2,2,"8895890"""".trimIndent(),
                    "BARCODE 543,974,\"128\",50,0,90,2,2,\"" + cnEnquiry.get(0).CCN_NO + "\"\r\n",
                    //    "BARCODE 543,974,\"128M\",64,0,90,4,8,\""+edt_cn.text.toString()+"\"\r\n",
                    //"TEXT 473,999,\"0\",90,9,8,\"CN: " + edt_cn.text.toString() + "\"\r\n",
                    "TEXT 473,940,\"0\",90,9,8,\"CN: " + cnEnquiry.get(0).CCN_NO
                            +"${if(cnEnquiry.get(0).MCN_NO!=null){" (${cnEnquiry.get(0).MCN_NO.toString()})"} else ""}"+ "\"\r\n",
                    "PRINT 1,1\r\n"
                )


                // for (command in expressblck) {

                    for (command in expressblck) {
                        outputStream!!.write(command.toByteArray())
                    }


                outputStream!!.flush()
            } catch (e: Exception) {
                e.message
            }
        }).start()


    }


    private fun ShowDailog(cnEnquiry: List<CnEnquiry>) {
        val alertDialogBuilder = AlertDialog.Builder(this@PrintCN)
        alertDialogBuilder.setIcon(R.drawable.ic_success)

        alertDialogBuilder.setMessage("Data Found")
        alertDialogBuilder.setPositiveButton(
            "Print"
        ) { arg0, arg1 ->
            //PRINT(response.body().cn_no.toString() + "")
            //  for(i in 1..3)
            MyTaskPrint(cnEnquiry)

        }

//...............

        alertDialogBuilder.setNeutralButton(
            "Exit"
        ) { dialog, which -> finish() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    };


    override fun onStop() {
        super.onStop()
        try {
            bluetoothSocket!!.close()
        } catch (e: java.lang.Exception) {
        }
    }
}