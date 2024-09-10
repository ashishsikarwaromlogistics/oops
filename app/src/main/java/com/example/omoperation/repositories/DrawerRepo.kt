package com.example.omoperation.repositories

import android.content.Context
import android.content.Intent
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.activities.BillSubmissionAct
import com.example.omoperation.activities.BranchesAct
import com.example.omoperation.activities.CNCreation
import com.example.omoperation.activities.CnRewareHouse
import com.example.omoperation.activities.LoadingPlanTally
import com.example.omoperation.activities.MyBrowser
import com.example.omoperation.activities.PodUploadAct
import com.example.omoperation.activities.VehicleLoadUnload
import com.example.omoperation.adapters.DrawerAdapter
import com.example.omoperation.network.ServiceInterface
import javax.inject.Inject

class DrawerRepo @Inject constructor():DrawerAdapter.Drawerinterface {
    override fun calltodrwer(pos: Int, con: Context) {
        if(pos==0){
            con.startActivity(Intent(con, CNCreation::class.java))
        } else  if(pos==1){
            con.startActivity(Intent(con, LoadingPlanTally::class.java))
        }else  if(pos==2){
            con.startActivity(Intent(con, VehicleLoadUnload::class.java))
        }else  if(pos==3){
            con.startActivity(Intent(con, PodUploadAct::class.java))
        }else  if(pos==4){
            con.startActivity(Intent(con, BillSubmissionAct::class.java))
        }else  if(pos==5){
            con.startActivity(Intent(con, CnRewareHouse::class.java))
        }else  if(pos==6){
           // con.startActivity(Intent(con, MyBrowser::class.java).putExtra("url","https://www.the-qrcode-generator.com/"))
            con.startActivity(Intent(con, MyBrowser::class.java).putExtra("title","OTPL CN").putExtra("url",ServiceInterface.omapp+"otpl-cn?bcode="+OmOperation.getPreferences(Constants.BCODE,"")+"&empcode="+OmOperation.getPreferences(Constants.EMP_CODE,"")))
        }//https://omapp.omlogistics.co.in  "/otpl-cn?bcode=" + data[1] + "&empcode=" + data[3]
    }
}