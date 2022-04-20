package com.joyflick.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int game_id;
    private String name;
    private String image_url;

    public Game (int game_id, String name, String image_url )
    {
        this.game_id = game_id;
        this.name = name;
        this.image_url = image_url;
    }

    public static Game json_parse(JSONObject single_game_json) throws JSONException {
        int id = single_game_json.getInt("id");
        String name = single_game_json.getString("name");
        String image_url = single_game_json.getString("background_image");

        return new Game(id,name,image_url);
    }

    public static List<Game> full_result_json_parse(JSONObject result_json) throws JSONException {
        JSONArray results = result_json.getJSONArray("results");

        List<Game> game_list = new ArrayList<Game>();

        for(int i = 0; i < results.length(); i++)
        {
            JSONObject single_game = results.getJSONObject(i);
            game_list.add(Game.json_parse(single_game));
        }

        return game_list;
    }
}
