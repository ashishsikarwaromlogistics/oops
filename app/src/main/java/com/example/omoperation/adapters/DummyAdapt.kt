package com.example.omoperation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R

class DummyAdapt(
    private val names: List<String>,
    private val onSelectionChanged: (List<Int>) -> Unit
) : RecyclerView.Adapter<DummyAdapt.NameViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()

    inner class NameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_gr)
       // val rel: RelativeLayout = itemView.findViewById(R.id.rel)
        init {
            itemView.setOnLongClickListener {
                toggleSelection(adapterPosition)
                true
            }
            itemView.setOnClickListener {
                if (selectedItems.isNotEmpty()) {
                    toggleSelection(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_loading_plan, parent, false)
        return NameViewHolder(view)
    }

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
        holder.textView.text = names[position]

        // Highlight if selected
        if (selectedItems.contains(position)) {
            holder.itemView.setBackgroundColor(Color.BLUE)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount() = names.size

     fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }
        notifyItemChanged(position)
        onSelectionChanged(selectedItems.toList())
    }

    fun getSelectedItems(): List<String> {
        return selectedItems.map { names[it] }
    }

    fun clearSelection() {
        val oldSelections = selectedItems.toList()
        selectedItems.clear()
        oldSelections.forEach { notifyItemChanged(it) }
    }
}