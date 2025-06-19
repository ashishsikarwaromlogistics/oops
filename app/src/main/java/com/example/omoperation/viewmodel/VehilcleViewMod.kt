package com.example.omoperation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.omoperation.NetworkState
import com.example.omoperation.Utils
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.model.CommonMod
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class VehilcleViewMod @Inject constructor(application: Application) : AndroidViewModel(application) {
    val vehicleno= MutableLiveData<String>("")
    val _livedata=MutableLiveData<NetworkState>()
    val livedata: LiveData<NetworkState> =_livedata
   /* fun checklorry(){
        if(vehicleno.value!!.isEmpty() || vehicleno.value.equals("")){
            _livedata.value=NetworkState.Error("error","please enter vehicle no")

        }
        else{
          //  _livedata.value=NetworkState.Error("success","please enter vehicle no")

            _livedata.value=NetworkState.Loading
        }

    }*/

     fun checklorry() {
         if(vehicleno.value!!.isEmpty() || vehicleno.value.equals("")){
             _livedata.value=NetworkState.Error("error","please enter vehicle no")
             return
         }
        val mod= CommonMod()
        mod.lorryno=vehicleno.value!!.uppercase(Locale.getDefault())
        mod.type="Challan"
        viewModelScope.launch {
            _livedata.value=NetworkState.Loading
            val resp= ApiClient.getClient().create(ServiceInterface::class.java).vehicle_validate_checklist(
                Utils.getheaders(),mod)
            if(resp.code()==200){

                if(resp.body()!!.error.toString().equals("false"))
                {
                    _livedata.postValue(NetworkState.Success(resp.body()!!.error.toString()))


                }
                else{
                    _livedata.postValue(NetworkState.Error("error","Please enter valid lorry number"))
                }
            }
            else{
              _livedata.postValue(NetworkState.Error("error","Please enter valid lorry number"))
            }
        }

    }

}