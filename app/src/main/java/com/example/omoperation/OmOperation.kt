package com.example.omoperation

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.omoperation.room.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OmOperation : Application() {


    override fun onCreate() {
        super.onCreate()
        omOperation= this
        context=this
        sharedPreferences =
            this.applicationContext.getSharedPreferences(
                Constants.PREF_NAME,
                MODE_PRIVATE
            )

    }
    companion object{
        lateinit var context : Context
        lateinit var sharedPreferences: SharedPreferences
        lateinit var omOperation:  OmOperation
        var appDatabase: AppDatabase?=null
        @Synchronized
        fun getInstance(): OmOperation {
            return omOperation
        }


        fun savePreferences( key : String,   value : String) {
            try {
                val editor: SharedPreferences.Editor =
                    sharedPreferences.edit()
                editor.putString(key, value)
                editor.apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun logoutdevice(){
            sharedPreferences.edit().clear().commit()
        }

        @JvmStatic
        fun getPreferences( key : String,value : String): String {
            var value = ""
            try {
                value =
                    sharedPreferences.getString(
                        key,
                        value
                    ).toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return value
        }
    }


}