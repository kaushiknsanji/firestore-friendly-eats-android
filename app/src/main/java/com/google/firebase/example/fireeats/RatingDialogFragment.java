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

import com.google.firebase.example.fireeats.databinding.DialogRatingBinding;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.example.fireeats.util.FirebaseUtil;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog Fragment containing rating form.
 */
public class RatingDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "RatingDialog";

    private DialogRatingBinding mBinding;
    private RatingListener mRatingListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate with ViewBinding
        mBinding = DialogRatingBinding.inflate(inflater, container, false);

        mBinding.restaurantFormButton.setOnClickListener(this);
        mBinding.restaurantFormCancel.setOnClickListener(this);

        // Return the root view
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof RatingListener) {
            mRatingListener = (RatingListener) context;
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
        if (view.getId() == mBinding.restaurantFormButton.getId()) {
            onSubmitClicked(view);
        } else if (view.getId() == mBinding.restaurantFormCancel.getId()) {
            onCancelClicked(view);
        }
    }

    public void onSubmitClicked(View view) {
        Rating rating = new Rating(
                Objects.requireNonNull(FirebaseUtil.getAuth().getCurrentUser()),
                mBinding.restaurantFormRating.getRating(),
                mBinding.restaurantFormText.toString());

        if (mRatingListener != null) {
            mRatingListener.onRating(rating);
        }

        dismiss();
    }

    public void onCancelClicked(View view) {
        dismiss();
    }

    interface RatingListener {

        void onRating(Rating rating);

    }
}
