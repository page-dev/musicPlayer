package com.example.music8.Interfaces;

import android.content.Context;
import android.view.View;

import com.example.music8.Track.TrackContainer;

public interface OnDotsClick {
    public void onDotsClick(TrackContainer trackContainer, int position, Context context, View v);
}
