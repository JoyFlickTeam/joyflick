package com.joyflick.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.R;
import com.joyflick.models.Game;
import com.joyflick.models.User;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> implements Filterable {
    private List<ParseUser> fullitems;
    public static final String TAG = "userAdapter";
    Context context;
    List<ParseUser> users;

    public userAdapter(Context context, List<ParseUser> user){
        this.context = context;
        this.users = user;
        fullitems = new ArrayList<>(user);

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
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return examplefilter;
    }
    private Filter examplefilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ParseUser> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(fullitems);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(ParseUser item : fullitems){
                    if(item.getUsername().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            fullitems.clear();
            fullitems.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView userImage;

        public ViewHolder(View userView) {
            super(userView);
            username = userView.findViewById(R.id.UserName);
            userImage = userView.findViewById(R.id.UserPicture);
        }

        public void bind(ParseUser user) {
            ParseFile imageUrl;
            username.setText(user.getUsername());
            imageUrl = user.getParseFile("profilePicture");
            Log.i(TAG, String.valueOf(imageUrl));
            Log.i(TAG, "Attempting to load profile picture" + imageUrl);
            if(imageUrl != null) {
                Glide.with(userImage.getContext()).load(imageUrl.getUrl()).placeholder(R.drawable.logo1).into(userImage);
            }
            else{
                Glide.with(userImage.getContext()).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(userImage);
            }
            //Glide.with(userImage.getContext()).load(imageUrl.getUrl()).into(userImage);
            //Glide.with(userImage.getContext()).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(userImage);
        }
    }
}
