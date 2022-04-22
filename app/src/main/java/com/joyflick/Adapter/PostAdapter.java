package com.joyflick.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.R;
import com.joyflick.fragments.ReviewDetailFragment;
import com.joyflick.models.Game;
import com.joyflick.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public static final String TAG = "PostAdapter";
    Context context;
    List<Post> posts;

    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View ratingView = LayoutInflater.from(context).inflate(R.layout.item_game_rating,parent, false);
        return new PostAdapter.ViewHolder(ratingView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder" + position);
        // get the game position
        Post post = posts.get(position);
        holder.bind(post);
    }
    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout container;
        ImageView ivRatingUserPicture;
        TextView tvRatingUserName;
        RatingBar rbRatingGame;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.reviewContainer);
            ivRatingUserPicture = itemView.findViewById(R.id.ivRatingUserPicture);
            tvRatingUserName = itemView.findViewById(R.id.tvRatingUserName);
            rbRatingGame = itemView.findViewById(R.id.rbRatingGame);
        }
        public void bind(Post post) {
            //tvRatingUserName.setText(post.getUser().getUsername());
            rbRatingGame.setRating(Float.valueOf(post.getRating().toString()));
            /*ParseFile profilePicture = post.getUser().getParseFile("profilePicture");
            if(profilePicture != null){
                Glide.with(context).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).centerCrop().into(ivRatingUserPicture);
            }
            else{
                Glide.with(context).load(R.drawable.logo1).centerCrop().into(ivRatingUserPicture);
            }*/

            // Go to detailed review screen
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Pass review info to detailed review fragment
                    Bundle bundle = new Bundle();
                    // pass object id

                    ReviewDetailFragment rdFragment = new ReviewDetailFragment();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, rdFragment).addToBackStack(null).commit();
                }
            });
        }
    }

    public void clear(){
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list){
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
