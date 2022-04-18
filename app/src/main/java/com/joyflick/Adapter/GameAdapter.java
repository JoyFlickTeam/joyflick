package com.joyflick.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.R;
import com.joyflick.models.Game;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    public static final String TAG = "GameAdapter";
    Context context;
    List<Game> games;

    public GameAdapter(Context context, List<Game> games){
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View gameView = LayoutInflater.from(context).inflate(R.layout.item_game,parent, false);
        return new ViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder" + position);
        // get the game position
        Game game = games.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(Game game) {
            String imageUrl;

            imageUrl = game.getPoster();
            Log.i(TAG, imageUrl);
            Glide.with(context).load(imageUrl).into(ivPoster);
        }
    }
}// end of the class
