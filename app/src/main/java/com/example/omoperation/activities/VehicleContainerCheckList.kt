package com.example.omoperation.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityVehicleContainerCheckListBinding
import com.google.gson.Gson
import com.example.omoperation.model.checklist.VehicleContainerEntry
import com.example.omoperation.model.checklist.VehicleContainerEntryResp
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleContainerCheckList : AppCompatActivity() {
    private lateinit var container_roof: TextView
    private lateinit var vehicle_no: EditText
    private lateinit var container_floor: TextView
    private lateinit var container_side: TextView
    private lateinit var container_door: TextView
    private lateinit var container_tripal: TextView
    private lateinit var container_polythene: TextView
    private lateinit var container_safity_kit: Spinner
    private lateinit var vehicle_container_rem: EditText
    private lateinit var uploadBtn: Button
      var ContainerRoof: String?=null
      var ContainerSide: String?=null
      var ContainerFloor: String?=null
      var ContainerDoor: String?=null
      var ContainerTripal: String?=null
      var ContainerPolythene: String?=null
      var VehicleContainerRem: String?=null
    lateinit var vehicleNum: String
    var KitStatus: String = "NO"


    var sheetcheck: Int = 0
    var beltcheck: Int = 0
    var bagcheck: Int = 0
    var netcheck: Int = 0
    val empcode: String by lazy {
        OmOperation.getPreferences(Constants.EMP_CODE, "")
    }
    val branch: String by lazy { OmOperation.getPreferences(Constants.BCODE, "") }
    private lateinit var progressDialog: ProgressDialog
    lateinit var binding: ActivityVehicleContainerCheckListBinding
    lateinit var title: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this, R.layout.activity_vehicle_container_check_list)
        title=findViewById<TextView>(R.id.title)
        title.setText("Container Check List")
      //  AppConfig.setupToolBar("Container Check List", this)
        progressDialog = ProgressDialog(this).apply {
            this.setMessage("Loading...")
            this.setCancelable(false)
        }
        init()
        OnClick()
    }

    fun OnClick() {
        container_roof.setOnClickListener {
            openDialogOk(this) { selectedOption ->
                // Do something with the selected option
                container_roof.setText(selectedOption.toString())
                if (selectedOption.equals("OK")) {
                    ContainerRoof = "Y"
                } else if (selectedOption.equals("NOT OK")) {
                    ContainerRoof = "N"
                }
            }
        }
        container_side.setOnClickListener {
            openDialogOk(this) { selectedOption ->
                // Do something with the selected option
                container_side.setText(selectedOption.toString())
                if (selectedOption.equals("OK")) {
                    ContainerSide = "Y"
                } else if (selectedOption.equals("NOT OK")) {
                    ContainerSide = "N"
                }
            }
        }
        container_floor.setOnClickListener {
            openDialogOk(this) { selectedOption ->
                // Do something with the selected option
                container_floor.setText(selectedOption.toString())
                if (selectedOption.equals("OK")) {
                    ContainerFloor = "Y"
                } else if (selectedOption.equals("NOT OK")) {
                    ContainerFloor = "N"
                }
            }
        }
        container_door.setOnClickListener {
            openDialogOk(this) { selectedOption ->
                // Do something with the selected option
                container_door.setText(selectedOption.toString())
                if (selectedOption.equals("OK")) {
                    ContainerDoor = "Y"
                } else if (selectedOption.equals("NOT OK")) {
                    ContainerDoor = "N"
                }
            }
        }
        container_tripal.setOnClickListener {

            openDialogYES(this) { selectedOption ->
                // Do something with the selected option
                container_tripal.setText(selectedOption.toString())
                if (selectedOption.equals("YES")) {
                    ContainerTripal = "Y"
                } else if (selectedOption.equals("NO")) {
                    ContainerTripal = "N"
                }
            }
        }
        container_polythene.setOnClickListener {

            openDialogYES(this) { selectedOption ->
                // Do something with the selected option
                container_polythene.setText(selectedOption.toString())
                if (selectedOption.equals("YES")) {
                    ContainerPolythene = "Y"
                } else if (selectedOption.equals("NO")) {
                    ContainerPolythene = "N"
                }
            }
        }
        showAlertCheckList()
        uploadBtn.setOnClickListener {
            if (Utils.haveInternet(this)){
                  if(vehicle_no.text.toString().equals("") || (vehicle_no.text.toString().length
                          ?: 0) < 2
                  ){
                    Utils.showDialog(this,"error","Please Add Vehicle Number properly",R.drawable.ic_error_outline_red_24dp)
                }
                else if(ContainerRoof ==null){
                    Utils.showDialog(this,"error","Please Add ContainerRoof",R.drawable.ic_error_outline_red_24dp)
                }

                else if(ContainerSide ==null){
                    Utils.showDialog(this,"error","Please Add ContainerSide",R.drawable.ic_error_outline_red_24dp)
                }
                else if(ContainerFloor ==null){
                    Utils.showDialog(this,"error","Please Add ContainerFloor",R.drawable.ic_error_outline_red_24dp)
                }
                else if(ContainerDoor ==null){
                    Utils.showDialog(this,"error","Please Add ContainerDoor",R.drawable.ic_error_outline_red_24dp)
                }
                else if(ContainerTripal ==null){
                    Utils.showDialog(this,"error","Please Add ContainerTripal",R.drawable.ic_error_outline_red_24dp)
                }
                else if(ContainerPolythene ==null){
                    Utils.showDialog(this,"error","Please Add ContainerPolythene",R.drawable.ic_error_outline_red_24dp)
                }
                else if(VehicleContainerRem ==null){
                    Utils.showDialog(this,"error","Please Add VehicleContainerRem",R.drawable.ic_error_outline_red_24dp)
                }
                else{
                    apiCall()
                }
            }



        }
    }

    fun init() {
        container_floor = findViewById(R.id.container_floor)
        vehicle_no = findViewById(R.id.vehicle_no)
        container_roof = findViewById(R.id.container_roof)
        container_side = findViewById(R.id.container_side)
        container_door = findViewById(R.id.container_door)
        container_tripal = findViewById(R.id.container_tripal)
        container_polythene = findViewById(R.id.container_polythene)
        container_safity_kit = findViewById(R.id.container_safity_kit)
        vehicle_container_rem = findViewById(R.id.vehicle_container_rem)


        uploadBtn = findViewById(R.id.uploadBtn)
        vehicleNum = vehicle_no.text.toString()
        VehicleContainerRem = vehicle_container_rem.text.toString()
    }

    fun openDialogOk(context: Context, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Option")
        val options = arrayOf("OK", "NOT OK")
        builder.setItems(options) { dialog, which ->
            val selectedOption = options[which]
            dialog.dismiss()
            callback(selectedOption)
        }



        builder.show()
    }

    fun openDialogYES(context: Context, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Option")
        val options = arrayOf("YES", "NO" )
        builder.setItems(options) { dialog, which ->
            val selectedOption = options[which]
            dialog.dismiss()
            callback(selectedOption)
        }



        builder.show()
    }
    var isFirstTime = true
    private fun showAlertCheckList() {

        val safeOpts = ArrayList<String>()
        safeOpts.add("---Select---")
        safeOpts.add("YES")
        safeOpts.add("NO")
        val arrayAdapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, safeOpts)
        container_safity_kit.setAdapter(arrayAdapter2)
        container_safity_kit.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                l: Long
            ) {
                if (isFirstTime) {
                    isFirstTime = false
                    return
                }
                view?.let {
                    if (position == 1) {
                        binding.llSafety.setVisibility(View.VISIBLE)
                    } else {
                        binding.llSafety.setVisibility(View.GONE)
                    }
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
         binding.edtAir.setOnClickListener {
            opend(
                binding.edtAir
            )
        }
        binding.edtLashing.setOnClickListener { opend(binding.edtLashing) }
        binding.edtSheet.setOnClickListener { opend(binding.edtSheet) }
        binding.edtCargo.setOnClickListener { opend(binding.edtCargo) }

    }

    fun apiCall() {
        sheetcheck = Utils.check_null_Int(binding.edtSheet.text.toString())
        beltcheck = Utils.check_null_Int(binding.edtLashing.text.toString())
        bagcheck = Utils.check_null_Int(binding.edtAir.text.toString())
        netcheck = Utils.check_null_Int(binding.edtCargo.text.toString())
       // progressDialog.show()
        val model = VehicleContainerEntry(
            ContainerPolythene!!,
            //container_safity_kit3.text.toString().toInt(),
            bagcheck,
            // container_safity_kit2.text.toString().toInt(),
            beltcheck,
            branch.toInt(),
            //container_safity_kit4.text.toString().toInt(),
            netcheck,
            ContainerDoor!!,
            empcode.toInt(),
            ContainerFloor!!,
            KitStatus,
            "Y",
            vehicle_container_rem.text.toString(),
            ContainerRoof!!,
            // container_safity_kit1.text.toString().toInt(),
            sheetcheck,
            ContainerSide!!,
            ContainerTripal!!,
            vehicle_no.text.toString()
        )
        Log.d("ashishsikarwar",Gson().toJson(model))
        val apiInterface = ApiClient.getscmclient().create(ServiceInterface::class.java)
        val call: Call<VehicleContainerEntryResp> = apiInterface.Container_Chek_List(model)
        call.enqueue(object : Callback<VehicleContainerEntryResp> {
            override fun onResponse(
                call: Call<VehicleContainerEntryResp>,
                response: Response<VehicleContainerEntryResp>
            ) {
                progressDialog.dismiss()
                try {
                    if (response.body()!!.error_status.equals("true")) {
                        Utils.showDialog( this@VehicleContainerCheckList,
                            "error true",
                            response.body()!!.message.toString(),
                            R.drawable.ic_error_outline_red_24dp)

                    } else {
                        Utils.showDialog( this@VehicleContainerCheckList,
                            "Success",
                            response.body()!!.message.toString(),
                            R.drawable.ic_success)
                            blanksallfield()

                        //startActivity(Intent(this@VehicleContainerCheckList,ChallanCreation::class.java))
                        //startActivity(Intent(this@VehicleContainerCheckList,ChallanCreation::class.java))
                    }
                } catch (e: NumberFormatException) {
                }
            }

            private fun blanksallfield() {
                binding.vehicleNo.setText("")
                binding.containerRoof.setText("")
                binding.containerSide.setText("")
                binding.containerFloor.setText("")
                binding.containerDoor.setText("")
                binding.containerTripal.setText("")
                binding.containerPolythene.setText("")
                binding.containerSafityKit.setSelection(0)
                binding.edtSheet.setText("")
                binding.edtAir.setText("")
                binding.edtCargo.setText("")
                binding.edtLashing.setText("")
                binding.vehicleContainerRem.setText("")
            }

            override fun onFailure(call: Call<VehicleContainerEntryResp>, t: Throwable) {
                progressDialog.dismiss()

                Utils.showDialog( this@VehicleContainerCheckList,
                    "onFailure",
                    t.toString(),
                    R.drawable.ic_error_outline_red_24dp)
            }

        })
    }
    private fun opend(textView: TextView) {
        val items = arrayOf<CharSequence>("0", "1", "2", "3", "4", "5")
        // }
        val builder = AlertDialog.Builder(this@VehicleContainerCheckList)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            textView.text = items[item]
            dialog.dismiss()
        }
        builder.show()
    }
}