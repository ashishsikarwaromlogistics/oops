package com.example.omoperation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R

class CommonAdapter(val name: Array<String>, val icon: Array<Int>,val communicate : CommonInterface) : RecyclerView.Adapter<CommonAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img=itemView.findViewById<ImageView>(R.id.img)
        val tv=itemView.findViewById<TextView>(R.id.tv)
        val card_pod=itemView.findViewById<CardView>(R.id.card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.common_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return  name.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.tv.setText(name[position])
       holder.img.setImageResource(icon[position]);
        holder.card_pod.setOnClickListener { communicate.sendposition(position) }
    }
    interface CommonInterface{
        public fun sendposition(i :Int)

    }

}