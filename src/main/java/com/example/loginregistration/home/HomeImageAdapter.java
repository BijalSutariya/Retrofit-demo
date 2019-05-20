package com.example.loginregistration.home;

import android.content.Context;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.loginregistration.R;
import com.example.loginregistration.home.HomeInterface.OnImageAddClick;

import java.io.File;
import java.util.List;


public class HomeImageAdapter extends RecyclerView.Adapter<HomeImageAdapter.MyViewHolder> {

    private List<HomeImageModel> photoList;
    private Context context;
    private OnImageAddClick listener;

    /**
     * constructor
     * @param photoList list
     * @param context   this
     * @param listener  OnImageAddClick
     */
    public HomeImageAdapter(List<HomeImageModel> photoList, Context context, OnImageAddClick listener) {
        this.photoList = photoList;
        this.context = context;
        this.listener = listener;
    }

    /**
     * onCreateViewHolder inflate Layout
     *
     * @param parent   ViewGroup
     * @param viewType int
     * @return layout
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_images, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * onBindViewHolder
     * @param holder   hold data
     * @param position position
     */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        HomeImageModel data = photoList.get(position);
        if (TextUtils.isEmpty(data.getImages()) && TextUtils.isEmpty(data.getVideo())){
            holder.ivPhoto.setImageResource(R.drawable.ic_camera);
            holder.ivDelete.setVisibility(View.GONE);
        }
        else if (!TextUtils.isEmpty(data.getImages())) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(new File(data.getImages()))
                    .into(holder.ivPhoto);
        } else if (!TextUtils.isEmpty(data.getVideo())) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(data.getVideo())))
                    .into(holder.ivPhoto);
            holder.ivPhoto.setEnabled(false);

        }
    }

    /**
     * getItemCount count list item
     *
     * @return list size
     */
    @Override
    public int getItemCount() {
        return photoList.size();
    }

    /**
     * MyViewHolder initialize view
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto, ivDelete;

        public MyViewHolder(final View itemView) {
            super(itemView);

            ivPhoto = itemView.findViewById(R.id.ivImageProof);
            ivDelete = itemView.findViewById(R.id.ivDelete);

            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeImageModel data = photoList.get(getAdapterPosition());
                    if (TextUtils.isEmpty(data.getImages())) {
                        listener.onMyClick(v, getAdapterPosition());
                    }
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeImageModel data = photoList.get(getAdapterPosition());
                    if (!TextUtils.isEmpty(data.getImages())) {
                        listener.onMyClick(v, getAdapterPosition());
                    }
                    else if (!TextUtils.isEmpty(data.getVideo())){
                        listener.onMyClick(v, getAdapterPosition());
                        ivPhoto.setEnabled(true);
                    }
                }
            });
        }
    }
}
