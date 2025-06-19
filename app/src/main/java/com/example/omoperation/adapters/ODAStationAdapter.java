package com.example.omoperation.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.omoperation.R;
import com.example.omoperation.model.oda.EmpEnquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ODAStationAdapter extends RecyclerView.Adapter<ODAStationAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<EmpEnquiry> modelList;
    private List<EmpEnquiry> modelListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone, city, contact_person;
        public ImageView thumbnail, personal_phone, map;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            city = view.findViewById(R.id.city);
            contact_person = view.findViewById(R.id.contact_person);
            phone = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.thumbnail);
            personal_phone = view.findViewById(R.id.personal_phone);
            map = view.findViewById(R.id.map);

            view.setOnClickListener(view1 -> listener.onContactSelected(modelListFiltered.get(getAdapterPosition())));
        }
    }


    public ODAStationAdapter(Context context, List<EmpEnquiry> modelList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.modelList = modelList;
        this.modelListFiltered = modelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_layout_oda_station, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final EmpEnquiry model = modelListFiltered.get(position);
        holder.name.setText(model.getBRANCH_BRANCH_NAME() + "(" + model.getBRANCH_BRANCH_CODE() + ")");
        holder.city.setText(model.getCITY_CITY_NAME());
        holder.contact_person.setText(model.getBRANCH_CONTACT_PERSON());
        holder.phone.setText(model.getBRANCH_BRANCH_PHONE());


    }

    @Override
    public int getItemCount() {
        return modelListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    modelListFiltered = modelList;
                } else {
                    List<EmpEnquiry> filteredList = new ArrayList<>();
                    for (EmpEnquiry row : modelList) {
                        if (row.getBRANCH_BRANCH_NAME().toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault())) || row.getCITY_CITY_NAME().toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault())) || row.getBRANCH_BRANCH_CODE().contains(charString.toLowerCase(Locale.getDefault()))) {
                            filteredList.add(row);
                        }
                    }

                    modelListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = modelListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                modelListFiltered = (ArrayList<EmpEnquiry>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(EmpEnquiry model);
    }
}
