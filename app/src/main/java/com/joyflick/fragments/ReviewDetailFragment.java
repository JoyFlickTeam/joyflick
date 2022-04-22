package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.joyflick.R;

public class ReviewDetailFragment extends Fragment {
    private ImageView ivDetailGamePicture;
    private TextView tvDetailGameName;
    private ImageView ivDetailUserPicture;
    private TextView tvDetailUserName;
    private TextView tvReview;
    private Button btnAddComment;

    public ReviewDetailFragment(){
        // Empty public constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivDetailGamePicture = view.findViewById(R.id.ivDetailGamePicture);
        tvDetailGameName = view.findViewById(R.id.tvDetailGameName);
        ivDetailUserPicture = view.findViewById(R.id.ivDetailUserPicture);
        tvDetailUserName = view.findViewById(R.id.tvDetailUserName);
        tvReview = view.findViewById(R.id.tvReview);
        btnAddComment = view.findViewById(R.id.btnAddComment);
    }
}
