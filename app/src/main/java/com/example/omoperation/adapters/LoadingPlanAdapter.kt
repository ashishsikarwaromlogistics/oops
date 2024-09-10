package com.example.omoperation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.activities.LoadingPlan
import com.example.omoperation.model.loading.LoadingNo

class LoadingPlanAdapter(val interfaces: Loadinginterface,val loadingPlan: Context,val loadingNo: List<LoadingNo>) : RecyclerView.Adapter<LoadingPlanAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val  loadingplan=itemView.findViewById<TextView>(R.id.loadingplan)
       val  branch=itemView.findViewById<TextView>(R.id.branch)
       val  ll=itemView.findViewById<LinearLayout>(R.id.ll)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoadingPlanAdapter.MyViewHolder {
      return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_loading,parent,false))
    }

    override fun onBindViewHolder(holder: LoadingPlanAdapter.MyViewHolder, position: Int) {
       holder.loadingplan.setText(loadingNo.get(position).LOADING_NO)
       holder.branch.setText(loadingNo.get(position).TO_BRANCH)
        holder.ll.setOnClickListener {
            interfaces.senddata(loadingNo.get(position))
        }
    }

    override fun getItemCount(): Int {
        return loadingNo.size
    }

    interface Loadinginterface{
        fun senddata(a  : LoadingNo)
    }

}