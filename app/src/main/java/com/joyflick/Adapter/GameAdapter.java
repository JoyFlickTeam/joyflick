package com.joyflick.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.R;
import com.joyflick.fragments.GameDetailFragment;
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
        RelativeLayout container;
        ImageView ivPoster;
        TextView gameTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.gameContainer);
            gameTitle = itemView.findViewById(R.id.gameTitle);
        }

        public void bind(Game game) {
            String imageUrl;
            gameTitle.setText(game.getTitle());
            imageUrl = game.getPoster();
            Log.i(TAG, imageUrl);
            Glide.with(context).load(imageUrl).into(ivPoster);

            // Click listener for row
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to game detail fragment
                    Pair<View, String> pPoster = Pair.create((View) ivPoster, "iPoster");
                    Pair<View,String> pTitle = Pair.create((View) gameTitle, "tTitle");
                    Toast.makeText(v.getContext(), game.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}// end of the class
