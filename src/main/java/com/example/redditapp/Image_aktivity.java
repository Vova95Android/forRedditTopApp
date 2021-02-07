package com.example.redditapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

public class Image_aktivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "save_state";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE=10;
    private SharedPreferences save_state;
    private int image_name=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        save_state=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.image_layout);
        image_name=save_state.getInt(Static.Image_name,0); //Получение последнего имени изображения (Get the last image name)
        ImageView imageview=findViewById(R.id.imageView);
        Bundle id = getIntent().getExtras();
        String uri=id.getString(Static.Image_URL);                  //Получение URL адреса изображения (Getting the URL of the image)
        Picasso.with(Image_aktivity.this).load(uri).into(imageview);

    }


    public void buttons(View view) {
        if (view.getId()==R.id.button_back){finish();}  //Кнопка назад (Back button)
        else if (view.getId()==R.id.button_save){       //Кнопка сохранить в галерею (Save to gallery button)
            //Проверка разрешения WRITE_EXTERNAL_STORAGE (Checking the WRITE_EXTERNAL_STORAGE permission)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            else {
                ImageView imageview=findViewById(R.id.imageView);
                imageview.setDrawingCacheEnabled(true);
                //Преобразование ImageView в растровое изображение (Converting ImageView to Bitmap)
                Bitmap b = imageview.getDrawingCache();
                //Сохранение изображения (Saving an image)
                MediaStore.Images.Media.insertImage(getContentResolver(), b,"Reddit_image_"+ image_name,"Reddit_image_"+ image_name);
                image_name++;
                //Сохранение последнего имени изображения (Save the last image name)
                SharedPreferences.Editor e=save_state.edit();
                e.putInt(Static.Image_name,image_name);
                e.apply();
                Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // разрешение было предоставлено (permission was granted)
                ImageView imageview=findViewById(R.id.imageView);
                imageview.setDrawingCacheEnabled(true);
                Bitmap b = imageview.getDrawingCache();
                MediaStore.Images.Media.insertImage(getContentResolver(), b,"Reddit_image_"+ image_name,"Reddit_image_"+ image_name);
                image_name++;
                SharedPreferences.Editor e=save_state.edit();
                e.putInt(Static.Image_name,image_name);
                e.apply();
                Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            } else {
                // разрешение не было предоставлено (permission was not granted)
                Toast.makeText(this, "Please allow the use of internal storage to continue", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
