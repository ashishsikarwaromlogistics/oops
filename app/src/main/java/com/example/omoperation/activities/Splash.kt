package com.example.omoperation.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.NetworkState
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivitySplashBinding
import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.viewmodel.ValidateViewMod
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

//13422420007710  //
class Splash : AppCompatActivity() {
    lateinit var binding:ActivitySplashBinding
   // private lateinit var mediaPlayer: MediaPlayer
    private lateinit var viewmod: ValidateViewMod
    lateinit var cp: CustomProgress
    lateinit var ap:String
    //  lateinit  var viewmod: SplashViewMod
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)
        window.statusBarColor = Color.BLACK
        //OmOperation.savePreferences(Constants.EMP_PASS,"1234567")
        cp= CustomProgress(this)
        viewmod=ViewModelProvider(this).get(ValidateViewMod::class.java)
        binding.viewmode=viewmod
        binding.lifecycleOwner=this


        binding.logo.animate()
           .alpha(0f)
           .setDuration(1000)
           .withEndAction {
               // Fade in after fade out is complete
               binding.logo.animate()
                   .alpha(1f)
                   .setDuration(1000)
                   .start()
           }.start()

    }

    override fun onResume() {
        super.onResume()
        if(OmOperation.getPreferences2( Constants.SAVE_OLL,"").equals("")){
            saveDeviceid()
        }
        else if(Utils.haveInternet(this)){

                lifecycleScope.launch {
                    val mod=CommonMod()
                    mod.status="omops"
                    val response = withTimeoutOrNull(15000L) { // Set timeout to 15 seconds
                        try {
                            ApiClient.getClient().create(ServiceInterface::class.java).omstaffAppVersion(mod)
                        } catch (e: Exception) {
                            Utils.showDialog(this@Splash,"error{${e.toString()}}","Net Connection is not working properly",R.drawable.ic_error_outline_red_24dp)
                            null // Return null on exception, e.g., network failure
                        }
                    }
                    if(response!=null){
                        if(response.code() ==200){
                            try {
                                val pInfo = packageManager.getPackageInfo(packageName, 0)
                                val versionCode = pInfo.versionCode  // Deprecated in API 28
                                val versionName = pInfo.versionName
                                if(response.body()?.ver.toString().toInt()>versionCode.toInt()){
                                    Utils.showDialog(this@Splash,"error","Please Update your App by MDM",R.drawable.ic_error_outline_red_24dp)
                                }
                                else validate()
                                Log.d("Version Code", "Version code: $versionCode")
                                Log.d("Version Name", "Version name: $versionName")
                            } catch (e: PackageManager.NameNotFoundException) {
                                e.printStackTrace()
                                validate()
                            }

                        }
                        else {
                            validate()
                        }
                    }
                    else
                        Utils.showDialog(this@Splash,"No response","Server error or network connection break",R.drawable.ic_error_outline_red_24dp)
                    //val response=



                }



        }
        else{
            Utils.showDialog(this,"error","No,Internet Connection",R.drawable.ic_error_outline_red_24dp)
        }

    }
   /* override fun onResume() {
        super.onResume()
        if(Utils.haveInternet(this)){
            if(OmOperation.getPreferences(Constants.SAVE_OLL,"true").equals("true")){
                    lifecycleScope.launch {
                        val mod=CommonMod()
                        mod.status="omops"
                        val response = withTimeoutOrNull(15000L) { // Set timeout to 15 seconds
                            try {
                                ApiClient.getClient().create(ServiceInterface::class.java).omstaffAppVersion(mod)
                            } catch (e: Exception) {
                                Utils.showDialog(this@Splash,"error{${e.toString()}}","Net Connection is not working properly",R.drawable.ic_error_outline_red_24dp)
                                null // Return null on exception, e.g., network failure
                            }
                        }
                        if(response!=null){
                            if(response.code() ==200){
                                try {
                                    val pInfo = packageManager.getPackageInfo(packageName, 0)
                                    val versionCode = pInfo.versionCode  // Deprecated in API 28
                                    val versionName = pInfo.versionName
                                    if(response.body()?.ver.toString().toInt()>versionCode.toInt()){
                                        Utils.showDialog(this@Splash,"error","Please Update your App by MDM",R.drawable.ic_error_outline_red_24dp)
                                    }
                                    else validate()
                                    Log.d("Version Code", "Version code: $versionCode")
                                    Log.d("Version Name", "Version name: $versionName")
                                } catch (e: PackageManager.NameNotFoundException) {
                                    e.printStackTrace()
                                    validate()
                                }

                            }
                            else {
                                validate()
                            }
                        }
                        else
                            Utils.showDialog(this@Splash,"No response","Server error or network connection break",R.drawable.ic_error_outline_red_24dp)
                        //val response=



                    }

            }
            else{
                viewmod.trackdeviceid()
             viewmod.issavedeviceid.observe(this,Observer{
                 when (it){
                  is NetworkState.Error ->{
                      cp.dismiss()
                      saveDeviceid()
                  }
                     is com.example.omoperation.NetworkState.Loading -> {
                         cp.show()
                     }
                     is com.example.omoperation.NetworkState.Success<*> -> {
                         cp.dismiss()
                         var resp: CommonRespS = it.data as CommonRespS
                         if(resp.error.equals("false")){
                             validate()
                         }
                         else{
                             saveDeviceid()
                         }
                     }

                 }
             })

            }
        }
        else{
            Utils.showDialog(this,"error","No,Internet Connection",R.drawable.ic_error_outline_red_24dp)
        }

    }*/
    fun validate(){
        if(OmOperation.getPreferences(Constants.ISLOGIN,"0").equals("1")){
            viewmod.checkvalidate()
            viewmod.livedata.observe(this, Observer {
                when (it) {
                    is NetworkState.Error ->{
                        cp.dismiss()
                        Utils.showDialog(this,it.title,it.message,R.drawable.ic_error_outline_red_24dp)
                        AlertDialog.Builder(this)
                            .setTitle(it.title)
                            .setIcon(R.drawable.ic_error_outline_red_24dp)
                            .setMessage(it.message)
                            .setCancelable(false)
                            .setPositiveButton(
                                "OK",
                                DialogInterface.OnClickListener {
                                    dialog: DialogInterface, id: Int -> dialog.dismiss()

                                })
                            .setNegativeButton(
                                "LogIN Again",
                                DialogInterface.OnClickListener {
                                    dialog: DialogInterface, id: Int -> dialog.dismiss()
                                    startActivity(Intent(this,LoginActivity::class.java))
                                    finish()
                                })
                            .show()


                    }

                    is NetworkState.Loading -> {
                        cp.show()
                    }
                    is NetworkState.Success<*> -> {
                        cp.dismiss()
                        startActivity(Intent(this,DashboardAct::class.java))
                        finish()
                    }

                    /*  is com.example.omoperation.NetworkState.Error -> TODO()
                      com.example.omoperation.NetworkState.Loading -> TODO()
                      is com.example.omoperation.NetworkState.Success -> TODO()*/
                }

            })

        }
        else {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
    fun saveDeviceid(){
        val builder = AlertDialog.Builder(this@Splash)
        val layoutInflater = layoutInflater  // Use the activity's inflater, not application context
        val view: View = layoutInflater.inflate(R.layout.save_oll, null)

        val emp_edit_text = view.findViewById<EditText>(R.id.emp_edit_text)
        val btn_yes = view.findViewById<Button>(R.id.yes_btn)

        builder.setView(view)
        val employee_dialog = builder.create()

        btn_yes.setOnClickListener {
            val empcode = emp_edit_text.text.toString()
            if (empcode.isEmpty()) {
                emp_edit_text.error = "Required"
            }
            else if(empcode.equals("00000")
                || empcode.equals("11111")
                || empcode.equals("22222")
                || empcode.equals("33333")
                || empcode.equals("44444")
                || empcode.equals("55555")
                || empcode.equals("77777")
                || empcode.equals("88888")
                || empcode.equals("99999")
                ||empcode.equals("0000")
                || empcode.equals("1111")
                || empcode.equals("2222")
                || empcode.equals("3333")
                || empcode.equals("4444")
                || empcode.equals("5555")
                || empcode.equals("7777")
                || empcode.equals("8888")
                || empcode.equals("9999")

                || empcode.equals("1234")
                || empcode.equals("12345")


                ){
                emp_edit_text.error = "please input proper oll"
            }
            else if (empcode.length == 5 || empcode.length == 4) {
                OmOperation.savePreferences2( Constants.SAVE_OLL, emp_edit_text.text.toString())
                Log.d("OLL", "Saved: ${OmOperation.getPreferences2( Constants.SAVE_OLL, "")}")
                employee_dialog.dismiss()
                validate()
            }
            else   emp_edit_text.error = "please input proper oll"
        }

        employee_dialog.show()


    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}