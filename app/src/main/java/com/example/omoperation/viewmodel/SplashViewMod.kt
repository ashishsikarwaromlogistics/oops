package com.example.omoperation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omoperation.NetworkState
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch

class SplashViewMod(application : Application) : AndroidViewModel(application) {
    // Application context if needed
    private val context = getApplication<Application>().applicationContext
   val _liveData=MutableLiveData<NetworkState>()
    val livedata: LiveData<NetworkState> =_liveData

    init {
        viewModelScope.launch {

        }
    }


}