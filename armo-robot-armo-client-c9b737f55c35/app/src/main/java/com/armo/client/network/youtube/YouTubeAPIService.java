package com.armo.client.network.youtube;

import com.armo.client.model.youtube.PlayListItems;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface YouTubeAPIService {


    @GET("playlistItems?part=id,snippet&maxResults=50")
    Single<PlayListItems> getPlayListVideos(@Query("key") String apiKey, @Query("playlistId") String listId);


}
