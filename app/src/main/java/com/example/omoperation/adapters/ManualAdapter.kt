package com.example.omoperation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.databinding.BarcodeListRowBinding
import com.example.omoperation.databinding.LayoutAvrManualGrBinding
import com.example.omoperation.room.tables.ManualAvr

class ManualAdapter(val barcodelist:
                    List<ManualAvr>,val interfaces : ManualInterface) : RecyclerView.Adapter<ManualAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: LayoutAvrManualGrBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding=itemView;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_avr_manual_gr,parent,false))

    }

    override fun getItemCount(): Int {
        return barcodelist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.cnNo.setText(barcodelist.get(position).cn)
        holder.binding.boxes.setText(barcodelist.get(position).boxes)
        holder.binding.delete.setOnClickListener { interfaces.deleteCN(barcodelist.get(position).cn.toString()) }
    }

    interface ManualInterface{
      fun deleteCN(cn : String)
    }

}