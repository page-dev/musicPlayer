package com.example.music8.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.music8.Interfaces.OnPlaylistClick;
import com.example.music8.Playlist.AddPlaylistActivity;
import com.example.music8.Playlist.Playlist;
import com.example.music8.Playlist.PlaylistAdapter;
import com.example.music8.Playlist.PlaylistContainer;
import com.example.music8.Playlist.Schema.PlaylistDbHelper;
import com.example.music8.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class PlaylistsFragment extends Fragment implements OnPlaylistClick{
    public ArrayList<PlaylistContainer> allPlaylists = new ArrayList<>();
    RecyclerView playlistsRecyclerView;
    FloatingActionButton addPlaylist;
    private PlaylistDbHelper dbHelper;
    public static PlaylistAdapter playlistAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        playlistsRecyclerView = view.findViewById(R.id.playlist_recyclerview);
        addPlaylist = view.findViewById(R.id.playlist_add_button);
        
        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPlaylistActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "" + allPlaylists.size(), Toast.LENGTH_SHORT).show();
            }
        });

        dbHelper = new PlaylistDbHelper(getContext());
        storeDataInArrays();


//        Recycler init
        playlistAdapter = new PlaylistAdapter(getContext(), allPlaylists, this::onPlaylistClick);
        playlistsRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        playlistsRecyclerView.setAdapter(playlistAdapter);



        return view;
    }

    @Override
    public void onPlaylistClick(PlaylistContainer playlistContainer, int position, Context context) {
        Toast.makeText(context, "go to playlist, make it fragment", Toast.LENGTH_SHORT).show();
    }

    public void storeDataInArrays(){
        Cursor cursor = dbHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                allPlaylists.add(new PlaylistContainer(new Playlist(cursor.getString(1), new ArrayList<>(), R.drawable.playlist_default_cover)));
            }
        }
    }
}