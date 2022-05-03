package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.Adapter.ChatHistoryAdapter;
import com.joyflick.R;
import com.joyflick.models.Message;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ChatHistoryFragment extends Fragment {
    public static final String TAG = "ChatHistoryFragment";
    private TextView tvNoChatHistory;
    private RecyclerView rvChatUsers;
    protected String uId;
    protected List<Message> cHistory;
    protected ChatHistoryAdapter adapter;

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
        cHistory = new ArrayList<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        uId = currentUser.getObjectId();
        adapter = new ChatHistoryAdapter(getContext(), uId, cHistory);
        adapter.setHasStableIds(true);
        rvChatUsers.setAdapter(adapter);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getContext());
        rvChatUsers.setLayoutManager(LinearLayoutManager);
        queryChatHistory();
    }

    protected void queryChatHistory(){
        // Check for latest message sent by user
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class);
        query1.whereEqualTo(Message.KEY_USER_ID, uId);
        // Check for latest message received by user
        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);
        query2.whereEqualTo(Message.KEY_OTHER_ID, uId);
        List<ParseQuery<Message>> qList = new ArrayList<>();
        qList.add(query1);
        qList.add(query2);
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.or(qList);
        query.orderByDescending("createdAt");
        Log.i(TAG, "Querying messages for " + Message.KEY_USER_ID + " and " + Message.KEY_OTHER_ID + " equal to " + uId);

        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting message history" + e);
                    return;
                }
                adapter.clear();

                // Get latest message from each unique user this user has a message history with
                HashSet<String> uniqueUsersIds = new HashSet<>();
                for(Message msg: messages){
                    Log.i(TAG, msg.getUserId() + " to " + msg.getOtherId() + ": " + msg.getBody());
                    if(uId.equals(msg.getUserId()) && !uniqueUsersIds.contains(msg.getOtherId())){
                        // New unique user found in otherId, add to list
                        Log.i(TAG, "otherId " + msg.getOtherId() + " being added to unique list, add message to adapter... ");
                        uniqueUsersIds.add(msg.getOtherId());
                        cHistory.add(msg);
                    }
                    else if(uId.equals(msg.getOtherId()) && !uniqueUsersIds.contains(msg.getUserId())){
                        // New unique user found in userId, add to list
                        Log.i(TAG, "userId " + msg.getUserId() + " being added to unique list, add message to adapter... ");
                        uniqueUsersIds.add(msg.getUserId());
                        cHistory.add(msg);
                    }
                }
                Log.i(TAG, "Total unique user messages found: " + cHistory.size());
                adapter.addAll(cHistory);
                adapter.notifyDataSetChanged();

                if(adapter.getItemCount() == 0){
                    // User has no message history
                    tvNoChatHistory.setVisibility(View.VISIBLE);
                }
                else{
                    tvNoChatHistory.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
