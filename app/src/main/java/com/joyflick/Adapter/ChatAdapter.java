package com.joyflick.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.R;
import com.joyflick.models.Message;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    public static final String TAG = "ChatAdapter";
    private static final int MESSAGE_OUTGOING = 129;
    private static final int MESSAGE_INCOMING = 921;
    private List<Message> mMessages;
    private static Context mContext;
    protected static String mUserId;
    protected static String otherId;

    public ChatAdapter(Context context, String userId, String otherId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
        this.otherId = otherId;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new MessageViewHolder.IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new MessageViewHolder.OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        return message.getUserId() != null && message.getUserId().equals(mUserId);
    }

    public abstract static class MessageViewHolder extends RecyclerView.ViewHolder {
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(Message message);

        public static class IncomingMessageViewHolder extends MessageViewHolder {
            ImageView imageOther;
            TextView body;
            TextView name;

            public IncomingMessageViewHolder(View itemView) {
                super(itemView);
                imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
                body = (TextView)itemView.findViewById(R.id.tvBody);
                name = (TextView)itemView.findViewById(R.id.tvName);
            }

            @Override
            public void bindMessage(Message message) {
                queryOtherUser();
                body.setText(message.getBody());
            }

            protected void queryOtherUser(){
                ParseQuery<ParseUser> query= ParseUser.getQuery();
                query.whereEqualTo("objectId", otherId);
                query.setLimit(1);
                Log.i(TAG, "Querying users for " + Post.KEY_USER + " equal to " + otherId);

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> users, ParseException e) {
                        if(e != null){
                            Log.e(TAG, "Issue with getting user" + e);
                            return;
                        }
                        // There should be only one user
                        ParseUser profileUser = users.get(0);
                        // Show other user's profile picture and name
                        name.setText(profileUser.getUsername());
                        ParseFile profilePicture = profileUser.getParseFile("profilePicture");
                        if(profilePicture != null){
                            Log.i(TAG, "Attempting to load profile picture");
                            Glide.with(mContext).load(profilePicture.getUrl()).circleCrop().into(imageOther);
                        }
                        else{
                            Log.i(TAG, "Using default profile picture");
                            Glide.with(mContext).load(R.drawable.logo1).circleCrop().into(imageOther);
                        }
                    }
                });
            }
        }

        public static class OutgoingMessageViewHolder extends MessageViewHolder {
            ImageView imageMe;
            TextView body;

            public OutgoingMessageViewHolder(View itemView) {
                super(itemView);
                imageMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
                body = (TextView) itemView.findViewById(R.id.tvBody);
            }

            @Override
            public void bindMessage(Message message) {
                queryUser();
                body.setText(message.getBody());
            }

            protected void queryUser(){
                ParseQuery<ParseUser> query= ParseUser.getQuery();
                query.whereEqualTo("objectId", mUserId);
                query.setLimit(1);
                Log.i(TAG, "Querying users for " + Post.KEY_USER + " equal to " + mUserId);

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> users, ParseException e) {
                        if(e != null){
                            Log.e(TAG, "Issue with getting user" + e);
                            return;
                        }
                        // There should be only one user
                        ParseUser profileUser = users.get(0);
                        // Show user's profile picture
                        ParseFile profilePicture = profileUser.getParseFile("profilePicture");
                        if(profilePicture != null){
                            Log.i(TAG, "Attempting to load profile picture");
                            Glide.with(mContext).load(profilePicture.getUrl()).circleCrop().into(imageMe);
                        }
                        else{
                            Log.i(TAG, "Using default profile picture");
                            Glide.with(mContext).load(R.drawable.logo1).circleCrop().into(imageMe);
                        }
                    }
                });
            }
        }
    }
}