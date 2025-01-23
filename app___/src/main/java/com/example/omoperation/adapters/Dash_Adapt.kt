package com.example.omoperation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import javax.inject.Inject
import javax.inject.Named

class Dash_Adapt @Inject constructor() : RecyclerView.Adapter<Dash_Adapt.MyViewHolder>() {
    val titlename= arrayOf("CN Enquery","AVR","Get Paper(Out)",
        "Challan","Barcode Print","Get Paper In",
        "Challan with Trip",
        "Cn Creation By Eway", "Restore Data","Vehicle Image\nUpload",
       "Pickup Challan Creation","Loading Plan\nTally","CN ReWare House",
        "Stock Audit", "Training","Logout")
    val background= arrayOf(
        R.drawable.pink_circle,R.drawable.blue_circle,R.drawable.green_circle,
        R.drawable.pink_circle,R.drawable.green_circle,R.drawable.blue_circle,
        R.drawable.pink_circle,
        R.drawable.green_circle, R.drawable.green_circle, R.drawable.blue_circle,
        R.drawable.pink_circle,R.drawable.pink_circle,R.drawable.green_circle,
        R.drawable.pink_circle,R.drawable.green_circle,R.drawable.pink_circle)

    val rect= arrayOf(R.drawable.aqua_rect,R.drawable.red_rect,R.drawable.green_rect,
        R.drawable.purple_rect,R.drawable.yellow_rect,R.drawable.blue_rect,
        R.drawable.red_rect,
        R.drawable.pink_rect,R.drawable.grey_rect,R.drawable.orange_rect,
        R.drawable.purple_rect,R.drawable.white_rect,R.drawable.aqua_rect,
        R.drawable.yellow_rect,R.drawable.aqua_rect,R.drawable.aqua_rect
       )

    val textcolor= arrayOf(R.color.color7,R.color.color8,R.color.color9,
        R.color.color10,R.color.color11,R.color.color12,
        R.color.color7,
        R.color.color1, R.color.color2, R.color.color3
        , R.color.color4, R.color.color5, R.color.color7,
        R.color.color10,R.color.color1,R.color.color6
       )
    val titleimage= arrayOf(R.drawable.enquiry1,R.drawable.ic_avr,R.drawable.branch,
        R.drawable.ic_loading,R.drawable.challan,R.drawable.enquiry1,
        R.drawable.ic_loading,
        R.drawable.enquiry1, R.drawable.rewarehouse,R.drawable.vehicletrack,
       R.drawable.vehicletrack,R.drawable.ic_loading,R.drawable.rewarehouse,
        R.drawable.ic_loading,R.drawable.training,R.drawable.logout

       )
    @Inject
    @Named("dashboard")
    lateinit var dashinterface: DashInterface
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val crd=itemView.findViewById<LinearLayout>(R.id.ll1)
      val img=itemView.findViewById<ImageView>(R.id.img)
      val tvtext=itemView.findViewById<TextView>(R.id.tvtext)
      val ll1=itemView.findViewById<LinearLayout>(R.id.ll1)

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
       holder.ll1.setBackgroundResource(rect[position])
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