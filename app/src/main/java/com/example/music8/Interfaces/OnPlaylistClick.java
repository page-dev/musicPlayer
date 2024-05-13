package com.example.music8.Interfaces;

import android.content.Context;

import com.example.music8.Playlist.PlaylistContainer;

public interface OnPlaylistClick {
    public void onPlaylistClick(PlaylistContainer playlistContainer, int position, Context context);
}
