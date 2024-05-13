package com.example.music8.Player;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    public static MediaPlayer instance;
    public static int currentIndex = -1, clickedTrackPosition;
    public static boolean
            isLooping = false,
            isShuffling = false,
            isTrackClicked = false,
            isChanged = false,
            isTrackClicked2 = false,
            shuffleClicked = false;

    public static MediaPlayer getInstance(){
        if (instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }
}
