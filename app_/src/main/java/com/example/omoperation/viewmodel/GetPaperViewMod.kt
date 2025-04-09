package com.example.omoperation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.model.gatepassin.GatePassInMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.NetworkState
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.launch
import java.util.ArrayList

class GetPaperViewMod(application: Application) : AndroidViewModel(application) {
   // val context = getApplication<Application>().applicationContext
    val context: Context = application.applicationContext
    val gatenumber= MutableLiveData("")
    val responsedata: LiveData<NetworkState> get() = _responsedata
    val _responsedata=MutableLiveData<NetworkState>()

        fun finddata() {
            if(gatenumber.value.equals("")||gatenumber.value!!.isEmpty()||gatenumber.value!!.isBlank()){
                _responsedata.postValue(NetworkState.Error("error","please enter Gate Entry Number"))

                return
            }
        _responsedata.value=NetworkState.Loading
        val mod= GatePassInMod()
        mod.bcode= OmOperation.getPreferences(Constants.BCODE,"")
        mod.status="searchGetPaper"
        mod.gatePassNo=gatenumber.value
       viewModelScope.launch {
          // val response= ApiClient.getClientsanchar().create(ServiceInterface::class.java).searchGetPaper(
           val response= ApiClient.getClientomsl().create(ServiceInterface::class.java).searchGetPaper(
               Utils.getheaders(),mod)
           if(response.code()==200){
               if(response.body()?.error.equals("false")){
                   _responsedata.postValue(NetworkState.Success(response.body()!!.response))
               }
               else{
                   _responsedata.postValue(NetworkState.Error("error true",response.body()!!.message))
               }
           }
           else {
               _responsedata.postValue(NetworkState.Error("error code"+response.code(),response.message()))

           }
       }


    }

}