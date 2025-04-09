package com.example.omoperation.repositories

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.activities.BarcodeModule
import com.example.omoperation.activities.CnEnquery
import com.example.omoperation.activities.CnRewareHouse
import com.example.omoperation.activities.GetPaper
import com.example.omoperation.activities.LoadingPlanByGate
import com.example.omoperation.activities.LoadingPlanTally
import com.example.omoperation.activities.LoginActivity
import com.example.omoperation.activities.PickUpChallan
import com.example.omoperation.activities.RestoreActivity
import com.example.omoperation.activities.StockAudit
import com.example.omoperation.activities.TripChallan
import com.example.omoperation.activities.VehicleImage
import com.example.omoperation.activities.VehicleLoadUnload
import com.example.omoperation.activities.VideoList
import com.example.omoperation.adapters.Dash_Adapt
import com.example.omoperation.room.AppDatabase
import javax.inject.Inject

class DasboardRepo @Inject constructor(): Dash_Adapt.DashInterface {
    /*override fun sendvalue(value: Int,con : Context) {
        if(value==0){

        }
    }*/
    lateinit var con : Context
    lateinit var db : AppDatabase
    override fun sendvalue(value : Int,con : Context) {
        this.con=con
        db=AppDatabase.getDatabase(con)
        if(value==0){
            con.startActivity(Intent(con, CnEnquery::class.java))
        }
        else if(value==1){
            openAVR()
        }
        else if(value==2){
            con.startActivity(Intent(con, LoadingPlanByGate::class.java))

        }
        else if(value==3){
            openchallanCreation()
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
                con. startActivity(Intent(con, BarcodeModule::class.java))

            }
        }
        else if(value==5){
            con.startActivity(Intent(con, GetPaper::class.java))
        }
        else if(value==6){
            con. startActivity(Intent(con, TripChallan::class.java))
        }
        else if(value==7){
            Utils.showDialog(con,"alert","please use om staff app", R.drawable.ic_error_outline_red_24dp)
           // con. startActivity(Intent(con, CnCreationByEway::class.java))
        }
        else if(value==8){
            con. startActivity(Intent(con, RestoreActivity::class.java))
        }

        else if(value==9){
            con. startActivity(Intent(con, VehicleImage::class.java))
        }
        else if(value==10){
            con. startActivity(Intent(con, PickUpChallan::class.java))
        }
        else if(value==11){


            con.startActivity(Intent(con, LoadingPlanTally::class.java))


        }
        else if(value==12){
            con.startActivity(Intent(con, CnRewareHouse::class.java))

        } else if(value==13){
            con.startActivity(Intent(con, StockAudit::class.java))

        }
        else if(value==14){
            openAVRWithtGate()
           // OmOperation.logoutdevice()


        }
        else if(value==15){
            con.startActivity(Intent(con, VehicleLoadUnload::class.java).putExtra("loadtype",2))



        }
        else if(value==16){
            con.startActivity(Intent(con, VideoList::class.java))



        }
        else if(value==17){
         //   con.startActivity(Intent(con, TestAct::class.java))
            exitApp()
        }
    }
      fun exitApp( ) {
        val alertBox = android.app.AlertDialog.Builder(con)
        alertBox.setMessage("Do you really want exit ?").setCancelable(false)
            .setPositiveButton(
                "YES"
            ) { dialog, which ->
                OmOperation.logoutdevice()
                con.startActivity(Intent(con, LoginActivity::class.java))
                (con as Activity).finish()

               // val activityManager = con.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
              //  activityManager.restartPackage(con.packageName)

            }.setNegativeButton("NO", null)

        val alert = alertBox.create()
        alert.show()


    }

    fun openchallanCreation( ) {
        val alertBox = android.app.AlertDialog.Builder(con)
        alertBox.setMessage("Do you want to use BackUp ?\n" +
                "क्या आप बैकअप का उपयोग करना चाहते हैं?").setCancelable(false)
            .setPositiveButton(
                "YES/हाँ"
            ) { dialog, which ->

                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",1)
                    .putExtra("isdelete","YES"))

                // val activityManager = con.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                //  activityManager.restartPackage(con.packageName)

            }.setNegativeButton("NO/नहीं") { dialog, _ ->

                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",1)
                    .putExtra("isdelete","NO"))

                dialog.dismiss() // Close the dialog
        }

            .setNeutralButton("Not Now/अभी नहीं"){  dialog, _ ->
                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",1)
                    .putExtra("isdelete","NOT NOW"))
                dialog.dismiss()
            }

        val alert = alertBox.create()
        alert.show()


    }
    fun openAVR() {
        val alertBox = android.app.AlertDialog.Builder(con)
        alertBox.setMessage("Do you want to use BackUp ?\n" +
                "क्या आप बैकअप का उपयोग करना चाहते हैं?").setCancelable(false)
            .setPositiveButton(
                "YES/हाँ"
            ) { dialog, which ->

                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",2)
                    .putExtra("isdelete","YES"))
             //   con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",2))
                // val activityManager = con.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                //  activityManager.restartPackage(con.packageName)

            }.setNegativeButton("NO/नहीं") { dialog, _ ->

                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",2)
                    .putExtra("isdelete","NO"))

                dialog.dismiss() // Close the dialog
            }
            .setNeutralButton("Not Now/अभी नहीं"){  dialog, _ ->
                     con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",2)
                    .putExtra("isdelete","NOT NOW"))
                dialog.dismiss()
            }

        val alert = alertBox.create()
        alert.show()


    }
    fun openAVRWithtGate() {
        val alertBox = android.app.AlertDialog.Builder(con)
        alertBox.setMessage("Do you want to use BackUp ?\n" +
                "क्या आप बैकअप का उपयोग करना चाहते हैं?").setCancelable(false)
            .setPositiveButton(
                "YES/हाँ"
            ) { dialog, which ->

                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",4)
                    .putExtra("isdelete","YES"))
                //   con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",2))
                // val activityManager = con.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                //  activityManager.restartPackage(con.packageName)

            }.setNegativeButton("NO/नहीं") { dialog, _ ->

                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",4)
                    .putExtra("isdelete","NO"))

                dialog.dismiss() // Close the dialog
            }
            .setNeutralButton("Not Now/अभी नहीं"){  dialog, _ ->
                con.startActivity(Intent(con, RestoreActivity::class.java).putExtra("value",4)
                    .putExtra("isdelete","NOT NOW"))
                dialog.dismiss()
            }

        val alert = alertBox.create()
        alert.show()


    }
}