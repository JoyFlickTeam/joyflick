package com.joyflick.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.R;
import com.joyflick.fragments.ProfileFragment;
import com.joyflick.models.Comments;
import com.joyflick.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{
    public static final String TAG = "CommentsAdapter";
    Context context;
    List<Comments> comments;

    public CommentsAdapter(Context context, List<Comments> comments){
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View commmentView = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(commmentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comments comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivCommentUserPicture;
        TextView tvCommentUserName;
        TextView tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCommentUserPicture = itemView.findViewById(R.id.ivCommentUserPicture);
            tvCommentUserName = itemView.findViewById(R.id.tvCommentUserName);
            tvComment = itemView.findViewById(R.id.tvComment);
        }

        public void bind(Comments comment){
            tvCommentUserName.setText(comment.getUser().getUsername());
            tvComment.setText(comment.getComment());
            ParseFile profilePicture = comment.getUser().getParseFile("profilePicture");
            if(profilePicture != null){
                Glide.with(context).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).centerCrop().into(ivCommentUserPicture);
            }
            else{
                Glide.with(context).load(R.drawable.logo1).centerCrop().into(ivCommentUserPicture);
            }
            // Navigate to commenter's profile
            ivCommentUserPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uId", comment.getUser().getObjectId());
                    ProfileFragment pFragment = new ProfileFragment();
                    pFragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, pFragment).addToBackStack(null).commit();
                }
            });
        }
    }

    public void clear(){
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Comments> list){
        comments.addAll(list);
        notifyDataSetChanged();
    }
}
