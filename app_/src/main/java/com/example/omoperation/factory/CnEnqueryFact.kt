package com.example.omoperation.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.omoperation.repositories.CnEnqueryRepository
import com.example.omoperation.viewmodel.CnEnqueryViewmod


class CnEnqueryFact(val context:Context,val repo : CnEnqueryRepository):ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CnEnqueryViewmod(context,repo) as T
    }
    
}

/*
class CnEnqueryFact(val context : Context,val repo : CnEnqueryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CnEnqueryViewmod(context,repo) as T
    }
}*/
