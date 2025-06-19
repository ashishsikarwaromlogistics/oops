package com.example.omoperation

sealed class NetworkState {
    object Loading : NetworkState()
    data class Success<T1>(val data: T1) : NetworkState()//we give same name <T1> for generic
    data class Error(val title: String,val message: String) : NetworkState()
}