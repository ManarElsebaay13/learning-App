package com.armo.client.network.youtube;

import com.armo.client.model.youtube.PlayListItems;
import com.armo.client.utils.Constants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class YouTubeAPIConnectionHandler {

    private static YouTubeAPIConnectionHandler instance;

    private YouTubeAPIService service;

    private YouTubeAPIConnectionHandler() {


        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();

        service = retrofit.create(YouTubeAPIService.class);
    }

    public static YouTubeAPIConnectionHandler getInstance() {
        if (instance == null) {
            synchronized (YouTubeAPIConnectionHandler.class) {
                if (instance == null)
                    instance = new YouTubeAPIConnectionHandler();
            }
        }
        return instance;
    }

    public Single<PlayListItems> getPlayListVideos(String listId) {
        return service.getPlayListVideos(Constants.YOUTUBE_API_KEY, listId);
    }
}
