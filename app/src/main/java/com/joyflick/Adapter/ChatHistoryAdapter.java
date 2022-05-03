package com.joyflick.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.ChatActivity;
import com.joyflick.R;
import com.joyflick.models.Message;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
    public static final String TAG = "ChatHistoryAdapter";
    List<Message> chMessages;
    Context chContext;
    String chUserId;

    public ChatHistoryAdapter(Context context, String userId, List<Message> messages) {
        this.chMessages = messages;
        this.chUserId = userId;
        this.chContext = context;
    }

    @NonNull
    @Override
    public ChatHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View chatView = LayoutInflater.from(chContext).inflate(R.layout.item_chat,parent, false);
        return new ChatHistoryAdapter.ViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder " + position);
        // Fix to duplicating recycler view
        if(position < (chMessages.size() + 1) / 2) {
            Message message = chMessages.get(position);
            holder.bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout container;
        ImageView ivChUserPicture;
        TextView tvChUserName;
        TextView tvChUserMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChUserPicture = itemView.findViewById(R.id.ivChUserPicture);
            tvChUserName = itemView.findViewById(R.id.tvChUserName);
            tvChUserMessage = itemView.findViewById(R.id.tvChUserMessage);
            container = itemView.findViewById(R.id.chContainer);
        }

        public void bind(Message message){
            // Display other user's info and body of latest message from their chat history
            if(chUserId.equals(message.getUserId())) {
                // If latest message is sent by current user, get other user's info from otherId
                queryUser(message.getOtherId());
            }
            else{
                // Latest message not sent by current user, get other user's info from userId
                queryUser(message.getUserId());
            }
            tvChUserMessage.setText(message.getBody());

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start chat activity with other user
                    Log.i(TAG, "Navigating to chat activity");
                    Bundle bundle = new Bundle();
                    if(chUserId.equals(message.getUserId())){
                        Log.i(TAG, chUserId + " is navigating to ChatActivity to chat with " + message.getOtherId());
                        bundle.putString("oId", message.getOtherId());
                    }
                    else{
                        Log.i(TAG, chUserId + " is navigating to ChatActivity to chat with " + message.getUserId());
                        bundle.putString("oId", message.getUserId());
                    }

                    Intent intent = new Intent(chContext, ChatActivity.class);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            });
        }

        protected void queryUser(String userId){
            ParseQuery<ParseUser> query= ParseUser.getQuery();
            query.whereEqualTo("objectId", userId);
            query.setLimit(1);
            Log.i(TAG, "Querying users for " + Post.KEY_USER + " equal to " + userId);

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if(e != null){
                        Log.e(TAG, "Issue with getting user" + e);
                        return;
                    }
                    // There should be only one user
                    ParseUser profileUser = users.get(0);
                    // Show user's profile picture, name, and posts
                    ParseFile profilePicture = profileUser.getParseFile("profilePicture");
                    if(profilePicture != null){
                        Log.i(TAG, "Attempting to load profile picture");
                        Glide.with(chContext).load(profilePicture.getUrl()).into(ivChUserPicture);
                    }
                    else{
                        Log.i(TAG, "Using default profile picture");
                        Glide.with(chContext).load(R.drawable.logo1).into(ivChUserPicture);
                    }
                    tvChUserName.setText(profileUser.getString("username"));
                }
            });
        }
    }

    public void clear(){
        chMessages.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Message> list){
        chMessages.addAll(list);
        notifyDataSetChanged();
    }
}
