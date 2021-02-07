package com.example.redditapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RedditPostAdapter extends ArrayAdapter<RedditPost> {
    public RedditPostAdapter(Context context, ArrayList<RedditPost> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Получение элемента данных для этой позиции(Get the data item for this position)
        RedditPost post = getItem(position);
            //Проверка, не используется ли существующее представление повторно (Checking if an existing view is not being reused)
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row, parent, false);
        }

        // Поиск в макете (Search in layout)
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
        TextView tvComments = (TextView) convertView.findViewById(R.id.tvComments);
        TextView tvSubreddit = (TextView) convertView.findViewById(R.id.tvSubreddit);
        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);


        // Заполнение данных(Filling in data)
        tvDate.setText(Long.toString(((System.currentTimeMillis()/1000)-post.getDate())/3600)+"h");
        tvTitle.setText(post.getTitle());
        tvAuthor.setText("(" + post.getAuthor() + ")");
        tvComments.setText(post.getNumComments() + " comments");
        tvSubreddit.setText(post.getSubreddit());


        // Display the thumbnail if there is one
        String thumbnailUrl = post.getThumbnail();
        if((thumbnailUrl.length() > 0) && (!thumbnailUrl.equals("self"))){
            Picasso.with(getContext()).load(thumbnailUrl).into(ivThumbnail);
        }

        return convertView;
    }
}
