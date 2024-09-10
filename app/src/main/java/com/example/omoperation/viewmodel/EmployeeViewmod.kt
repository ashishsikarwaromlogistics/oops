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
import com.example.omoperation.room.tables.Employees
import com.example.omoperation.model.CommonMod
import kotlinx.coroutines.launch

class EmployeeViewmod(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>().applicationContext
    val _livedata= MutableLiveData<NetworkState>()
    val livedata: LiveData<NetworkState> get() = _livedata
    lateinit var db : AppDatabase
    init {
        db= AppDatabase.getDatabase(context = context)
        getofflinebranch()
    }
    fun getofflinebranch(){
        _livedata.value=NetworkState.Loading
        val mod= CommonMod()
        mod.status=""
        viewModelScope.launch {
            if(db.employeedao().getAll().size==0){
                val resp= ApiClient.getClient().create(ServiceInterface::class.java).employeedirect(
                    Utils.getheaders())
                if(resp.code()==200){
                    if(resp.body()!!.error.equals("false")){
                        //_livedata.value=NetworkState.Success(resp.body()!!.emp_enquiry)
                        viewModelScope.launch {
                            val brnchesloist=resp.body()!!.emp_enquiry
                            for(i in brnchesloist){
                                val branches= Employees(
                                    BRANCH_NAME=i.BRANCH_NAME?: "-",
                                    EMP_FIRST_NAME=i.EMP_FIRST_NAME?: "-",
                                    EMP_EMP_CODE=i.EMP_EMP_CODE?: "-",
                                    EMP_EMP_TITLE=i.EMP_EMP_TITLE?: "-",
                                    EMP_PHONE_NO=i.EMP_PHONE_NO?: "0",
                                    EMP_EMAIL_ID=i.EMP_EMAIL_ID?: "-",
                                    DEPT_NAME=i.DEPT_NAME?: "-",
                                    DESIG_NAME=i.DESIG_NAME?: ""
                                )
                                db.employeedao().insertAll(branches)
                            }

                            _livedata.value=NetworkState.Success(db.employeedao().getAll())

                        }
                    }
                    else{}
                }
            }
            else{
                _livedata.value=NetworkState.Success(db.employeedao().getAll())
            }
        }
    }


    fun getonlinebranch(){
        _livedata.value=NetworkState.Loading
        val mod= CommonMod()
        mod.status=""
        viewModelScope.launch {
            db.employeedao().deleteall()
            val resp= ApiClient.getClient().create(ServiceInterface::class.java).employeedirect(
                Utils.getheaders())
            if(resp.code()==200){
                if(resp.body()!!.error.equals("false")){
                    //_livedata.value=NetworkState.Success(resp.body()!!.emp_enquiry)
                    viewModelScope.launch {
                        val brnchesloist=resp.body()!!.emp_enquiry
                        for(i in brnchesloist){
                            val branches= Employees(
                                BRANCH_NAME=i.BRANCH_NAME?: "-",
                                EMP_FIRST_NAME=i.EMP_FIRST_NAME?: "-",
                                EMP_EMP_CODE=i.EMP_EMP_CODE?: "-",
                                EMP_EMP_TITLE=i.EMP_EMP_TITLE?: "-",
                                EMP_PHONE_NO=i.EMP_PHONE_NO?: "0",
                                EMP_EMAIL_ID=i.EMP_EMAIL_ID?: "-",
                                DEPT_NAME=i.DEPT_NAME?: "-",
                                DESIG_NAME=i.DESIG_NAME?: ""
                            )
                            db.employeedao().insertAll(branches)
                        }

                        _livedata.value=NetworkState.Success(db.employeedao().getAll())

                    }
                }
                else{}
            }


        }
    }


}