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
package com.google.firebase.example.fireeats;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.example.fireeats.adapter.RatingAdapter;
import com.google.firebase.example.fireeats.databinding.ActivityRestaurantDetailBinding;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.util.FirebaseUtil;
import com.google.firebase.example.fireeats.util.GlideApp;
import com.google.firebase.example.fireeats.util.RestaurantUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class RestaurantDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot>,
        RatingDialogFragment.RatingListener {

    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    private static final String TAG = "RestaurantDetail";
    private ActivityRestaurantDetailBinding mBinding;

    private RatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mRestaurantRef;
    private ListenerRegistration mRestaurantRegistration;

    private RatingAdapter mRatingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate with ViewBinding
        mBinding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());
        // Set the root view from ViewBinding instance
        setContentView(mBinding.getRoot());

        mBinding.restaurantButtonBack.setOnClickListener(this);
        mBinding.fabShowRatingDialog.setOnClickListener(this);

        // Get restaurant ID from extras
        String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseUtil.getFirestore();

        // Get reference to the restaurant
        mRestaurantRef = mFirestore.collection("restaurants").document(restaurantId);

        // Get ratings
        Query ratingsQuery = mRestaurantRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mBinding.recyclerRatings.setVisibility(View.GONE);
                    mBinding.viewEmptyRatings.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerRatings.setVisibility(View.VISIBLE);
                    mBinding.viewEmptyRatings.setVisibility(View.GONE);
                }
            }
        };

        mBinding.recyclerRatings.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerRatings.setAdapter(mRatingAdapter);

        mRatingDialog = new RatingDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mRestaurantRegistration = mRestaurantRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mRestaurantRegistration != null) {
            mRestaurantRegistration.remove();
            mRestaurantRegistration = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mBinding.restaurantButtonBack.getId()) {
            onBackArrowClicked(view);
        } else if (view.getId() == mBinding.fabShowRatingDialog.getId()) {
            onAddRatingClicked(view);
        }
    }

    private Task<Void> addRating(final DocumentReference restaurantRef, final Rating rating) {
        // TODO(developer): Implement
        return Tasks.forException(new Exception("not yet implemented"));
    }

    /**
     * Listener for the Restaurant document ({@link #mRestaurantRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onRestaurantLoaded(snapshot.toObject(Restaurant.class));
    }

    private void onRestaurantLoaded(@Nullable Restaurant restaurant) {
        Objects.requireNonNull(restaurant);

        mBinding.restaurantName.setText(restaurant.getName());
        mBinding.restaurantRating.setRating((float) restaurant.getAvgRating());
        mBinding.restaurantNumRatings.setText(getString(R.string.fmt_num_ratings, restaurant.getNumRatings()));
        mBinding.restaurantCity.setText(restaurant.getCity());
        mBinding.restaurantCategory.setText(restaurant.getCategory());
        mBinding.restaurantPrice.setText(RestaurantUtil.getPriceString(restaurant));

        // Background image
        GlideApp.with(mBinding.restaurantImage.getContext())
                .load(restaurant.getPhoto())
                .into(mBinding.restaurantImage);
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    public void onAddRatingClicked(View view) {
        mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);
    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mRestaurantRef, rating)
                .addOnSuccessListener(this, aVoid -> {
                    Log.d(TAG, "Rating added");

                    // Hide keyboard and scroll to top
                    hideKeyboard();
                    mBinding.recyclerRatings.smoothScrollToPosition(0);
                })
                .addOnFailureListener(this, e -> {
                    Log.w(TAG, "Add rating failed", e);

                    // Show failure message and hide keyboard
                    hideKeyboard();
                    Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                            Snackbar.LENGTH_SHORT).show();
                });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
