package com.example.musicclient_sasanka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music_common.MusicCentralInterface;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected static final String TAG = "MainActivity";
    private boolean mIsBound = false; /* Boolean to store the status of the service connection*/
    TextView serviceStatus; /* Text View to show the status of service connection*/
    TextView headingTitle; /* static text view for heading*/
    TextView headingTitle2; /* static text view for heading*/
    TextView songInfoText; /* /* static text view for showing the information about the song*/
    EditText songIdText; /* Edit text to take the input from the user*/
    Button bindService; /* Button to call bindservice*/
    Button unBindService; /* Button to call unbindservice*/
    Button showAllSongs; /* Button to open recycler view activity*/
    RadioGroup songGroup; /* Radio Group UI element*/
    public static MediaPlayer mediaPlayer; /* Static media player */
    public static ArrayList<String> titlesList; /* to store all the titles of music*/
    public static ArrayList<String> artistList; /* to store all artist names*/
    public static ArrayList<Bitmap> pictureList; /* to store bitmap of pictures*/
    public static ArrayList<String> songsUrls; /* to store urls of song*/
    private MusicCentralInterface MusicCentralService; /* aidl reference*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Defining the UI elements based on the resourcefiles*/
        serviceStatus = findViewById(R.id.textView);
        bindService = findViewById(R.id.button);
        unBindService = findViewById(R.id.button2);
        unBindService.setEnabled(mIsBound);
        showAllSongs = findViewById(R.id.button3);
        showAllSongs.setEnabled(mIsBound);
        serviceStatus.setText("Service Not Binded"); /* Intially the service is not bound*/
        songGroup = findViewById(R.id.radioGroup2);
        songGroup.setEnabled(mIsBound); /* when service not bound disable the UI elements*/
        headingTitle = findViewById(R.id.headingtitle);
        headingTitle.setVisibility(View.INVISIBLE);
        headingTitle2 = findViewById(R.id.headingtitle2);
        headingTitle2.setVisibility(View.INVISIBLE);
        songInfoText = findViewById(R.id.songInfo);
        songInfoText.setVisibility(View.INVISIBLE);
        songIdText = findViewById(R.id.songIdText);
        songIdText.setVisibility(View.INVISIBLE);
        bindService.setOnClickListener(v -> {
            try {
                bindToService(); /* Method to start bind service*/
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        unBindService.setOnClickListener(v -> unBindFromService()); /* Method to unbind from service*/
        showAllSongs.setOnClickListener(v -> {
            try {
                getAllSongsInformationAndStartSongsActivity(); /* method to start recylerviewactivity*/
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        /* Default intialisation to empty*/
        titlesList = new ArrayList<String>();
        artistList = new ArrayList<String>();
        pictureList = new ArrayList<Bitmap>();
        songsUrls = new ArrayList<String>();
        /* Listening to onenter of a edit text and show the info about the song*/
        songIdText.setOnEditorActionListener((v, actionId, event) ->
        {
            int id = Integer.parseInt(songIdText.getText().toString()); /* Take the user input*/
            if (id < titlesList.size()) {
                try {
                    Bundle songInfo = MusicCentralService.getSongWithId(id); /* Call the service to fetch songinfo with id of song*/
                    String result = "Song Name: " + songInfo.getString("title") + "\n \n Artist Name: " + songInfo.getString("name");
                    songInfoText.setText(result); /* Set the info about the song*/
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please Enter a Valid Song ID between 0 and " + (titlesList.size() - 1), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }


    private final ServiceConnection mConnection = new ServiceConnection() {
        /* This method gets called when the service connection is successfull*/
        @Override
        public void onServiceConnected(ComponentName className, IBinder iservice) {
            Log.i(TAG, "onServiceConnected: The service is connected");
            MusicCentralService = MusicCentralInterface.Stub.asInterface(iservice);
            mIsBound = true; /* make the boolean true once the service is bounded*/
            try {
                thingsToDoOnServiceBind(); /* call method to do post binding actions*/
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /* The below method gets called when the service crashes*/
        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "onServiceDisconnected: DIsconnected");
            MusicCentralService = null;
            mIsBound = false;
            thingsToDoOnServiceUnbind();
        }
    };

    /*Method to call Startforegroundservice and bindservice to the music central service*/
    public void bindToService() throws RemoteException {
        if (!mIsBound) {
            Intent i = new Intent(MusicCentralInterface.class.getName());
            ResolveInfo info = getPackageManager().resolveService(i, 0);
            i.setComponent(new ComponentName("com.example.musiccentral_sasanka", "com.example.musiccentral_sasanka.MusicCentralService"));
            /*i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));*/
            startForegroundService(i);
            bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /* Method to unbind from the service*/
    public void unBindFromService() {
        if (mIsBound) {
            unbindService(mConnection);
            serviceStatus.setText("Service Not Binded");
            mIsBound = false;
            thingsToDoOnServiceUnbind();/* Method call to perform actions post unbind*/
        }
    }

    /* Method to enable all the UI elements on bind*/
    public void thingsToDoOnServiceBind() throws RemoteException {
        if (mIsBound) {
            serviceStatus.setText("Service Binded");
            unBindService.setEnabled(mIsBound);
            showAllSongs.setEnabled(mIsBound);
            headingTitle.setVisibility(View.VISIBLE);
            songIdText.setVisibility(View.VISIBLE);
            songInfoText.setVisibility(View.VISIBLE);
            headingTitle2.setVisibility(View.VISIBLE);
            getAllSongInfo(0);
        }
    }

    /* Method to disable certain UI elements on Unbind*/
    public void thingsToDoOnServiceUnbind() {
        if (!mIsBound) {
            serviceStatus.setText("Service Not Binded");
            unBindService.setEnabled(mIsBound);
            showAllSongs.setEnabled(mIsBound);
            headingTitle.setVisibility(View.INVISIBLE);
            songIdText.setVisibility(View.INVISIBLE);
            songInfoText.setVisibility(View.INVISIBLE);
            headingTitle2.setVisibility(View.INVISIBLE);
            songInfoText.setText("");
            songIdText.setText("");
            songGroup.removeAllViews();
            Intent i = new Intent(MusicCentralInterface.class.getName());
            ResolveInfo info = getPackageManager().resolveService(i, 0);
            i.setComponent(new ComponentName("com.example.musiccentral_sasanka", "com.example.musiccentral_sasanka.MusicCentralService"));
            stopService(i); /* Stop the service on unbind*/
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            titlesList = new ArrayList<String>();
            artistList = new ArrayList<String>();
            pictureList = new ArrayList<Bitmap>();
            songsUrls = new ArrayList<String>();
        }
    }

    /* Get all songs information from the service and store it*/
    public void getAllSongInfo(int flag) throws RemoteException {
        Bundle info = MusicCentralService.getAllSongsInfo();
        titlesList = info.getStringArrayList("titles");
        artistList = info.getStringArrayList("names");
        pictureList = info.getParcelableArrayList("pictures");
        songsUrls = info.getStringArrayList("urls");
        if (flag == 0) {
            addRadioButtons(titlesList); /* add radiobuttons to the UI*/
        }
    }

    /* Start the Recycler View activity*/
    public void getAllSongsInformationAndStartSongsActivity() throws RemoteException {
        getAllSongInfo(1);
        if (mIsBound) {
            Intent intent = new Intent(this, RecyclerViewActivity.class);
            Log.i(TAG, "getAllSongsInformationAndStartSongsActivity: " + MusicCentralService.getAllSongsInfo().toString());
            startActivity(intent);
        }
    }

    /* Method to addradioibuttton and listen on their select*/
    public void addRadioButtons(ArrayList<String> titlesList) throws RemoteException {
        for (int i = 0; i < titlesList.size(); i++) {
            RadioButton radio = new RadioButton(this);
            Bundle songTitle = MusicCentralService.getSongWithId(i);
            radio.setText(songTitle.getString("title"));
            radio.setId(View.generateViewId());
            radio.setText(titlesList.get(i));
            songGroup.addView(radio);
        }
        RadioButton radio = new RadioButton(this);
        radio.setId(View.generateViewId());
        radio.setText("Stop Song");
        songGroup.addView(radio);
        songGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            int k = 0;
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if (radioGroup.getChildAt(i).getId() == id) {
                    k = i;
                    Log.i(TAG, "onCheckedChanged: hwllo" + k);
                    if (id <= titlesList.size()) {
                        Bundle songInfo = null;
                        try {
                            songInfo = MusicCentralService.getSongWithId(id - 1);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        String result = "Song Name: " + songInfo.getString("title") + "\n \n Artist Name: " + songInfo.getString("name");
                        songInfoText.setText(result);
                        songIdText.setText(String.valueOf(i));
                    } else {
                        songInfoText.setText("");
                        songIdText.setText("");
                    }
                    break;
                }
            }
            try {
                playSong(k);
            } catch (RemoteException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    /* playing the song at the passed index using mediaplayer*/
    public void playSong(int index) throws RemoteException, IOException {
        if (index < songsUrls.size()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            String url = MusicCentralService.getSongURL(index); /* get the songurl from the music central service*/
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
        } else {
            mediaPlayer.stop();
        }
    }

    /* Ondestroy if the song is playing stop it*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}