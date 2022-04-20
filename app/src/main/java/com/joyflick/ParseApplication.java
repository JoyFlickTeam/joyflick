package com.joyflick;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("zmszOLTkEAiXPYJD3AWP6XL6OSsHIoBGz2buAmRq")
                .clientKey("zxMWBvcKR5CzNllUEMwnv5iDBRu2ONa16RzGBDOf")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
