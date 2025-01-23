package com.example.omoperation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omoperation.R;
import com.example.omoperation.model.offline.OfflineChallanListModel;

import java.util.ArrayList;

public class OfflineChallanAdapter extends RecyclerView.Adapter<OfflineChallanAdapter.MyViewHolder> {

    private ArrayList<OfflineChallanListModel> list;
    private Context context;
    private OnItemSelectedListener listener;

    public OfflineChallanAdapter(ArrayList<OfflineChallanListModel> list, Context context, OnItemSelectedListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offline_challan_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OfflineChallanListModel model = list.get(position);
        holder.cnno.setText(model.getCNNO() + " (" + model.getBOXES() + ")");
        //holder.boxes.setText(model.getBOXES());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                listener.onclick(model, false);
            } else {
                listener.onclick(model, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cnno;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cnno = itemView.findViewById(R.id.CNNO);
            //boxes = itemView.findViewById(R.id.boxes);
            checkBox = itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(v -> {
                checkBox.setChecked(!checkBox.isChecked());
                if (checkBox.isChecked()) {
                    listener.onclick(list.get(getAdapterPosition()), false);
                } else {
                    listener.onclick(list.get(getAdapterPosition()), true);
                }
            });
        }
    }

    public interface OnItemSelectedListener {
        void onclick(OfflineChallanListModel model, boolean flag);
    }
}
