package com.example.redditapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static java.lang.Math.log;

public class MainActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "save_state";
    private ListView lvPosts;
    private RedditPostAdapter postsAdapter;
    private String after="";                //Переменные страниц (Page variables)
    private String before="";
    private int count=0;
    private int one_pages=25;
    private SharedPreferences save_state;   //Переменная сохранения состояния (State persistence variable)
    public String[] URL_array=new String[one_pages];  //Массив ссылок на страницы (Array of page links)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save_state=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        after=save_state.getString(Static.next_page,"");     //Получение предыдущего состояния (Getting the previous state)
        before=save_state.getString(Static.before_page,"");
        count=save_state.getInt(Static.count,0);
        get_new_pages(Static.next_page);                                //Открытие страницы (Opening a page)

    }

    void get_new_pages(String next_page){
        SharedPreferences.Editor editor=save_state.edit();              //Сохранение предидущего состояния (Preservation of the previous state)
        editor.putString(Static.before_page,before);
        editor.putString(Static.next_page,after);
        editor.putInt(Static.count,count);
        editor.apply();

        Retrofit retrofit = new Retrofit.Builder()                      //Создаие билдера для GET запросов (Creating a builder for GET requests)
                .baseUrl("https://www.reddit.com/top/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        lvPosts = (ListView) findViewById(R.id.lvPosts);
        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {      //Слушатель нажатий на посты (Post click listener)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((URL_array[position].length()>10)&&(!URL_array[position].equals("self"))){
                    Intent image_intent=new Intent(MainActivity.this,Image_aktivity.class);
                    image_intent.putExtra(Static.Image_URL,URL_array[position]);
                    startActivity(image_intent);
                }
            }
        });
        ArrayList<RedditPost> aPosts = new ArrayList<RedditPost>();
        postsAdapter = new RedditPostAdapter(this, aPosts);          //Получение адаптера для ListView (Getting adapter for ListView)
        lvPosts.setAdapter(postsAdapter);
        Call<String> messages;
        FeedAPI feedApi = retrofit.create(FeedAPI.class);                   //Создание интерфейса (Interface creation)
        //GET запрос в зависимости от того следующая страница или предыдущая (GET request depending on whether the next page or the previous)
        if (next_page.equals(Static.next_page)) messages = feedApi.hot_list("GLOBAL",after,null,count,one_pages);
        else messages = feedApi.hot_list("GLOBAL",null,before,count,one_pages);
        messages.enqueue(new Callback<String>() {       //Обратный вызов запроса (Request callback)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONArray items = null;
               try{
                    JSONObject mainObject = new JSONObject(response.body());
                    //Получение массива сообщений json (Get the posts json array)

                    JSONObject data = mainObject.getJSONObject("data"); //Переход в раздел data в массиве JSON
                    //Получение имен для перехода по страницам (Getting names to navigate through pages)
                    before=data.getString("before");
                    after=data.getString("after");
                    if (next_page.equals(Static.next_page)) count=count+one_pages;
                    else count=count-one_pages;
                    items = data.getJSONArray("children");
                    // Разбор массива json на массив объектов модели (Parse the json array into array of model objects)
                    ArrayList<RedditPost> posts = RedditPost.fromJson(items);
                    for(int ithem_url=0;ithem_url<one_pages;ithem_url++) URL_array[ithem_url]=posts.get(ithem_url).getUrl();
                    // Загрузка объектов модели в адаптер(Load the model objects into the adapter)
                    for(RedditPost post: posts){
                        postsAdapter.add(post);
                    }
                    postsAdapter.notifyDataSetChanged();

                } catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    //Вызов нажатия на кнопки навигации страниц (Call for clicking on page navigation buttons)
    public void buttons(View view){
        if (view.getId()==R.id.button_before){
            get_new_pages(Static.before_page);
        }else if (view.getId()==R.id.button_next){
            get_new_pages(Static.next_page);
        }
    }
}
