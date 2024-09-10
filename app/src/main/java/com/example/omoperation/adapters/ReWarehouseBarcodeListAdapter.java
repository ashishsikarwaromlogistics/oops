package com.example.omoperation.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omoperation.R;

import java.util.ArrayList;

public class ReWarehouseBarcodeListAdapter extends RecyclerView.Adapter<ReWarehouseBarcodeListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<String> list;
    private ArrayList<String> filtered;
    private Context context;
    private OnDeleteBarcodeListener deleteBarcodeListener;

    public ReWarehouseBarcodeListAdapter(ArrayList<String> list, Context context, OnDeleteBarcodeListener deleteBarcodeListener) {
        this.list = list;
        this.filtered = list;
        this.context = context;
        this.deleteBarcodeListener = deleteBarcodeListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.barcode_list_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final String barcode = list.get(position);
        holder.textView.setText(barcode);
        holder.removeBtn.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete this " + Html.fromHtml("<b>" + list.get(position) + "</b>") + " barcode?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> deleteBarcodeListener.onDelete(barcode, list.get(0).substring(0, list.get(0).length() - 4)))
                .setNegativeButton("No", null)
                .show());
    }

    @Override
    public int getItemCount() {
        // return list.size();
        if (list != null) {
            return list.size();
        } else {
            return 0; // Or handle the null case according to your requirements
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    list = filtered;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String row : filtered) {
                        if (row.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    list = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton removeBtn;

        public MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.barcodeNo);
            removeBtn = view.findViewById(R.id.removeBtn);
        }
    }

    public interface OnDeleteBarcodeListener {
        void onDelete(String barcode, String lastGR);
    }
}
