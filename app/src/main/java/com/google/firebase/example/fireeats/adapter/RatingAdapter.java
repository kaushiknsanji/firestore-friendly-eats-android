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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.ItemRatingBinding;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView adapter for a bunch of Ratings.
 */
public class RatingAdapter extends FirestoreAdapter<RatingAdapter.ViewHolder> {

    public RatingAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Rating.class));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemRatingBinding mItemBinding;

        public ViewHolder(View itemView) {
            super(itemView);

            // Bind itemView with ViewBinding
            mItemBinding = ItemRatingBinding.bind(itemView);
        }

        public void bind(@Nullable Rating rating) {
            Objects.requireNonNull(rating);

            mItemBinding.ratingItemName.setText(rating.getUserName());
            mItemBinding.ratingItemRating.setRating((float) rating.getRating());
            mItemBinding.ratingItemText.setText(rating.getText());
        }
    }

}
