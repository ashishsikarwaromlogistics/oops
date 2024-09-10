package com.example.omoperation.network

sealed  class NetworkState {
    object Loading : NetworkState()
    data class Success<T>(val data: T) : NetworkState()
    data class Error(val title: String,val message: String) : NetworkState()
}