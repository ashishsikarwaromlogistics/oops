package com.example.omoperation.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.omoperation.R;
import com.example.omoperation.Utils;

import java.util.ArrayList;

import javax.inject.Inject;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    ArrayList<String> returnValue;
    Context context;
    ImageInterface imageInterface;
    public ImageAdapter(Context context, ArrayList<String> returnValue) {
    this.returnValue=returnValue;
    this.context=context;
        imageInterface= (ImageInterface) context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_image,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
     //  holder.image.setImageBitmap( BitmapFactory.decodeFile(returnValue.get(position)));
        Glide.with(context)
                .load(returnValue.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                      //  holder.image_loading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                      //  holder.image_loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .placeholder(R.drawable.no_image_available)
                .into(holder.image);

        holder.image_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageInterface.sendPosition(position);
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{ Utils.ShowImageDailog(context,returnValue.get(position));}
                catch (Exception e){}

            }
        });
    }

    @Override
    public int getItemCount() {
        return returnValue.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image_cross,image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image_cross=itemView.findViewById(R.id.image_cross);
            image=itemView.findViewById(R.id.image);
        }
    }
    public interface  ImageInterface{
        void sendPosition(int position);
    }
}
