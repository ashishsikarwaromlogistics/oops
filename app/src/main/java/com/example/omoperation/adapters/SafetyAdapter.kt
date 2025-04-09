package com.example.omoperation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R

class SafetyAdapter(val totallist: ArrayList<String>) :RecyclerView.Adapter<SafetyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val barcodeNo=itemView.findViewById<TextView>(R.id.barcodeNo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SafetyAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.barcode_list_row,parent,false))
    }

    override fun onBindViewHolder(holder: SafetyAdapter.MyViewHolder, position: Int) {
        holder.barcodeNo.setText(totallist.get(position))
    }
    fun addbarcode(value: String){
        totallist.reverse()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return  totallist.size
    }
}