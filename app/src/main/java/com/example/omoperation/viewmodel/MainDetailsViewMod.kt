package com.example.omoperation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.model.cncreation.CnCreationMod
import com.example.omoperation.model.cncreation.Invoice
import com.google.gson.Gson

class MainDetailsViewMod : ViewModel() {
    private val _sharedData = MutableLiveData<String>()
     val manualnum=MutableLiveData("")
      val manualdate=MutableLiveData<String>("")
      val frombranchcode=MutableLiveData<String>("")
      val tobranchcode=MutableLiveData<String>("")
      val cnrcode=MutableLiveData<String>("")
      val cnecode=MutableLiveData<String>("")
      val cnr=MutableLiveData<String>("")
      val cne=MutableLiveData<String>("")
      val billingpartycode=MutableLiveData<String>("")
      val billingpartname=MutableLiveData<String>("")
      val remarks=MutableLiveData<String>("")
    val sharedData: LiveData<String> get() = _sharedData

    fun sendData(data: String) {
        manualnum.value = data
    }

    fun collectdata(){
        val mod=CnCreationMod()
        mod.manual_no=manualnum.value
        mod.manual_date=manualdate.value
        mod.from_branch_code=frombranchcode.value
        mod.to_branch_code=tobranchcode.value
        mod.cnrCode=cnrcode.value
        mod.cneeCode=cnecode.value
        mod.cnr=cnr.value
        mod.cnee=cne.value
        mod.tran_mode=tran_mode.value
        mod.freight_mode=freight_mode.value
        mod.bill_part_code=billingpartycode.value
        mod.bill_part_name=billingpartname.value
        mod.load_type=load_type.value
        mod.remarks=remarks.value
        OmOperation.savePreferences(Constants.CNCREATION,Gson().toJson(mod).toString())


    }
    val tran_mode=MutableLiveData<String>("")
    val freight_mode=MutableLiveData<String>("")
    val load_type=MutableLiveData<String>("")
    fun updatetran_mod(value:String){
     tran_mode.value=value
    }
    fun updatefreight_mod(value:String){
        freight_mode.value=value
    }
    fun updateload_type(value:String){ load_type.value=value}
    fun updatefrombranch(value:String){ frombranchcode.value=value}
    fun updatetobranch(value:String){ tobranchcode.value=value}




}