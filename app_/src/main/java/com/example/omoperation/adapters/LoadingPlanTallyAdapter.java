package com.example.omoperation.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omoperation.R;
import com.example.omoperation.model.tally.Detail;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoadingPlanTallyAdapter extends RecyclerView.Adapter<LoadingPlanTallyAdapter.MyViewHolder> {
    List<Detail> detail;
    LoadingPlanInterface loadingPlanInterface;
    public LoadingPlanTallyAdapter(@NotNull List<Detail> detail, LoadingPlanInterface loadingPlanInterface) {
        this.detail=detail;
        this.loadingPlanInterface=loadingPlanInterface;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_loading_plan, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
     holder.che_gr.setChecked(detail.get(position).getCheckvarue());
     holder.tv_gr.setText(detail.get(position).getM_CN_NO());
     holder.tv_wt.setText(detail.get(position).getWT());
     holder.tv_ch_wt.setText(detail.get(position).getCHRGWT());
     holder.tv_pkt.setText(detail.get(position).getPKG());
     holder.tv_remarks.setText(detail.get(position).getRemarks());
     holder.tv_destcode.setText(detail.get(position).getDEST_CODE());
     holder.tv_cn_age.setText(detail.get(position).getCN_AGE());
     holder.linearLayout.setBackgroundColor(Color.parseColor(detail.get(position).getCOLOR_BY()));
        holder.tv_remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.che_gr.isChecked()){}
                else{loadingPlanInterface.sendRemarks(position);}
            }
        });
        holder.che_gr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(holder.che_gr.isChecked()){
                  holder.che_gr.setChecked(false);
                  Log.d("ashishs","Checked ")  ;
              }
              else {
                  loadingPlanInterface.sendRemarks(position,false);
                  Log.d("ashishs","UnChecked ")  ;
              }
            }
        });
    }

    @Override
    public int getItemCount() {
        return detail.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox che_gr;
        TextView tv_gr,tv_wt,tv_ch_wt,tv_pkt,tv_remarks,tv_destcode,tv_cn_age;
        LinearLayout linearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            che_gr=itemView.findViewById(R.id.che_gr);
            tv_gr=itemView.findViewById(R.id.tv_gr);
            tv_wt=itemView.findViewById(R.id.tv_wt);
            tv_ch_wt=itemView.findViewById(R.id.tv_ch_wt);
            tv_pkt=itemView.findViewById(R.id.tv_pkt);
            tv_remarks=itemView.findViewById(R.id.tv_remarks);
            tv_destcode=itemView.findViewById(R.id.tv_destcode);
            linearLayout=itemView.findViewById(R.id.linearLayout);
            tv_cn_age=itemView.findViewById(R.id.tv_cn_age);
        }
    }
    public interface LoadingPlanInterface{

        public void sendRemarks(int position);
        public void sendRemarks(int position,boolean task);
    }
}
