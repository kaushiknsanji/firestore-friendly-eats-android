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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.example.fireeats.databinding.DialogFiltersBinding;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog Fragment containing filter form.
 */
public class FilterDialogFragment extends DialogFragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // Constant used for logs
    public static final String TAG = "FilterDialog";

    // Constant for when "Price" filter is not selected, i.e., set to the default "Any price"
    private static final int ANY_PRICE = -1;

    private DialogFiltersBinding mBinding;
    private FilterListener mFilterListener;

    // ArrayAdapter instance for "Sort" Spinner
    private ArrayAdapter<String> mSortOptionsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate with ViewBinding
        mBinding = DialogFiltersBinding.inflate(inflater, container, false);

        // Set mutable data for "Sort" Spinner
        ArrayList<String> sortByOptions = new ArrayList<>(
                Arrays.asList(requireContext().getResources().getStringArray(R.array.sort_by))
        );
        // Create Adapter for "Sort" Spinner, with default layout for Spinner item
        mSortOptionsAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sortByOptions
        );
        // Set layout to use when list of choices appear as dropdown
        mSortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set Adapter for "Sort" Spinner
        mBinding.spinnerSort.setAdapter(mSortOptionsAdapter);

        // Add Item Selected listener on "Price" Spinner
        mBinding.spinnerPrice.setOnItemSelectedListener(this);

        // Add Click listeners on Dialog Buttons
        mBinding.buttonSearch.setOnClickListener(this);
        mBinding.buttonCancel.setOnClickListener(this);

        // Return the root view
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getDialog()).getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Remove dialog.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear ViewBinding instance
        mBinding = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mBinding.buttonSearch.getId()) {
            onSearchClicked();
        } else if (view.getId() == mBinding.buttonCancel.getId()) {
            onCancelClicked();
        }
    }

    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedCategory() {
        String selected = (String) mBinding.spinnerCategory.getSelectedItem();
        if (getString(R.string.value_any_category).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedCity() {
        String selected = (String) mBinding.spinnerCity.getSelectedItem();
        if (getString(R.string.value_any_city).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private int getSelectedPrice() {
        String selected = (String) mBinding.spinnerPrice.getSelectedItem();
        if (selected.equals(getString(R.string.price_1))) {
            return 1;
        } else if (selected.equals(getString(R.string.price_2))) {
            return 2;
        } else if (selected.equals(getString(R.string.price_3))) {
            return 3;
        } else {
            return ANY_PRICE;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mBinding.spinnerSort.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Restaurant.FIELD_AVG_RATING;
        }
        if (getString(R.string.sort_by_price).equals(selected)) {
            return Restaurant.FIELD_PRICE;
        }
        if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Restaurant.FIELD_POPULARITY;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mBinding.spinnerSort.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_by_price).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mBinding != null) {
            mBinding.spinnerCategory.setSelection(0);
            mBinding.spinnerCity.setSelection(0);
            mBinding.spinnerPrice.setSelection(0);
            mBinding.spinnerSort.setSelection(0);
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mBinding != null) {
            filters.setCategory(getSelectedCategory());
            filters.setCity(getSelectedCity());
            filters.setPrice(getSelectedPrice());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Based on the "Price" Spinner selection,
        // show/hide the "Sort by Price" option in "Sort" Spinner
        modifySortByOptions(getSelectedPrice() == ANY_PRICE);
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // no-op
    }

    /**
     * Removes the "Sort by Price" option in "Sort" Spinner when a particular price
     * is selected by the user through the "Price" Spinner.
     * <p/>
     * When a particular price is not selected, i.e., when "Price" Spinner is set to "Any price",
     * then the "Sort by Price" option will be added back to the "Sort" Spinner if not present.
     *
     * @param showSortByPrice A {@link Boolean} to either add or remove the "Sort by Price" option
     *                        in "Sort" Spinner. When {@code true}, the "Sort by Price" option will
     *                        be added back if not present; otherwise it will be removed.
     */
    private void modifySortByOptions(boolean showSortByPrice) {
        // Read the position of "Sort by Price" option in "Sort" Spinner
        int sortByPricePosition = mSortOptionsAdapter.getPosition(getString(R.string.sort_by_price));

        if (showSortByPrice) {
            // Add the "Sort by Price" option if not present in the adapter
            if (sortByPricePosition == -1) {
                mSortOptionsAdapter.add(getString(R.string.sort_by_price));
            }
        } else {
            // Remove the "Sort by Price" option from the adapter
            if (sortByPricePosition > -1) {
                mSortOptionsAdapter.remove(mSortOptionsAdapter.getItem(sortByPricePosition));
            }
        }
    }

    interface FilterListener {

        void onFilter(Filters filters);

    }

}