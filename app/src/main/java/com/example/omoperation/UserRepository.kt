package com.example.omoperation

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.omoperation.activities.Splash
import javax.inject.Inject

interface UserRepository {
    fun saveUser()

}

class SqLRepo @Inject constructor() : UserRepository{
    override fun saveUser() {
     //  con.startActivity(Intent(con,Splash::class.java))
    }

}
class FirebaseRepo @Inject constructor(): UserRepository{
    override fun saveUser() {
       Log.d("ashish","save firebase")
    }
}