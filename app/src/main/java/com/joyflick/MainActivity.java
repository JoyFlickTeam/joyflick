package com.joyflick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.joyflick.backbone.RAWGConnector;
import com.joyflick.fragments.ChatHistoryFragment;
import com.joyflick.fragments.FeedFragment;
import com.joyflick.fragments.FollowingFragment;
import com.joyflick.fragments.PostFragment;
import com.joyflick.fragments.ProfileFragment;
import com.joyflick.fragments.SearchSelectorFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private final int REQUEST_CODE = 10;

    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    //setup for menu item state color swapping
    private MenuItem selectedItem;

    //RAWGConnector wrapper
    //use with care
    private RAWGConnector rawg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set top bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo1);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView = findViewById(R.id.bottomNavigation);


        ColorStateList color_list = ContextCompat.getColorStateList(this, R.color.icon_color_shift);
        bottomNavigationView.setItemIconTintList(color_list);





        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                Fragment fragment;
                

                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new FollowingFragment();
                        break;
                    case R.id.action_releases:
                        fragment = new FeedFragment();
                        break;
                    case R.id.action_post:
                        fragment = new SearchSelectorFragment();
                        break;
                    case R.id.action_chat:
                        fragment = new ChatHistoryFragment();
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);



        rawg = new RAWGConnector();


        //temporary check for api. remove it afterwards.
        JSONObject handler;
        try {
            handler = rawg.getGamesJson();
            Log.i("TAG",handler.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        Log.i(TAG,"trigger");



        return super.onOptionsItemSelected(item);
    }
}