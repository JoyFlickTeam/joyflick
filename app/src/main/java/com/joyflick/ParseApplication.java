package com.joyflick;

import android.app.Application;

import com.joyflick.models.Comments;
import com.joyflick.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Register Parse model
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comments.class);

        // Initialize Parse SDK
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("zmszOLTkEAiXPYJD3AWP6XL6OSsHIoBGz2buAmRq")
                .clientKey("zxMWBvcKR5CzNllUEMwnv5iDBRu2ONa16RzGBDOf")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
