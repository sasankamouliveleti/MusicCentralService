package com.example.musicclient_sasanka;

import static com.example.musicclient_sasanka.MainActivity.mediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {
    protected static final String TAG = "RecyclerViewActivity";
    ArrayList<String> songsTitle; /* List to store all the song titles*/
    ArrayList<String> songsArtist; /* List to store all the song artists*/
    ArrayList<Bitmap> songsPicture; /* List to store all the song bitmap pictures*/
    ArrayList<String> songsURL; /* List to store all the song URLS*/
    RecyclerView songsView;
    com.example.recyclerview.RVClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        /* Intialise all the values from the main activity*/
        songsTitle = MainActivity.titlesList;
        songsArtist = MainActivity.artistList;
        songsPicture = MainActivity.pictureList;
        songsURL = MainActivity.songsUrls;
        songsView = findViewById(R.id.recycler_view); /* intialise the recylcer view*/
        /* Listener to listen user clicks*/
        listener = (view, position) -> {
            String url = songsURL.get(position);
            if(mediaPlayer!=null){
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        };
        com.example.recyclerview.MyAdapter adapter = new com.example.recyclerview.MyAdapter(songsTitle, songsArtist, songsPicture, songsURL, listener, RecyclerViewActivity.this);
        songsView.setHasFixedSize(true);
        songsView.setLayoutManager(new LinearLayoutManager(this));/* Layout is Linear*/
        songsView.setAdapter(adapter);
    }
}