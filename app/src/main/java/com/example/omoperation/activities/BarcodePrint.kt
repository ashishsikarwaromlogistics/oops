package com.example.omoperation.activities

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ListAdapter
import com.example.omoperation.databinding.ActivityBarcodePrintBinding
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BarcodePrint : AppCompatActivity() {
    lateinit var binding: ActivityBarcodePrintBinding

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var bluetoothDevice: BluetoothDevice? = null
    lateinit var mDeviceList: ArrayList<BluetoothDevice>

    private val spinner_item_location = -1
    var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
    var BTMAC: String? = null
    val items = ArrayList<String>()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_print)
        mDeviceList = ArrayList()
        getPairedDevices()
       binding.print.setOnClickListener {
          if(isConnected()){
         lifecycleScope.launch {
             //for(i in 0 until 3)
             printdata("1","1234560001","123456","Delhi","Agra","1","123456","12:19","07-08-2024")
         }
          }
       }
    }


    suspend fun printdata(box: String,bar : String,cn:String,from:String,to:String,currentBox:String,manualNO:String,time:String,date:String){
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
         ///   "BAR 100,100, 3, 130\r\n",//vertical line
         //   "BOX 20,25,130,200,3\r\n",  //x,y,width,height,radius
            //"BAR 100,100, 122, 3\r\n",//horizontal

          //  "BOX 8,135,330,140,4\r\n",
           // "BOX 8,135,150,140,4\r\n",
            "BOX 8,135,300,170,3\r\n",//x y w , h from y axis,radius
            ("TEXT 10,140,\"ROMAN.TTF\",0,2,12,\"" + cn
                    + "\"\r\n"),
            "BOX 8,175,300,205,3\r\n",
            ("TEXT 10,180,\"ROMAN.TTF\",0,2,9,\"" + from
                    + "\"\r\n"),
            "BOX 8,215,400,250,3\r\n",
            (" TEXT 10,220,\"ROMAN.TTF\",0,2,11,\""
                    + to + "\"\r\n"),
            "BOX 8,255,400,290,3\r\n",
            ("TEXT 10,260,\"ROMAN.TTF\",0,2,11,\"PKT   : "
                    + currentBox + " of " + box + "\"\r\n"),
            ("TEXT 200,260,\"ROMAN.TTF\",0,2,9,\"R/NO : "
                    + (if (manualNO.length > 20) manualNO.substring(
                0,
                19
            ) else manualNO) + "\"\r\n"),
            "BOX 8,335,200,365,3\r\n",
            ("TEXT 10,340,\"ROMAN.TTF\",0,2,9,\"TIME : " + time
                    + "\"\r\n"),
            "BOX 8,295,200,325,3\r\n",
            ("TEXT 10,300,\"ROMAN.TTF\",0,2,9,\"DATE : " + date
                    + "\"\r\n"), "PRINT 1,1\r\n"
        )

        for (command in ccmm) {

            //   for (String command : Retail_Express) {
            //   for (String command : rugtec) {
           outputStream!!.write(command.toByteArray())
            outputStream!!.flush()
          //  publishProgress(currentBox)
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
                    Toast.makeText(this@BarcodePrint, "Failed to pair device", Toast.LENGTH_SHORT).show()
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
            BTMAC = bluetoothDevice?.address
            bluetoothDevice = bluetoothAdapter?.getRemoteDevice(BTMAC)
            val uuidString = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
            bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(uuidString)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            inputStream = bluetoothSocket?.inputStream
        } catch (ex: Exception) {
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

    private fun dismissProgressDialog() {
        progressDialog.dismiss()
    }
}
