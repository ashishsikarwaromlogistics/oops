package com.example.omoperation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.omoperation.R

class CustomGrList(
    private val context: Context,
      val addremove: ADDGRREMOVE,
    private val items: List<ArrayList<String>>,
    private val checkedItems: MutableList<Boolean>,

) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_gr, parent, false)

        // Get the views from the layout
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val textView: TextView = view.findViewById(R.id.textView)

        // Set the item name
        textView.text = items.get(position).toString()

        // Set the checkbox state
        checkBox.isChecked = checkedItems[position]

        // Handle checkbox change listener
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkedItems[position] = isChecked
            addremove.addgr(position,isChecked)
            Log.d("ashish",""+isChecked)
        }

        return view
    }
    interface ADDGRREMOVE{
        fun addgr(position : Int,ischecked : Boolean)
    }
}