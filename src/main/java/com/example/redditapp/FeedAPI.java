package com.example.redditapp;

import com.example.redditapp.Message;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FeedAPI {
    //String BASE_URL = "https://www.reddit.com/r/";

    @GET("/hot.json")
    Call<String> hot_list(@Query("g") String cantry,@Query("after") String after,@Query("before") String before,@Query("count") int count,@Query("limit") int limit);
}
