package com.example.omoperation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.room.tables.Employees
import java.util.Locale


class EmployeeAdapter(val list: List<Employees>, val empinterface: EmployeeInterface) :
    RecyclerView.Adapter<EmployeeAdapter.MyViewHolder>() , Filterable {
    var modelListFiltered: List<Employees>
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate( R.layout.custom_branch,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.setText(modelListFiltered.get(position).EMP_FIRST_NAME )
        holder.city.setText(modelListFiltered.get(position).BRANCH_NAME)
        holder.contact_person.setText(modelListFiltered.get(position).uid.toString())
        holder.phone.setText(modelListFiltered.get(position).EMP_PHONE_NO)
        holder.personal_phone.setOnClickListener {  }
        holder.rel.setOnClickListener { empinterface.sendata(modelListFiltered.get(position)) }
    }

    override fun getItemCount(): Int {
        return modelListFiltered.size
    }
    interface EmployeeInterface{
        fun sendata(employees: Employees)

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
                            it.BRANCH_NAME.lowercase(Locale.getDefault()).contains(charString.lowercase(Locale.getDefault())) ||
                                    it.EMP_FIRST_NAME.lowercase(Locale.getDefault()).contains(charString.lowercase(Locale.getDefault())) ||
                                    it.EMP_EMP_CODE.lowercase(Locale.getDefault()).contains(charString.lowercase(Locale.getDefault()))
                        }
                        filteredList
                    }

                return FilterResults().apply { values = modelListFiltered }
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                modelListFiltered = filterResults?.values as List<Employees>
                notifyDataSetChanged()
            }
        }
    }



}