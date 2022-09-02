package com.example.musiccentral_sasanka;

import static com.example.musiccentral_sasanka.Constants.artistNames;
import static com.example.musiccentral_sasanka.Constants.songPicture;
import static com.example.musiccentral_sasanka.Constants.songTitles;
import static com.example.musiccentral_sasanka.Constants.songURL;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.music_common.MusicCentralInterface;

import java.util.ArrayList;
import java.util.List;


public class MusicCentralService extends Service {
    protected static final String TAG = "MusicCentralService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "Music central style";

    public MusicCentralService() {
    }
    /* Method to convert list of strings to array list*/
    public static ArrayList<String> listToArrayList(List<String> myList) {
        ArrayList<String> arraylist = new ArrayList<String>();
        for (Object object : myList) {
            arraylist.add((String) object);
        }
        return arraylist;
    }
    /* method to convert list of bitmap to array list*/
    public ArrayList<Bitmap> listToArrayListBitmap(List<Integer> myList) {
        ArrayList<Bitmap> arraylist = new ArrayList<Bitmap>();
        for (int object : myList) {
            arraylist.add(BitmapFactory.decodeResource(getResources(), object));
        }
        return arraylist;
    }
    /* Exposing the api methods to client by defining the methods of aidl file*/
    private final MusicCentralInterface.Stub mBinder = new MusicCentralInterface.Stub() {
        /* method to send all songs information*/
        @Override
        public Bundle getAllSongsInfo() throws RemoteException {
            Log.i(TAG, "getAllSongsInfo: Entered getAllSongsInfo");
            Bundle result = new Bundle();
            synchronized (result) {
                result.putStringArrayList("titles", listToArrayList(songTitles));
                result.putStringArrayList("names", listToArrayList(artistNames));
                Log.i(TAG, "getAllSongsInfo: " + songPicture);
                result.putParcelableArrayList("pictures", listToArrayListBitmap(songPicture));
                result.putStringArrayList("urls", listToArrayList(songURL));
            }
            return result;
        }
        /* method to send a particular song information*/
        @Override
        public Bundle getSongWithId(int id) throws RemoteException {
            Log.i(TAG, "getSongWithId: Entered getSongWithId");
            Bundle result = new Bundle();
            synchronized (result) {
                result.putString("title", listToArrayList(songTitles).get(id));
                result.putString("name", listToArrayList(artistNames).get(id));
                result.putParcelable("picture", listToArrayListBitmap(songPicture).get(id));
                result.putString("url", listToArrayList(songURL).get(id));
            }
            return result;
        }
        /* method to send a particular songs url*/
        @Override
        public String getSongURL(int id) throws RemoteException {
            Log.i(TAG, "getSongURL: Entered getSongUrl");
            return listToArrayList(songURL).get(id);
        }
    };
    /* Method to create the notification channel*/
    private void createNotificationChannel() {
        CharSequence name = "Music Central notification";
        String description = "The channel for music central notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(description);
        }
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.createNotificationChannel();
        PackageManager pm = this.getPackageManager();
        final Intent notificationIntent = pm.getLaunchIntentForPackage("com.example.musicclient_sasanka"); /* on click of notification open the main activity*/
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setOngoing(true).setContentTitle("Music Central Service is Running")
                .setContentText("This is the Music Central Service")
                .setTicker("Music Central Service is Running")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Show service", pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification); /* elevating the service to foreground*/
    }

    /* return the ibinder object onbind*/
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}