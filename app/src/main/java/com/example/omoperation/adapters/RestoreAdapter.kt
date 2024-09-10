package com.example.omoperation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.activities.RestoreActivity
import com.example.omoperation.model.RestoreCN

class RestoreAdapter(val mylist: List<RestoreCN>,val  sendvalue: SendValue) : RecyclerView.Adapter<RestoreAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val cnnum=itemView.findViewById<TextView>(R.id.cnnum)
       val chkcn=itemView.findViewById<CheckBox>(R.id.chkcn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestoreAdapter.MyViewHolder {
       return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_restore,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.cnnum.setText(mylist.get(position).cn+"---"+mylist.get(position).box)
        if(mylist.get(position).restore){
            holder.chkcn.isChecked=true
        }
        else{
            holder.chkcn.isChecked=false
        }
       holder.chkcn.setOnClickListener {
           if(holder.chkcn.isChecked){

               sendvalue.chageststus(position,true)
               Log.d("ashish","checked")
           }
           else{
               sendvalue.chageststus(position, false)
               Log.d("ashish","Un checked")
           }
       }
    }

    override fun getItemCount(): Int {
        return mylist.size
    }
    interface SendValue{
        fun chageststus(poition : Int,restore : Boolean)
    }
}