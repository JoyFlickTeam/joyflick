package com.joyflick.Adapter;

import android.content.Context;
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
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private static final int MESSAGE_OUTGOING = 129;
    private static final int MESSAGE_INCOMING = 921;
    private List<Message> mMessages;
    private static Context mContext;
    private String mUserId;

    public ChatAdapter(Context context, String userId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
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
                //Glide.with(mContext).load(getProfileUrl(message.getUserId())).circleCrop().into(imageOther);
                body.setText(message.getBody());
                name.setText(message.getUserId());
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
                //Glide.with(mContext).load(getProfileUrl(message.getUserId())).circleCrop().into(imageMe);
                body.setText(message.getBody());
            }
        }
    }
}