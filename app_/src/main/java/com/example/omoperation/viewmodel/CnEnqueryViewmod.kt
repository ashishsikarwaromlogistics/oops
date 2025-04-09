package com.example.omoperation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.model.cn_enquery.Myquery
import com.example.omoperation.model.dispactch.DispatchResp
import com.example.omoperation.model.freight.FreightResp
import com.example.omoperation.repositories.CnEnqueryRepository

class CnEnqueryViewmod(val context : Context,val repo : CnEnqueryRepository) : ViewModel() {
    val cntext = MutableLiveData("")
    val cndata : LiveData<Myquery>  get()=repo._livedata
    val dispatchdetails : LiveData<DispatchResp>  get()=repo._dispatchdetails
    val freightdetails : LiveData<FreightResp>  get()=repo._freightdetails

    fun checkcn(){
        if(cntext.value.equals("") || cntext.value!!.isEmpty()){
            Utils.showDialog(context,"error","please enter CN Number ", R.drawable.ic_error_outline_red_24dp)
        }
        else{
          repo.getCNDetails(context,cntext.value!!)
        }

    }

    fun getDispatch(){
        if(cntext.value.equals("") || cntext.value!!.isEmpty()){
            Utils.showDialog(context,"error","please enter CN Number ", R.drawable.ic_error_outline_red_24dp)
        }
        else{
            repo.getDispatch(context,cntext.value!!)
        }
    }

    fun getFreight(){
        if(cntext.value.equals("") || cntext.value!!.isEmpty()){
            Utils.showDialog(context,"error","please enter CN Number ", R.drawable.ic_error_outline_red_24dp)
        }
        else{
            repo.getFreight(context,cntext.value!!)
        }
    }
}