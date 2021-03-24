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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView adapter for displaying the results of a Firestore {@link Query}.
 * <p>
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * {@link DocumentSnapshot#toObject(Class)} is not cached so the same object may be deserialized
 * many times as the user scrolls.
 * <p>
 * See the adapter classes in FirebaseUI (https://github.com/firebase/FirebaseUI-Android/tree/master/firestore) for a
 * more efficient implementation of a Firestore RecyclerView Adapter.
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    // Constant used for logs
    private static final String TAG = "Firestore Adapter";
    // List to store the snapshots retrieved from the Query for the changes
    private final ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();
    // The Query to read the snapshots from
    private Query mQuery;
    // The Listener to be registered on the Query set
    private ListenerRegistration mRegistration;

    /**
     * Constructor of {@link FirestoreAdapter}
     *
     * @param query The {@link Query} to listen for changes and read the snapshots from.
     */
    public FirestoreAdapter(Query query) {
        mQuery = query;
    }

    /**
     * Starts listening to the {@link Query} set.
     */
    public void startListening() {
        // TODO(developer): Implement
    }

    /**
     * Stops listening to the {@link Query} set.
     */
    public void stopListening() {
        // If listener was previously registered, then detach the listener from the Query
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        // Clear existing data
        mSnapshots.clear();
        notifyDataSetChanged();
    }

    /**
     * Method to change the {@link Query} previously set.
     *
     * @param query The new {@link Query} to listen for changes and read the snapshots from.
     */
    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        mSnapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        startListening();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        // Return the number of snapshots present as the Item count of the Adapter
        return mSnapshots.size();
    }

    /**
     * Returns the {@link DocumentSnapshot} present at the position {@code index}.
     */
    protected DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    /**
     * Called when there is an error while listening to the {@link Query} set.
     * Can be overridden by subclasses to perform some action or show some message on error.
     *
     * @param error The error occurred while listening to the {@link Query} set.
     */
    protected void onError(FirebaseFirestoreException error) {
    }

    /**
     * Called after the new snapshot of the Event was processed successfully.
     * Can be overridden by subclasses to perform some action or show/hide appropriate views
     * on checking for "no documents" state.
     */
    protected void onDataChanged() {
    }
}