package com.example.omoperation.repositories

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.omoperation.OmOperation
import com.example.omoperation.activities.AVR
import com.example.omoperation.activities.BarcodePrint
import com.example.omoperation.activities.BranchesAct
import com.example.omoperation.activities.ChallanCreation
import com.example.omoperation.activities.CnEnquery
import com.example.omoperation.activities.EmployeesAct
import com.example.omoperation.activities.LoginActivity
import com.example.omoperation.activities.PickUpChallan
import com.example.omoperation.activities.RestoreActivity
import com.example.omoperation.activities.VehicleImage
import com.example.omoperation.adapters.Dash_Adapt
import javax.inject.Inject

class DasboardRepo @Inject constructor(): Dash_Adapt.DashInterface {
    /*override fun sendvalue(value: Int,con : Context) {
        if(value==0){

        }
    }*/
    override fun sendvalue(value : Int,con : Context) {
        if(value==0){
            con.startActivity(Intent(con, BranchesAct::class.java))
        }
        else if(value==1){
            con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",2))
        }
        else if(value==2){
            con.startActivity(Intent(con, CnEnquery::class.java))

        }
        else if(value==3){
            con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",1))
        }
        else if(value==4){
            if (ContextCompat.checkSelfPermission(con, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED  && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    con as Activity,
                    arrayOf( android.Manifest.permission.BLUETOOTH_CONNECT),
                    1
                )
            } else {
                con. startActivity(Intent(con, BarcodePrint::class.java))

            }
        }
        else if(value==5){
            con.startActivity(Intent(con, EmployeesAct::class.java))
        }
        else if(value==6){
            con. startActivity(Intent(con, RestoreActivity::class.java))
        }

        else if(value==7){
            con. startActivity(Intent(con, VehicleImage::class.java))
        }
        else if(value==8){
            con. startActivity(Intent(con, PickUpChallan::class.java))
        }
        else if(value==9){

            OmOperation.logoutdevice()
            con.startActivity(Intent(con, LoginActivity::class.java))

        }
    }
}