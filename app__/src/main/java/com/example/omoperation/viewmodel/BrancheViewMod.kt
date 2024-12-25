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
import com.example.omoperation.room.AppDatabase
import com.example.omoperation.room.tables.Branches
import com.example.omoperation.model.CommonMod
import kotlinx.coroutines.launch

class BrancheViewMod(application: Application) : AndroidViewModel(application) {

    val _livedata=MutableLiveData<NetworkState>()
    val livedata: LiveData<NetworkState> get() = _livedata
    lateinit var db : AppDatabase
    val context = getApplication<Application>().applicationContext
    init {
        db= AppDatabase.getDatabase(context = context)
        getofflinebranch()
    }
    fun getofflinebranch(){
        _livedata.value=NetworkState.Loading
        val mod= CommonMod()
        mod.status=""
        viewModelScope.launch {
            if(db.branchesdao().getAll().size==0){
                val resp= ApiClient.getClient().create(ServiceInterface::class.java).branch_networkdir(Utils.getheaders(),mod)
                if(resp.code()==200){
                    if(resp.body()!!.error.equals("false")){
                        //_livedata.value=NetworkState.Success(resp.body()!!.emp_enquiry)
                        viewModelScope.launch {
                            val brnchesloist=resp.body()!!.emp_enquiry
                            for(i in brnchesloist){
                                val branches= Branches(
                                    BRANCH_BRANCH_CODE=i.BRANCH_BRANCH_CODE?: "-",
                                    BRANCH_BRANCH_NAME=i.BRANCH_BRANCH_NAME?: "-",
                                    CITY_CITY_NAME=i.CITY_CITY_NAME?: "-",
                                    BRANCH_STATE=i.BRANCH_STATE?: "-",
                                    BRANCH_BRANCH_PHONE=i.BRANCH_BRANCH_PHONE?: "0",
                                    BRANCH_EMAIL=i.BRANCH_EMAIL?: "-",
                                    BRANCH_CONTACT_PERSON=i.BRANCH_CONTACT_PERSON?: "-",
                                    BRANCH_LATI=i.BRANCH_LATI?: "0.0",
                                    BRANCH_LONG=i.BRANCH_LONG?: "0.0"
                                )
                                db.branchesdao().insertAll(branches)
                            }

                            _livedata.value=NetworkState.Success(db.branchesdao().getAll())

                        }
                    }
                    else{}
                }
            }
            else{
                _livedata.value=NetworkState.Success(db.branchesdao().getAll())
            }
        }
         }


    fun getonlinebranch(){
        _livedata.value=NetworkState.Loading
        val mod= CommonMod()
        mod.status=""
        viewModelScope.launch {
            db.branchesdao().deleteall()
                val resp= ApiClient.getClient().create(ServiceInterface::class.java).branch_networkdir(Utils.getheaders(),mod)
                if(resp.code()==200){
                    if(resp.body()!!.error.equals("false")){
                       // _livedata.value=NetworkState.Success(resp.body()!!.emp_enquiry)
                        viewModelScope.launch {
                            val brnchesloist=resp.body()!!.emp_enquiry
                            for(i in brnchesloist){
                                val branches= Branches(
                                    BRANCH_BRANCH_CODE=i.BRANCH_BRANCH_CODE?: "-",
                                    BRANCH_BRANCH_NAME=i.BRANCH_BRANCH_NAME?: "-",
                                    CITY_CITY_NAME=i.CITY_CITY_NAME?: "-",
                                    BRANCH_STATE=i.BRANCH_STATE?: "-",
                                    BRANCH_BRANCH_PHONE=i.BRANCH_BRANCH_PHONE?: "0",
                                    BRANCH_EMAIL=i.BRANCH_EMAIL?: "-",
                                    BRANCH_CONTACT_PERSON=i.BRANCH_CONTACT_PERSON?: "-",
                                    BRANCH_LATI=i.BRANCH_LATI?: "0.0",
                                    BRANCH_LONG=i.BRANCH_LONG?: "0.0"
                                )
                                db.branchesdao().insertAll(branches)
                            }

                            _livedata.value=NetworkState.Success(db.branchesdao().getAll())

                        }
                    }
                    else{}
                }


        }
    }

}