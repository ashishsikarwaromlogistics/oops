package com.example.omoperation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.room.tables.Branches

class BrancheAdapter(val list: List<Branches>,val branchesinterface: BranchInterface) :
    RecyclerView.Adapter<BrancheAdapter.MyViewHolder>() ,Filterable{
    var modelListFiltered: List<Branches>
       init {
           modelListFiltered=list
       }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val city: TextView = itemView.findViewById(R.id.city)
        val contact_person: TextView = itemView.findViewById(R.id.contact_person)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val personal_phone: ImageView = itemView.findViewById(R.id.personal_phone)
        val map: ImageView = itemView.findViewById(R.id.map)
        val rel: RelativeLayout = itemView.findViewById(R.id.rel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrancheAdapter.MyViewHolder {
      return MyViewHolder(LayoutInflater.from(parent.context).inflate( R.layout.custom_branch,parent,false))
    }

    override fun onBindViewHolder(holder: BrancheAdapter.MyViewHolder, position: Int) {
       holder.name.setText(modelListFiltered.get(position).BRANCH_BRANCH_NAME+"--"+modelListFiltered.get(position).BRANCH_BRANCH_CODE)
       holder.city.setText(modelListFiltered.get(position).CITY_CITY_NAME)
       holder.contact_person.setText(modelListFiltered.get(position).BRANCH_CONTACT_PERSON)
       holder.phone.setText(modelListFiltered.get(position).BRANCH_BRANCH_PHONE)
       holder.personal_phone.setOnClickListener {  }
       holder.rel.setOnClickListener { branchesinterface.sendata(modelListFiltered.get(position)) }
    }

    override fun getItemCount(): Int {
        return modelListFiltered.size
    }
    interface BranchInterface{
        fun sendata(branches:Branches)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                modelListFiltered =
                    if (charString.isEmpty()) {
                        list
                } else {
                    val filteredList = list.filter {
                        it.BRANCH_BRANCH_NAME.toLowerCase().contains(charString.toLowerCase()) ||
                                it.CITY_CITY_NAME.toLowerCase().contains(charString.toLowerCase()) ||
                                it.BRANCH_BRANCH_CODE.toLowerCase().contains(charString.toLowerCase())
                    }
                    filteredList
                }

                return FilterResults().apply { values = modelListFiltered }
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                modelListFiltered = filterResults?.values as List<Branches>
                notifyDataSetChanged()
            }
        }
    }



}