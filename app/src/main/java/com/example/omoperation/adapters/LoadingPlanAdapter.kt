package com.example.omoperation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.model.loading.LoadingNo
import com.example.omoperation.model.oda.EmpEnquiry
import java.util.Locale

class LoadingPlanAdapter(val interfaces: Loadinginterface,val loadingPlan: Context,val loadingNo: List<LoadingNo>,
    )
    : RecyclerView.Adapter<LoadingPlanAdapter.MyViewHolder>() ,   Filterable {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val  loadingplan=itemView.findViewById<TextView>(R.id.loadingplan)
       val  branch=itemView.findViewById<TextView>(R.id.branch)
       val  loadingdate=itemView.findViewById<TextView>(R.id.loadingdate)
       val  enterdate=itemView.findViewById<TextView>(R.id.enterdate)
       val  ll=itemView.findViewById<LinearLayout>(R.id.ll)
    }
   // var modelListFiltered: MutableList<LoadingNo> = mutableListOf()
    var modelListFiltered=loadingNo.toMutableList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoadingPlanAdapter.MyViewHolder {

      return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_loading,parent,false))
    }

    override fun onBindViewHolder(holder: LoadingPlanAdapter.MyViewHolder, position: Int) {
       holder.loadingplan.setText("LOADING_NO : "+modelListFiltered.get(position).LOADING_NO)
       holder.branch.setText("TO_BRANCH : "+modelListFiltered.get(position).TO_BRANCH)
       holder.enterdate.setText("LOADING_DATE : "+modelListFiltered.get(position).LOADING_DATE)
       holder.loadingdate.setText("ENTER_DATE: "+modelListFiltered.get(position).ENTER_DATE)
        holder.ll.setOnClickListener {
            interfaces.senddata(modelListFiltered.get(position))
        }
    }

    override fun getItemCount(): Int {
        return modelListFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    modelListFiltered = loadingNo as ArrayList<LoadingNo>
                } else {
                    val filteredList: MutableList<LoadingNo> = ArrayList<LoadingNo>()
                    for (row in loadingNo) {
                        if (row.LOADING_NO.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault())) || row.TO_BRANCH.lowercase(
                                Locale.getDefault()
                            )
                                .contains(charString.lowercase(Locale.getDefault())
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    modelListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = modelListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                modelListFiltered = filterResults.values as ArrayList<LoadingNo>
                notifyDataSetChanged()
            }
        }
    }

    interface Loadinginterface{
        fun senddata(a  : LoadingNo)
    }

}