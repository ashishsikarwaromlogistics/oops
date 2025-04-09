package com.example.omoperation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.model.gatepass.Response

class GatePlanAdapter(val response: ArrayList<Response>, val myinterface: GatePassInterface?) : RecyclerView.Adapter<GatePlanAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chk_sr = itemView.findViewById<CheckBox>(R.id.chk_sr)
        var tv_ch = itemView.findViewById<TextView>(R.id.tv_ch)
        var tv_cn = itemView.findViewById<TextView>(R.id.tv_cn)
        var tv_pkg = itemView.findViewById<TextView>(R.id.tv_pkg)
        var tv_act_wt = itemView.findViewById<TextView>(R.id.tv_act_wt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_gate_paln, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
     return  response.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val resp=response.get(position)

      holder.tv_ch.setText(resp.CHLN_NO)
      holder.tv_cn.setText(resp.CN_NO)
      holder.tv_pkg.setText(resp.PKG)
      holder.tv_act_wt.setText(resp.ACT_WT)
        holder.chk_sr.isChecked=resp.isChecked!!

        if(resp.isChecked!!){
            holder.chk_sr.isChecked=true
        }
        else if(!resp.isChecked!!){
            holder.chk_sr.isChecked=false
        }

        holder.chk_sr.setOnClickListener {
            if(!resp.isChecked!!){
                myinterface?.GatePassValue(resp.CN_NO!!)
                holder.chk_sr.isChecked=true
                Log.d("ashish","true")
            }
            else{
                holder.chk_sr.isChecked=true
                Log.d("ashish","false")
            }

        }

    }
    public interface GatePassInterface{
        public fun GatePassValue(cn_num : String)

    }
}