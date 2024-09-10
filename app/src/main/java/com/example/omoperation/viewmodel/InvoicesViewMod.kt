package com.example.omoperation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omoperation.Constants
import com.example.omoperation.NetworkState
import com.example.omoperation.OmOperation
import com.example.omoperation.Utils
import com.example.omoperation.model.cncreation.CnCreationMod
import com.example.omoperation.model.cncreation.Invoice
import com.google.gson.Gson

class InvoicesViewMod : ViewModel() {
    val invNo=MutableLiveData("")
    val invDt=MutableLiveData("")
    val ptNo=MutableLiveData("")
    val pckgs=MutableLiveData("")
    val qty=MutableLiveData("")
    val pckType=MutableLiveData("")
    val netVal=MutableLiveData("")
    val grossVal=MutableLiveData("")
    val actWt=MutableLiveData("")
    val cftunit=MutableLiveData("")
    val l=MutableLiveData("")
    val w=MutableLiveData("")
    val h=MutableLiveData("")
    val cftr=MutableLiveData("")
    val cftwt=MutableLiveData("")
    val chWt=MutableLiveData("")
    val ewayBillNo=MutableLiveData("")
    val ewayBillDate=MutableLiveData("")
    val desc=MutableLiveData("")
    private val _livedata=MutableLiveData<NetworkState>()
    val livedata: LiveData<NetworkState> get() = _livedata

    fun Addinvices(){
        _livedata.value=NetworkState.Loading
        if(
            invNo.value.equals("")||
            invDt.value.equals("")||
            ptNo.value.equals("")||
            pckgs.value.equals("")||
            qty.value.equals("")||
            pckType.value.equals("")||
            netVal.value.equals("")||
            grossVal.value.equals("")||
            actWt.value.equals("")||
            cftunit.value.equals("")||
            l.value.equals("")||
            w.value.equals("")||
            h.value.equals("")||
            cftr.value.equals("")||
            chWt.value.equals("")||
            ewayBillNo.value.equals("")||
            ewayBillDate.value.equals("")||
            desc.value.equals("")
            ){
            _livedata.value=NetworkState.Error("error","please fill All value")
           return
        }

        var invoices=ArrayList<Invoice>()
       var mod=CnCreationMod()
        mod=Gson().fromJson(OmOperation.getPreferences(Constants.CNCREATION,""),CnCreationMod::class.java)
        if(mod.invoices!=null)
        invoices= mod.invoices as ArrayList<Invoice>
        val inv=Invoice()
        inv.invNo=invNo.value
        inv.invDt=invDt.value
        inv.ptNo=ptNo.value
        inv.pckgs=pckgs.value
        inv.qty=qty.value
        inv.pckType=pckType.value//added this
        inv.netVal=netVal.value
        inv.grossVal=grossVal.value
        inv.actWt=actWt.value
        inv.cftWt=cftwt.value
        inv.l=l.value
        inv.w=w.value
        inv.h=h.value
        inv.cftr=cftr.value
        inv.cftWt=cftwt.value//added this
        inv.chWt=chWt.value
        inv.ewayBillNo=ewayBillNo.value
        inv.ewayBillDate=ewayBillDate.value
        inv.desc=desc.value
        inv.unit=cftunit.value//added this
        invoices.add(inv)
        mod.invoices=invoices
        OmOperation.savePreferences(Constants.CNCREATION,Gson().toJson(mod).toString())
        _livedata.value=NetworkState.Success("Data Enter Successfully")

        Log.d("ashish",OmOperation.getPreferences(Constants.CNCREATION,""))
    }
    fun updatecftunit(value:String){
        cftunit.value=value
    } fun updatepckType(value:String){
        pckType.value=value
    }
}