package com.example.omoperation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.widget.TextView


class CustomProgress(val con: Context) {
    lateinit var dialog : Dialog
    lateinit var tv_msg : TextView
    init{
        dialog=Dialog(con)
    }
      fun show() {
        val alertBox = android.app.AlertDialog.Builder(
            con
        )


          dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
          dialog.setContentView(R.layout.custom_progress)
          tv_msg=dialog.findViewById<TextView>(R.id.tv_msg)
          dialog.show()
    }

      fun setmsg( msg : String) {
          tv_msg.setText(msg)

    }

    fun dismiss(){
        if(dialog.isShowing){
            dialog.dismiss()
        }
    }

}