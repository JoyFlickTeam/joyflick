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
import com.joyflick.models.Game;
import com.joyflick.models.User;

import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {

    public static final String TAG = "userAdapter";
    Context context;
    List<User> users;

    public userAdapter(Context context, List<User> user){
        this.context = context;
        this.users = user;
    }

    @NonNull
    @Override
    public userAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View userView = LayoutInflater.from(context).inflate(R.layout.item_user,parent, false);
        return new userAdapter.ViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(@NonNull userAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder" + position);
        // get the game position
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView userImage;

        public ViewHolder(View userView) {
            super(userView);
            username = userView.findViewById(R.id.UserName);
            userImage = userView.findViewById(R.id.UserPicture);
        }

        public void bind(User user) {
            String imageUrl;
            username.setText(User.getKeyUsername());
            imageUrl = User.getKeyProfile();
            Log.i(TAG, imageUrl);
            Glide.with(context).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(userImage);
        }
    }
}
