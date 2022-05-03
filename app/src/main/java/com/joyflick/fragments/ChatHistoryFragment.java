package com.joyflick.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.joyflick.R;

public class ChatHistoryFragment extends Fragment {
    public static final String TAG = "ChatHistoryFragment";
    private TextView tvNoChatHistory;
    private RecyclerView rvChatUsers;

    public ChatHistoryFragment(){
        // Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNoChatHistory = view.findViewById(R.id.tvNoChatHistory);
        rvChatUsers = view.findViewById(R.id.rvChatUsers);
    }
}
