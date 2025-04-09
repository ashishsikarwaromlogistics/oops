package com.example.omoperation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.model.dataclass.CNWithBoxes

class MissingAdapter(val missinginter:MissingInterface,val scancnbox: ArrayList<CNWithBoxes>) : RecyclerView.Adapter<MissingAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.barcodeNo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_missing, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      holder.titleTextView.setText(scancnbox.get(position).cn+"-->"+scancnbox.get(position).diffbox)
        holder.titleTextView.setOnClickListener { missinginter.missing(scancnbox.get(position)) }
    }

    override fun getItemCount(): Int {
        return scancnbox.size
    }

    interface MissingInterface{
        fun missing(cnWithBoxes: CNWithBoxes)

    }

}