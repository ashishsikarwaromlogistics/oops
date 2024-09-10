package com.example.omoperation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.NetworkState
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.CommonRespS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AvrViewMod  @Inject constructor(application: Application) : AndroidViewModel(application) {
    //val gatenumber = MutableLiveData<String>("13411620000054")
    val gatenumber = MutableLiveData<String>("")
    val date = MutableLiveData<String>("")
    val vehiclenum = MutableLiveData<String>("")
    val _data = MutableLiveData<NetworkState>()
    val data : LiveData<NetworkState> get() = _data
    val myBarcodes= MutableLiveData<String>("")

    fun finddata(){
        if(gatenumber.value.equals("")){
            _data.value=NetworkState.Error("error ","Please enter gate entry number")

            return;
        }
        val mod = CommonMod()
        mod.status = "getDetails"
        mod.gate_no = gatenumber.value
        val map = HashMap<String, String>()
        map["Accept"] = "application/json"
        map[Constants.JWTTOKEN] = OmOperation.getPreferences(Constants.JWTTOKEN, "")
        map["EMP_CODE"] = OmOperation.getPreferences(Constants.EMP_CODE, "")
        _data.value= NetworkState.Loading
       viewModelScope.launch(Dispatchers.IO) {
           ApiClient.getClient().create(ServiceInterface::class.java).
           challanUnloading(map,mod).enqueue(object : Callback<CommonRespS>{
               override fun onResponse(call: Call<CommonRespS>, response: Response<CommonRespS>) {
                   if(response.body()!!.error.equals("false")){
                       date.value=response.body()!!.date
                       vehiclenum.value=response.body()!!.lorry_no
                       _data.value=NetworkState.Success(response.body())
                   }
                   else{
                       _data.value=NetworkState.Error("error true","not found")
                   }
               }

               override fun onFailure(call: Call<CommonRespS>, t: Throwable) {
                   //   Utils.showDialog(getApplication(),"error true",response.body()!!.response?:"",R.drawable.ic_error_outline_red_24dp)
                   _data.value=NetworkState.Error("onFailure",t.message.toString())
               }

           })
       }
    }

    fun checkdata(barcode : String){
        var  barCode=barcode
        if (barCode.startsWith("O")) {
            barCode = barCode.substring(1, barCode.length)
            barCode = Utils.revertTransform(barCode)

        }
        if (barCode.contains( getApplication<Application>().getString(R.string.NBC_Sticker_Identification))) {
            val CustomerBarcode =
                barCode.split( getApplication<Application>().getString(R.string.NBC_Sticker_Identification).toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            barCode =
                getApplication<Application>().getString(R.string.NBC_Prefix) + CustomerBarcode[1] + CustomerBarcode[4]
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
        if(!barCode.equals(""))
        myBarcodes.value=barCode
    }


}