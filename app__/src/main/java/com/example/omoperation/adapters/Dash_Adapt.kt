package com.example.omoperation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.activities.DashboardAct
import javax.inject.Inject
import javax.inject.Named

class Dash_Adapt @Inject constructor() : RecyclerView.Adapter<Dash_Adapt.MyViewHolder>() {
    val titlename= arrayOf("Branch","AVR","CN Enquery",
        "Challan","Barcode Print","Employee List",
        "Restore Data","Vehicle Image\nUpload","Pickup Challan Creation","Logout")
    val background= arrayOf(R.drawable.pink_circle,R.drawable.blue_circle,R.drawable.green_circle,
        R.drawable.pink_circle,R.drawable.green_circle,R.drawable.blue_circle,
        R.drawable.green_circle, R.drawable.blue_circle, R.drawable.pink_circle,
        R.drawable.pink_circle)
    val textcolor= arrayOf(R.color.text_pink,R.color.colorPrimary,R.color.text_green,
        R.color.colorPrimary,R.color.text_aqua,R.color.text_yellow,
        R.color.text_green, R.color.text_green,R.color.text_pink
        ,R.color.text_yellow)
    val titleimage= arrayOf(R.drawable.branch,R.drawable.ic_avr,R.drawable.enquiry1,
        R.drawable.ic_loading,R.drawable.challan,R.drawable.profile_ic,
        R.drawable.rewarehouse,R.drawable.vehicletrack,R.drawable.vehicletrack,
        R.drawable.logout)
    @Inject
    @Named("dashboard")
    lateinit var dashinterface: DashInterface
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val crd=itemView.findViewById<CardView>(R.id.crd)
      val img=itemView.findViewById<ImageView>(R.id.img)
      val tvtext=itemView.findViewById<TextView>(R.id.tvtext)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_dash,parent,false))
    }

    override fun getItemCount(): Int {
      return titlename.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.img.setImageResource(titleimage[position])
       holder.img.setBackgroundResource(background[position])
      // holder.tvtext.setTextColor(textcolor[position])
        holder.tvtext.setTextColor(ContextCompat.getColor(holder.itemView.context, textcolor[position]))

        holder.tvtext.setText(titlename[position])
        holder.crd.setOnClickListener {
            dashinterface.sendvalue(position,holder.itemView.context)
        }
    }




    interface DashInterface {
        fun sendvalue(value : Int,con : Context)

    }

}