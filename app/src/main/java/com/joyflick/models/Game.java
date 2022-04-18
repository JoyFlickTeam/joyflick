package com.joyflick.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Game {
    String Poster;
    String title;
    String release;
    double rating;
    int gameId;

    public Game(JSONObject jsonObject) throws JSONException {
        Poster = jsonObject.getString("background_image");
        title = jsonObject.getString("name");
        release = jsonObject.getString("released");
        rating = jsonObject.getDouble("rating");
        gameId = jsonObject.getInt("id");
    }

    public static List<Game> fromJsonArray(JSONArray gameJsonArray) throws JSONException {
        List<Game> games = new ArrayList<>();
        for(int i=0; i <gameJsonArray.length(); i++){
            games.add(new Game(gameJsonArray.getJSONObject(i)));
        }
        return games;
    }

    public String getPoster() {
        return Poster;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease() {
        return release;
    }

    public double getRating() {
        return rating;
    }

    public int getGameId() {
        return gameId;
    }
}// end of the classs
