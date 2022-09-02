package com.example.music_common;
interface MusicCentralInterface {
    Bundle getAllSongsInfo();
    Bundle getSongWithId(int id);
    String getSongURL(int id);
}