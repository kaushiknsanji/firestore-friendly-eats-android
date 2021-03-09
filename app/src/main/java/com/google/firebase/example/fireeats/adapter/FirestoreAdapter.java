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

import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.Nullable;
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
        extends RecyclerView.Adapter<VH> implements EventListener<QuerySnapshot> {

    // Constant used for logs
    private static final String TAG = "FirestoreAdapter";
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
        if (mQuery != null && mRegistration == null) {
            // If Query is set and listener is not yet registered,
            // then register the listener on the Query
            mRegistration = mQuery.addSnapshotListener(this);
        }
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
     * {@code onEvent} will be called with the new value or the error if an error occurred. It's
     * guaranteed that exactly one of value or error will be non-{@code null}.
     *
     * @param snapshot The value of the event. {@code null} if there was an error.
     * @param error    The error if there was error. {@code null} otherwise.
     */
    @Override
    public void onEvent(@Nullable QuerySnapshot snapshot,
                        @Nullable FirebaseFirestoreException error) {
        // Handle listen error
        if (error != null) {
            Log.e(TAG, "onEvent: Listen error", error);
            // Propagate this error to onError
            onError(error);
            return;
        }

        // Lookup for changes since the last snapshot
        for (DocumentChange change : snapshot.getDocumentChanges()) {
            // Dispatch item change events to Adapter based on DocumentChange type
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }

        // Propagate this event to onDataChanged
        onDataChanged();
    }

    /**
     * Called when a new Document is added.
     * Adds the new snapshot to the list (maintained by the adapter) at its corresponding index
     * and triggers an item insertion event to the adapter at the position index added.
     *
     * @param change {@link DocumentChange} instance representing a change in the Document.
     */
    private void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    /**
     * Called when an existing Document is modified.
     * 1. When only the Document content has changed, then the corresponding snapshot in the snapshot
     * list (maintained by the adapter) is updated to the new snapshot and an item change event
     * for the position index is triggered to the adapter.
     * 2. When both the Document content and position is changed, then the old snapshot saved in the
     * snapshot list is removed and the new snapshot of the Document is added to this snapshot list
     * at its new index and an item moved event for the change in position index is triggered
     * to the adapter.
     *
     * @param change {@link DocumentChange} instance representing a change in the Document.
     */
    private void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // When there is a change in content only
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // When there is a change in content and position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    /**
     * Called when an existing Document is removed.
     * Removes the existing snapshot from the list (maintained by the adapter)
     * and triggers an item removed event to the adapter at the position index removed.
     *
     * @param change {@link DocumentChange} instance representing a change in the Document.
     */
    private void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    /**
     * Returns the {@link DocumentSnapshot} present at the position {@code index}.
     */
    protected final DocumentSnapshot getSnapshot(int index) {
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