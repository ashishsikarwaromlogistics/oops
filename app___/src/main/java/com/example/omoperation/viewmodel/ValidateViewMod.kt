package com.example.omoperation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.omoperation.Constants
import com.example.omoperation.NetworkState
import com.example.omoperation.OmOperation
import com.example.omoperation.Utils
import com.example.omoperation.model.login.LoginMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.launch

class ValidateViewMod(application: Application) : AndroidViewModel(application) {
    val context:Context=application.applicationContext
    val _livedata= MutableLiveData<NetworkState>()
    val livedata: LiveData<NetworkState> =_livedata
   fun checkvalidate (){
        _livedata.value=NetworkState.Loading


            if(Utils.haveInternet(context)){

                val mod= LoginMod()
                mod.uuid= Utils.getUUID(context)
                val amdroid_id = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
                mod.Serial=amdroid_id
                mod.password=OmOperation.getPreferences(Constants.EMP_PASS,"")
                mod.android_ver= Build.VERSION.SDK_INT
                mod.user_id = OmOperation.getPreferences(Constants.EMP_CODE,"")

                mod.type = "validate"
                mod.android_ver = Build.VERSION.SDK_INT
                mod.device_name = Build.MODEL
                mod.Serial = Build.USER
                mod.manufacturer = "ITTEST"

                try {
                    mod.app_ver_code = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
                    mod.app_ver_name = context.packageManager.getPackageInfo(context.packageName, 0).versionName
                } catch (e: Exception) {
                }

                viewModelScope.launch {
                    val response= ApiClient.getClient().create(ServiceInterface::class.java).LogIN(mod)
                    if(response.code()==200){
                        if(response.body()!!.error!!.toString().equals("false",false)){
                            _livedata.value=NetworkState.Success(response.body()!!.user)
                            response.body()!!.user!!.JWTTOKEN?.let {
                                OmOperation.savePreferences(
                                    Constants.JWTTOKEN,
                                    it
                                )
                            }
                            OmOperation.savePreferences(Constants.BCODE,response.body()!!.user!!.BCODE.toString())
                            OmOperation.savePreferences(Constants.BNAME,response.body()!!.user!!.BNAME.toString())
                            OmOperation.savePreferences(Constants.ISLOGIN,"1")
                            OmOperation.savePreferences(Constants.EMPNAME,response.body()!!.user!!.Name.toString())
                        }
                        else{
                            _livedata.value=NetworkState.Error("error true",response.message())
                        }

                    }
                    else{
                        _livedata.value=NetworkState.Error(response.code().toString(),response.message())
                    }

                }


                //{"Serial":"5e2a3be4e52dccf8","android_ver":29,"app_ver_code":228,"app_ver_name":"12.0.8","bcode":null,"device_name":"EDA51K","ecode":null,"id":null,"manufacturer":"Honeywell","name":null,"password":"@33740","type":"login","user_id":"33740","uuid":"e8bf44d4-e65c-38ca-911d-c722c6269161"}

        }
       else {
                _livedata.value=NetworkState.Error("error","Internet not connect")
       }
    }
}