package com.joyflick.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.joyflick.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SettingsFragment extends Fragment {
    public static final String TAG = "SettingsFragment";
    public final static int PICK_PHOTO_CODE = 1046;
    private TextView tvProfileSettings;
    private ImageView ivSettingsProfileImage;
    private TextView tvSettingsProfileName;
    private EditText etChangeUsername;
    private Button btnChangeUsername;
    private EditText etChangePassword;
    private EditText etConfirmPassword;
    private Button btnChangePassword;
    private Button btnChangeProfilePhoto;

    public SettingsFragment(){
        // Empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvProfileSettings = view.findViewById(R.id.tvProfileSettings);
        ivSettingsProfileImage = view.findViewById(R.id.ivSettingsProfileImage);
        tvSettingsProfileName = view.findViewById(R.id.tvSettingsProfileName);
        etChangeUsername = view.findViewById(R.id.etChangeUsername);
        btnChangeUsername = view.findViewById(R.id.btnChangeUsername);
        etChangePassword = view.findViewById(R.id.etChangePassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangeProfilePhoto = view.findViewById(R.id.btnChangeProfilePhoto);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile profilePicture = currentUser.getParseFile("profilePicture");
        if(profilePicture != null){
            Log.i(TAG, "Attempting to load profile picture");
            Glide.with(getContext()).load(profilePicture.getUrl()).into(ivSettingsProfileImage);
        }
        else{
            Log.i(TAG, "Using default profile picture");
            Glide.with(getContext()).load(R.drawable.logo1).into(ivSettingsProfileImage);
        }
        tvSettingsProfileName.setText(currentUser.getUsername());

        // Change username
        btnChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etChangeUsername.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "New username can't be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    currentUser.setUsername(etChangeUsername.getText().toString());
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.e(TAG, "Issue with updating username: " + e);
                                Toast.makeText(getContext(), "Issue with updating username, it might be taken already", Toast.LENGTH_SHORT).show();
                            }
                            tvSettingsProfileName.setText(currentUser.getUsername());
                            Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        // Change password
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = etChangePassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                if(!newPassword.isEmpty() && !confirmPassword.isEmpty() && newPassword.equals(confirmPassword)){
                    // Matching text for new password AND not empty, try to change password
                    currentUser.setPassword(newPassword);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.e(TAG, "Issue with updating password: " + e);
                                Toast.makeText(getContext(), "Issue with changing password", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(), "Both passwords must match and not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Change profile picture
        btnChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(view);
            }
        });
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto (View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i("Profile Fragmentation","Tagout");
        startActivityForResult(intent, PICK_PHOTO_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapBytes = stream.toByteArray();

            ParseUser userBeforeSave = ParseUser.getCurrentUser();

            ParseFile parseFile = new ParseFile("profile_"+userBeforeSave.getUsername()+".png",bitmapBytes);

            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null)
                    {
                        userUpdate(parseFile);
                    }
                    else
                    {
                        Log.e(TAG,"Failed on Saving parsefile to cloud:"+e);
                    }
                }
            });
        }
    }

    public void userUpdate(ParseFile file)
    {
        Log.i(TAG,"checko!");
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("profilePicture", file);

        currentUser.saveInBackground(e -> {
            if(e == null)
            {
                ParseUser uploadedUser = ParseUser.getCurrentUser();

                Log.i(TAG, "Profile photo update success");
                Toast.makeText(getContext(), "Profile photo has been updated.", Toast.LENGTH_SHORT).show();
                // Update profile picture displayed
                ParseFile profilePicture = uploadedUser.getParseFile("profilePicture");
                Glide.with(getContext()).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).into(ivSettingsProfileImage);
            }
            else
            {
                Log.e(TAG,"Failed on Uploading the User with profile file:"+e);
            }
        });
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // Check Android version
            if(Build.VERSION.SDK_INT > 27){
                // Newer version detected - use new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // Use getBitmap for older Android versions
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // For hiding toolbar
    @Override
    public void onResume(){
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop(){
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
