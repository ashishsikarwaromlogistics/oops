package com.example.omoperation

import android.util.Log
import javax.inject.Inject

class LoggerService @Inject constructor(val myLog: MyLog) {

    fun print(){
      myLog.mylog()
    }
}