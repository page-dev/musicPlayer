package com.example.music8.Playlist;

public class PlaylistContainer {
    private Playlist playlist;

    public PlaylistContainer(Playlist playlist) {
        this.playlist = playlist;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
