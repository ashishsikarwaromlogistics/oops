package com.example.omoperation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import javax.inject.Inject
import javax.inject.Named


class DrawerAdapter @Inject constructor()
    : RecyclerView.Adapter<DrawerAdapter.MyViewHolder>() {
    val titlename= arrayOf("CN Creation","Loading Plan Tally","Vehicle Load/Unload",
        "POD Upload","Bill Submission","CN ReWare House",
        "OTPL CN Creation","Get Paper IN","Gate Pass(Out)","Empty Challan","Print Docket")

    @Inject
    @Named("drawer")
    lateinit var drawerinterface:Drawerinterface
        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val tv=itemView.findViewById<TextView>(R.id.tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerAdapter.MyViewHolder {
     return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_drawer,parent,false))
    }

    override fun onBindViewHolder(holder: DrawerAdapter.MyViewHolder, position: Int) {
       holder.tv.setText(titlename.get(position))
        holder.tv.setOnClickListener {
            drawerinterface.calltodrwer(position,holder.itemView.context)
        }
    }

    override fun getItemCount(): Int {
       return titlename.size
    }

    interface Drawerinterface{
        fun calltodrwer(pos:Int,con : Context)
    }
}