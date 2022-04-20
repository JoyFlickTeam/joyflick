package com.joyflick.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.joyflick.Adapter.GameAdapter;
import com.joyflick.R;

import java.util.ArrayList;

public class GameDetailFragment extends Fragment {
    public static final String TAG = "GameDetailFragment";
    private ImageView ivPoster;
    private TextView tvName;

    public GameDetailFragment(){
        // Empty public constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivPoster = view.findViewById(R.id.ivGamePoster);
        tvName = view.findViewById(R.id.tvGameName);
    }// end of the onView Created
}
