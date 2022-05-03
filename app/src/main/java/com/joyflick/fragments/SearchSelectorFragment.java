package com.joyflick.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.joyflick.R;

public class SearchSelectorFragment extends Fragment {
    public static final String TAG = "SearchSelectorFragment";
    private ImageButton ibSearchGames;
    private ImageButton ibSearchUsers;

    public SearchSelectorFragment(){
        // Empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibSearchGames = view.findViewById(R.id.ibSearchGames);
        ibSearchUsers = view.findViewById(R.id.ibSearchUsers);
        ibSearchGames.setBackground(null);
        ibSearchUsers.setBackground(null);

        Glide.with(view).load(R.drawable.search_games).into(ibSearchGames);
        Glide.with(view).load(R.drawable.search_users).into(ibSearchUsers);

        ibSearchGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment sFragment = new SearchFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, sFragment).addToBackStack(null).commit();
            }
        });
        ibSearchUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchUserFragment suFragment = new SearchUserFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, suFragment).addToBackStack(null).commit();
            }
        });
    }
}
