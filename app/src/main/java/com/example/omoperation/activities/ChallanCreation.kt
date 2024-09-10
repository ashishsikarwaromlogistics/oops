package com.example.omoperation.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityChallanCreationBinding
import com.example.omoperation.model.CommonMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.launch


class ChallanCreation : AppCompatActivity() {
    lateinit var binding: ActivityChallanCreationBinding
    lateinit var cp: CustomProgress
    var touchingBranch=""
    var des_branch_code=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_challan_creation)
        cp=CustomProgress(this)
        binding.source.setText(OmOperation.getPreferences(Constants.BCODE,""))
        setclick()



    }

    private fun setclick() {
        val touchingOpts = ArrayList<String>()
        touchingOpts.add("---Touching Branch Flag---")
        touchingOpts.add("YES")
        touchingOpts.add("NO")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, touchingOpts)
        binding.touchingFlg.setAdapter(arrayAdapter)







        binding.touchingFlg.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                touchingOpts[position]
                if (position == 1) {
                    binding.touchingBranchCard.visibility = View.VISIBLE
                    binding.weightCard.visibility = View.VISIBLE
                } else {
                    binding.touchingBranchCard.visibility = View.GONE
                    binding.weightCard.visibility = View.GONE
                    touchingBranch = ""
                    binding.weight.setText("")
                    binding.touchingBranch.text = "Select Touching Branch"
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        binding.destination.setOnClickListener {
            val intent = Intent(this, BranchesAct::class.java)
            intent .putExtra("senddata",1)
            resultLauncher.launch(intent)

        }
        binding.touchingBranch.setOnClickListener {
            val intent = Intent(this, BranchesAct::class.java)
            intent .putExtra("senddata",1)
            resulttouching.launch(intent)

        }
        binding.odaStation.setOnClickListener {
            val intent = Intent(this, OdaStation::class.java)
            odalauncher.launch(intent)

        }
        binding.edtLoadingPlan.setOnClickListener {
            val intent = Intent(this, LoadingPlan::class.java)
            loadinglauncher.launch(intent)

        }
        binding.edtAir.setOnClickListener(View.OnClickListener { opend(binding.edtAir) })
        binding.edtLashing.setOnClickListener(View.OnClickListener { opend(binding.edtLashing) })
        binding.edtSheet.setOnClickListener(View.OnClickListener { opend(binding.edtSheet) })
        binding.edtCargo.setOnClickListener(View.OnClickListener { opend(binding.edtCargo) })
        binding.scanBtn.setOnClickListener {
            if(validate()){
                if(Utils.haveInternet(this)){
                    checkLorry()
                    //https://api.omlogistics.co.in/vehicle_validate_checklist.php
                    //I  {"lorryno":"OM","type":"challan"}
                }
            }

        }


        if(intent.getStringExtra("from").equals("offline_challan")){
            binding.source.text = "9996 Pickup"
            binding.source.setOnClickListener {
                val b =
                    AlertDialog.Builder(this@ChallanCreation)
                b.setTitle("Select Pickup")
                val types = arrayOf("9996", "9995")
                b.setItems(types) { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                    binding.source.text = types[which]
                }
                b.show()
            }


            binding.destination.setText(OmOperation.getPreferences(Constants.BCODE,""))
            des_branch_code = OmOperation.getPreferences(Constants.BCODE,"")
        }
    }

    private fun checkLorry() {
        cp.show()
        val mod= CommonMod()
        mod.lorryno=binding.lorryNo.text.toString().uppercase()
        mod.type="Challan"
        lifecycleScope.launch {
            val resp=ApiClient.getClient().create(ServiceInterface::class.java).vehicle_validate_checklist(Utils.getheaders(),mod)
            if(resp.code()==200){
                cp.dismiss()
                if(resp.body()!!.error.toString().equals("false"))
                {
                    val bundle = Bundle()
                    bundle.putString("loading_plan", binding.edtLoadingPlan.getText().toString())
                    bundle.putString("to", binding.destination.text.toString())
                    bundle.putString("lorry", binding.lorryNo.text.toString())
                    bundle.putString("seal", binding.sealNo.text.toString())
                    bundle.putString("driverName", binding.driverName.text.toString())
                    bundle.putString("driverMob", binding.driverMob.text.toString())
                    bundle.putString("oda_station_code", binding.odaStation.text.toString())
                    bundle.putString("touchingFlg", binding.touchingFlg.selectedItem.toString())
                    bundle.putString("touchingBranch",touchingBranch)
                    bundle.putString("_weight", binding.weight.text.toString())

                    bundle.putInt(
                        "airbag",
                        Utils.check_null_Int(binding.edtAir.getText().toString())
                    )
                    bundle.putInt(
                        "sheetbelt",
                        Utils.check_null_Int(binding.edtSheet.getText().toString())
                    )
                    bundle.putInt(
                        "lashingbelt",
                        Utils.check_null_Int(binding.edtLashing.getText().toString())
                    )
                    bundle.putInt(
                        "cargo",
                        Utils.check_null_Int(binding.edtCargo.getText().toString())
                    )
                  val intent=  Intent(this@ChallanCreation,BarcodeScanning::class.java)
                    intent.apply { putExtras(bundle) }

                    startActivity(intent)
                 //   Utils.showDialog(this@ChallanCreation,"success","good",R.drawable.ic_success)

                }
            }
            else{
                cp.dismiss()
                Utils.showDialog(this@ChallanCreation,"error","Please enter valid lorry number",R.drawable.ic_error_outline_red_24dp)

            }
        }

    }

    fun validate():Boolean{
         if(binding.driverMob.text.toString().length!=10){
           Utils.showDialog(this,"error","Please enter valid mobile",R.drawable.ic_error_outline_red_24dp)
         return false
         }
         else if(binding.touchingFlg.selectedItemPosition==0){
           Utils.showDialog(this,"error","Please select Touching branch options",R.drawable.ic_error_outline_red_24dp)
         return false
         }
         else if(binding.driverName.text.toString().equals("") ||
             binding.driverName.text.toString().length<2){
           Utils.showDialog(this,"error","Please Enter Driver name ",R.drawable.ic_error_outline_red_24dp)
         return false
         }
        return true
    }

    private fun opend(textView: TextView) {
        val items = arrayOf<CharSequence>("0", "1", "2", "3", "4", "5")
        // }
        val builder = AlertDialog.Builder(this@ChallanCreation)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            textView.text = items[item]
            dialog.dismiss()
        }
        builder.show()
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            des_branch_code=data!!.getStringExtra("branchcode").toString()
            binding.destination.setText(data!!.getStringExtra("branchcode"))
        }
    }
    var resulttouching = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            binding.touchingBranch.setText(data!!.getStringExtra("branchcode"))
        }
    }
    var odalauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            binding.odaStation.setText(data!!.getStringExtra("branchcode"))
        }
    }
    var loadinglauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            binding.edtLoadingPlan.setText(data!!.getStringExtra("loadingno"))
        }
    }
}