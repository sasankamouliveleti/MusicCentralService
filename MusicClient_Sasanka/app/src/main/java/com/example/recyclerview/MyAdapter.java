package com.example.recyclerview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicclient_sasanka.R;


import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    protected static final String TAG = "MyAdapter";
    private final ArrayList<String> songsTitle; /* List to store all the song titles*/
    private final ArrayList<String> songsArtist; /* List to store all the song Artists*/
    private final ArrayList<Bitmap> songsPicture; /* List to store all the song Picutres*/
    private final ArrayList<String> songsURL; /* List to store all the song urls which is not in use*/
    private final com.example.recyclerview.RVClickListener RVlistener;
    private final Context context;
    /* Definig the constructor*/
    public MyAdapter(ArrayList<String> titleList, ArrayList<String> artistList, ArrayList<Bitmap> pictureList, ArrayList<String> urlList, com.example.recyclerview.RVClickListener listener, Activity context) {
        this.songsTitle = titleList;
        this.songsArtist = artistList;
        this.songsPicture = pictureList;
        this.songsURL = urlList;
        this.RVlistener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listView;
        listView = inflater.inflate(R.layout.recylerview_list, parent, false);
        return new ViewHolder(listView, RVlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /* Set Various fields with values in the layout*/
        holder.song_name.setText(songsTitle.get(position));
        holder.artist_name.setText(songsArtist.get(position));
        Log.i(TAG, "onBindViewHolder: " + songsPicture.get(position));
        holder.image.setImageBitmap((Bitmap) songsPicture.get(position)); /* Set image bitmap*/
    }

    @Override
    public int getItemCount() {
        return songsTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView song_name;
        public TextView artist_name;
        public ImageView image;
        private final com.example.recyclerview.RVClickListener listener;
        private View itemView;

        public ViewHolder(@NonNull View itemView, com.example.recyclerview.RVClickListener passedListener) {
            super(itemView);
            song_name = (TextView) itemView.findViewById(R.id.song_name);
            artist_name = (TextView) itemView.findViewById(R.id.artist_name);
            image = (ImageView) itemView.findViewById(R.id.img_item);
            this.itemView = itemView;
            this.listener = passedListener;
            itemView.setOnClickListener(this);
        }
        /* Onclick to call the listener implemented in the main activity*/
        @Override
        public void onClick(View v) {
            try {
                listener.onClick(v, getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("ON_CLICK", "in the onclick in view holder");
        }
    }
}
