package com.example.letseat.Control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.letseat.Boundary.RestaurantsDetailsUI;
import com.example.letseat.Entity.Restaurant;
import com.example.letseat.R;


import java.util.List;

/*
This class shows how each details of restaurant are displayed in list item (with use of viewholder)
 */

public class RestaurantListViewAdapter extends RecyclerView.Adapter<RestaurantListViewAdapter.myViewHolder> {


    private Context mContext;
    private List<Restaurant> mData;
    RequestOptions option;



    public RestaurantListViewAdapter(Context mContext, List<Restaurant> mData){
        this.mContext = mContext;
        this.mData = mData;

        //Glide req option
        option=new RequestOptions().centerCrop().placeholder(R.drawable.restaurant_place).error(R.drawable.restaurant_place);

    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.restaurant_listview,parent, false);
        final myViewHolder viewHolder = new myViewHolder(view);
        viewHolder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, RestaurantsDetailsUI.class);
                i.putExtra("vicinity", mData.get(viewHolder.getAdapterPosition()).getVicinity());
                i.putExtra("restaurant_name", mData.get(viewHolder.getAdapterPosition()).getName());
                i.putExtra("restaurant_rating", mData.get(viewHolder.getAdapterPosition()).getRating());
                i.putExtra("restaurant_image", mData.get(viewHolder.getAdapterPosition()).getImage());
                i.putExtra("place_id", mData.get(viewHolder.getAdapterPosition()).getPlaceID());
                i.putExtra("phone_number", mData.get(viewHolder.getAdapterPosition()).getPhoneNumber());
                i.putExtra("opening_hour", mData.get(viewHolder.getAdapterPosition()).getOpeningHours());
                i.putExtra("latitude", mData.get(viewHolder.getAdapterPosition()).getLatitude());
                i.putExtra("longitude", mData.get(viewHolder.getAdapterPosition()).getLongitude());
                i.putExtra("fireBaseID", mData.get(viewHolder.getAdapterPosition()).getFirebaseID());
                i.putExtra("isFave", mData.get(viewHolder.getAdapterPosition()).getIsFavourite());
                mContext.startActivity(i);
                //((Activity)mContext).finish();
            }
        });
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Glide.with(mContext).load(mData.get(position).getImage()).apply(option).into(holder.img_thumbnail);
        double ratings = mData.get(position).getRating();
        holder.tv_name.setText(mData.get(position).getName());
        holder.tv_rating.setText( ratings+"");
        if (1.0>ratings && ratings > 0.0 ){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.half_star_ ),null,null,null);
        }else if (ratings == 1.0){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.one_star_ ),null,null,null);
        }else if (2.0>ratings && ratings > 1.0 ){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.one_half_star_ ),null,null,null);
        } else if (ratings == 2.0){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.two_star_ ),null,null,null);
        }else if (3.0>ratings && ratings > 2.0 ){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.two_half_star_ ),null,null,null);
        }else if (ratings == 3.0){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.three_star_ ),null,null,null);
        }else if (4.0>ratings && ratings > 3.0 ){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.three_half_star_ ),null,null,null);
        }else if (ratings == 4.0){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.four_star_ ),null,null,null);
        }else if (5.0>ratings && ratings > 4.0 ){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.four_half_star_ ),null,null,null);
        }else if (ratings == 5.0){
            holder.tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(mContext.getResources().getDrawable( R.drawable.five_star_ ),null,null,null);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        TextView tv_rating;
        ImageView img_thumbnail;
        ConstraintLayout view_container;

        public myViewHolder(View itemView){
            super(itemView);
            tv_name = itemView.findViewById(R.id.res_name);
            tv_rating = itemView.findViewById(R.id.rating);
            img_thumbnail = itemView.findViewById(R.id.thumbnail);
            view_container = itemView.findViewById(R.id.container);
        }
    }
}