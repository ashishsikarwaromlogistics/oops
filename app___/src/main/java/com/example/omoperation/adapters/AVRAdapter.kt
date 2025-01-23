package com.example.omoperation.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.activities.CheckMissing
import com.example.omoperation.databinding.BarcodeListRowBinding

class AVRAdapter(var context: Context,val removeBarcode:  RemoveBarcode,val barcodelist: ArrayList<String>) : RecyclerView.Adapter<AVRAdapter.MyViewHolder>() {
   var isfilter=false
    lateinit var filtercode:List<String>
    class MyViewHolder(itemView: BarcodeListRowBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding=itemView;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AVRAdapter.MyViewHolder {
        return MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.barcode_list_row,parent,false))
    }

    fun setfilter(isfilter: Boolean, filtercode: List<String>?){
      this.isfilter=isfilter
        try{
            this.filtercode=filtercode!!
        }catch (e:Exception){}

         notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AVRAdapter.MyViewHolder, position: Int) {
      if(isfilter){
          holder.binding.barcodeNo.setText(filtercode.get(position))
          holder.binding.removeBtn.setOnClickListener {
              removeBarcode.removebarcode(filtercode.get(position))
          }
          holder.binding.barcodeNo.setOnClickListener {
              context.startActivity(Intent(context,CheckMissing::class.java).
              putExtra("cn",filtercode.get(position).substring(0,filtercode.get(position).length-4),))
          }
      }
        else{
          holder.binding.barcodeNo.setText(barcodelist.get(position))
          holder.binding.removeBtn.setOnClickListener {
              removeBarcode.removebarcode(barcodelist.get(position))
          }
          holder.binding.barcodeNo.setOnClickListener {
              context.startActivity(Intent(context,CheckMissing::class.java).
              putExtra("cn",barcodelist.get(position).substring(0,barcodelist.get(position).length-4),))
          }
        }

    }

    override fun getItemCount(): Int {
       return if(isfilter) filtercode.size else  barcodelist.size
    }

    interface RemoveBarcode{
        fun removebarcode(barcode : String)
    }
}