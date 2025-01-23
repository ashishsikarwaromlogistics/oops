package com.example.omoperation.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivitySplashBinding
import com.example.omoperation.model.CommonMod
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)
        window.statusBarColor = Color.BLACK
        //OmOperation.savePreferences(Constants.EMP_PASS,"1234567")
        cp= CustomProgress(this)
        viewmod=ViewModelProvider(this).get(ValidateViewMod::class.java)
        binding.viewmode=viewmod
        binding.lifecycleOwner=this


      //  mediaPlayer = MediaPlayer.create(this, R.raw.a) // Add your audio file here
        /*if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }*/
       //val animation=Animation.
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
        if(Utils.haveInternet(this)){
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



                //val response=


                if(response?.code()==200){
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
        }
    }
    fun validate(){
        if(OmOperation.getPreferences(Constants.ISLOGIN,"0").equals("1")){

            viewmod.checkvalidate()
            viewmod.livedata.observe(this, Observer {
                when (it) {
                    is com.example.omoperation.NetworkState.Error ->{
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

                    com.example.omoperation.NetworkState.Loading -> {
                        cp.show()
                    }
                    is com.example.omoperation.NetworkState.Success<*> -> {
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



    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}