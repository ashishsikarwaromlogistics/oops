package com.example.omoperation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.model.MIS

class MisAdap(val mydata: ArrayList<MIS>) : RecyclerView.Adapter<MisAdap.MyVIewHolder>() {
    class MyVIewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val tv=itemView.findViewById<TextView>(R.id.tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisAdap.MyVIewHolder {
        return MyVIewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_drawer,parent,false))
    }

    override fun onBindViewHolder(holder: MisAdap.MyVIewHolder, position: Int) {
        val datas=mydata.get(position)
        holder.tv.setText(datas.type+"\n"+datas.number+"\nEMP"+datas.empcode+datas.empname)
    }

    override fun getItemCount(): Int {
       return mydata.size
    }
}