/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.example.fireeats.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.ItemRestaurantBinding;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.util.RestaurantUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class RestaurantAdapter extends FirestoreAdapter<RestaurantAdapter.ViewHolder> {

    private OnRestaurantSelectedListener mListener;

    public RestaurantAdapter(Query query, OnRestaurantSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public interface OnRestaurantSelectedListener {

        void onRestaurantSelected(DocumentSnapshot restaurant);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemRestaurantBinding mItemBinding;

        public ViewHolder(View itemView) {
            super(itemView);

            // Bind itemView with ViewBinding
            mItemBinding = ItemRestaurantBinding.bind(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnRestaurantSelectedListener listener) {

            Restaurant restaurant = Objects.requireNonNull(snapshot.toObject(Restaurant.class));
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(mItemBinding.restaurantItemImage.getContext())
                    .load(restaurant.getPhoto())
                    .into(mItemBinding.restaurantItemImage);

            mItemBinding.restaurantItemName.setText(restaurant.getName());
            mItemBinding.restaurantItemRating.setRating((float) restaurant.getAvgRating());
            mItemBinding.restaurantItemCity.setText(restaurant.getCity());
            mItemBinding.restaurantItemCategory.setText(restaurant.getCategory());
            mItemBinding.restaurantItemNumRatings.setText(resources.getString(R.string.fmt_num_ratings,
                    restaurant.getNumRatings()));
            mItemBinding.restaurantItemPrice.setText(RestaurantUtil.getPriceString(restaurant));

            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onRestaurantSelected(snapshot);
                }
            });
        }

    }
}
