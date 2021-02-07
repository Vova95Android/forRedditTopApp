package com.example.redditapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RedditPost {
    private String title;       //Переменные для считывания из JSON (Variables to read from JSON)
    private String author;
    private int numComments;
    private String subreddit;
    private String thumbnail;
    private String url;
    private int upvotes;
    private long date;
    private int numb_post=0;

    //Открытые методы для получения переменных (Public methods for getting variables)
    public String getTitle() { return title; }

    public String getAuthor() {
        return author;
    }

    public int getNumComments() {
        return numComments;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getUrl(){
        return url;
    }

    public int getUpvotes(){
        return upvotes;
    }
    public long getDate(){ return date; }

    public int getNumb_post(){return numb_post;}

    // Возвращает RedditPost из заданных данных JSON (Returns a RedditPost from the given JSON data)
    public static RedditPost fromJson(JSONObject jsonObject){
        RedditPost p = new RedditPost();
        try{
            p.title = jsonObject.getString("title");
            p.author = jsonObject.getString("author");
            p.numComments = jsonObject.getInt("num_comments");
            p.subreddit = jsonObject.getString("subreddit");
            p.url = jsonObject.getString("url");
            p.upvotes = jsonObject.getInt("score");
            p.date = jsonObject.getLong("created_utc");


            // Получите URL-адрес миниатюры (Get the thumbnail Url)
            try{
                p.thumbnail = jsonObject.getString("thumbnail");
            } catch (JSONException e){
                // There is no thumbnail
                p.thumbnail = "";
            }
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return p;
    }

    public static ArrayList<RedditPost> fromJson(JSONArray jsonArray){
        ArrayList<RedditPost> posts = new ArrayList<RedditPost>(jsonArray.length());

        // Преобразуем каждый элемент в массиве json в объект json, а затем в сообщение (Convert each element in the json array to a json object, then to a Post)
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject postJson = null;
            try{
                postJson = jsonArray.getJSONObject(i).getJSONObject("data");
            } catch(Exception e){
                e.printStackTrace();
                continue;
            }

            RedditPost post = RedditPost.fromJson(postJson);
            post.numb_post=i;
            if(post != null){
                posts.add(post);
            }
        }

        return posts;
    }
}
