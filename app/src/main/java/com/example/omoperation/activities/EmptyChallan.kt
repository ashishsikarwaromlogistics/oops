package com.example.omoperation.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityEmptyChallanBinding
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.model.empty.EmptyMod
import com.example.omoperation.model.vehicle.VehicleValidateMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmptyChallan : AppCompatActivity() {
    lateinit var title: TextView
    lateinit var binding: ActivityEmptyChallanBinding

    val cp by lazy { CustomProgress(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_empty_challan)
        title=findViewById(R.id.title)
        title.setText("Empty Challan Creation")
        binding.source.setOnClickListener {
            val intent = Intent(this, BranchesAct::class.java)
            intent .putExtra("senddata",1)
            sourcelauncher.launch(intent)
        }
        binding.destination.setOnClickListener {
            val intent = Intent(this, BranchesAct::class.java)
            intent .putExtra("senddata",1)
            destinationlauncher.launch(intent)
        }
        binding.approveby.setOnClickListener {
            val intent = Intent(this, EmployeesAct::class.java)
            intent .putExtra("senddata",1)
            employeelauncher.launch(intent)
        }

        binding.createchallan.setOnClickListener {
            if(Utils.haveInternet(this))
                lifecycleScope.launch {
                    if(checkvalidate()){
                        cp.show()
                        checklorry()
                    }
                    else{
                        Utils.showDialog(this@EmptyChallan,"error","Please fill All Fields",R.drawable.ic_error_outline_red_24dp)
                    }
                }
        }

    }
    fun checkvalidate(): Boolean{
        if(binding.source.text.toString().equals(""))
            return false
        else  if(binding.destination.text.toString().equals(""))
            return false
        else  if(binding.lorryNo.text.toString().equals(""))
            return false
        return true
    }
    var sourcelauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            //  des_branch_code=data!!.getStringExtra("branchcode").toString()
            binding.source.setText(data!!.getStringExtra("branchcode"))
        }
    }
    var destinationlauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            //  binding.destination=data!!.getStringExtra("branchcode").toString()
            binding.destination.setText(data!!.getStringExtra("branchcode"))
        }
    }
    var employeelauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            //  binding.destination=data!!.getStringExtra("branchcode").toString()
            binding.approveby.setText(data!!.getStringExtra("empcode"))
        }
    }
    fun CreateChallan(){
        cp.show()
        val mod= EmptyMod(
            binding.destination.text.toString(),OmOperation.getPreferences(Constants.EMP_CODE,""),binding.lorryNo.text.toString(),binding.source.text.toString()
        ,binding.approveby.text.toString())
        lifecycleScope.launch {
            val resp=   ApiClient.getClient().create(ServiceInterface::class.java).emptrychallan(mod)
            cp.dismiss()
            if(resp!=null){
                if(resp.code()==200){
                    if(resp.body()!!.error.equals("false")){
                        Utils.showDialog(this@EmptyChallan,"Success",resp.body()!!.response.toString(),R.drawable.ic_success)
                    }
                    else {
                        error("Challan not created{${resp.body()!!.response.toString()}}")
                    }
                }
                else  error("{${"code="+resp.code() + "message"+resp.message()}}")
            }
            else
                error("Response not created")
        }


    }
    fun error(msg : String){
        Utils.showDialog(this,"error",msg,R.drawable.ic_error_outline_red_24dp)
    }
    fun checklorry(){
        val mod= VehicleValidateMod()
        mod.lorryno=binding.lorryNo.text.toString()
        lifecycleScope.launch {
            val response=  ApiClient.getClient().create(ServiceInterface::class.java).vehicle_validate_checklist(Utils.getheaders(),mod)
            cp.dismiss()
            if(response.body()!!.error.equals("false")){
                CreateChallan()
            }
            else {
                error("Invalid Vehcle num")
            }
        }



    }
    fun Back(v: View){
        onBackPressed()
    }
}