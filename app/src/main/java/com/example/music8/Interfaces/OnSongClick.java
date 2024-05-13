package com.example.music8.Interfaces;

import android.content.Context;

import com.example.music8.Track.TrackContainer;

public interface OnSongClick {
    public void onSongClick(TrackContainer trackContainer, int position, Context context);
}
