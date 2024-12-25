package com.example.omoperation

import android.app.Dialog
import android.content.Context
import android.widget.TextView


class CustomProgress(con: Context) {
    private var dialog: Dialog = Dialog(con)
    private  lateinit var tv_msg : TextView
    init {

        dialog.setContentView(R.layout.custom_progress)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Use safe call for getWindow
        tv_msg = dialog.findViewById(R.id.tv_msg)
    }
    fun show() {
        if (!dialog.isShowing) { // Show only if not already showing
            dialog.show()
        }
    }

    fun setmsg(msg: String) {
        if (::tv_msg.isInitialized) { // Check if tv_msg is initialized before setting text
            tv_msg.text = msg
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

}