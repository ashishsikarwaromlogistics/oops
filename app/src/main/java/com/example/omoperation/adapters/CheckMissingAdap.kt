package com.example.omoperation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R


class CheckMissingAdap(val barcodes: List<String>) : RecyclerView.Adapter<CheckMissingAdap.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.barcodeNo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckMissingAdap.MyViewHolder {
       return MyViewHolder(LayoutInflater.from(parent.context).inflate( R.layout.custom_missing,parent,false))
    }

    override fun onBindViewHolder(holder: CheckMissingAdap.MyViewHolder, position: Int) {
        holder.titleTextView.setText(barcodes.get(position))
    }

    override fun getItemCount(): Int {
       return barcodes.size
    }


}