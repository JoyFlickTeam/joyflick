package com.joyflick.backbone;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RAWGConnector {



    //api is 20000 limited. please don't overuse this
    private static final String API_KEY = "f7ba6691680e4f7885044b6e27a3dbc7";
    private static final String WEB_FRONT = "https://rawg-video-games-database.p.rapidapi.com/games";
    private static final String TAG = "RAWGConnector";

    OkHttpClient client;

    JSONObject json_result;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public RAWGConnector(){
        client = new OkHttpClient();
    }

    public JSONObject getGamesJson() throws InterruptedException {

        Map<String,String> parameters = new HashMap<String,String>();


        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time_stamp_to_current = "1960-01-01,"+currentDate;

        parameters.put("dates",time_stamp_to_current);

        parameters.put("ordering","-released");

        json_result = this.getJsonWithParameters(parameters);

        return json_result;

    }

    public JSONObject getSearchedGamesJson(String search_query) throws InterruptedException {

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("search",search_query);

        json_result = this.getJsonWithParameters(parameters);
        return json_result;

    }

    private JSONObject getJsonWithParameters(Map<String,String> parameters) throws InterruptedException {

        parameters.put("key",API_KEY);

        Request request = new Request.Builder()
                .url(WEB_FRONT+RAWGConnector.convertToURLParsedData(parameters))
                .get()
                .addHeader("X-RapidAPI-Host", "rawg-video-games-database.p.rapidapi.com")
                .addHeader("X-RapidAPI-Key", "ec188c62d8msh1d5101344a5e1c6p1ea2c8jsn96fee295423e")
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,"jsonconvertfail",e);
                        json_result = null;
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            json_result = new JSONObject(response.body().string());
                        } catch (JSONException e) {
                            Log.e(TAG,"jsonconvertfail",e);
                        }
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await();

        return json_result;

    }

    public static String convertToURLParsedData(Map<String,String> parameters)
    {
        String final_url = "?";
        String separator = "&";
        for(String key : parameters.keySet())
        {
            String value = parameters.get(key);
            final_url += key + "=" + value + separator;
        }

        final_url = final_url.substring(0,final_url.length()-1);

        return final_url;
    }

    //TODO: Requires a model to have list of games and such, and make a special system that extracts those







}