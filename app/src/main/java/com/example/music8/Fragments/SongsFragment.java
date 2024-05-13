package com.example.music8.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.music8.Fragments.BottomDialog.DotsDialogFragment;
import com.example.music8.Interfaces.OnDotsClick;
import com.example.music8.Interfaces.OnSecondContainerChangeFragment;
import com.example.music8.Interfaces.OnSongClick;
import com.example.music8.Player.MyMediaPlayer;
import com.example.music8.R;
import com.example.music8.Utils.Sort;
import com.example.music8.Songs.SongsAdapter;
import com.example.music8.Track.Track;
import com.example.music8.Track.TrackContainer;

import java.io.File;
import java.util.ArrayList;


public class SongsFragment extends Fragment implements OnSongClick, OnDotsClick{
    public static ArrayList<TrackContainer> allTracks =new ArrayList<>();
    RecyclerView allSongsRecyclerView;
    private OnSecondContainerChangeFragment listener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSongs();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        allSongsRecyclerView = view.findViewById(R.id.all_songs_recycler_view);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerViewInit();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context != null){
            listener = (OnSecondContainerChangeFragment) context;
        }
    }

    private void recyclerViewInit(){
        Sort.sortObjects(allTracks, new Track.NameComparator());
        allSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allSongsRecyclerView.setAdapter(new SongsAdapter(requireContext(), allTracks, this::onSongClick, this::onDotsClick));
    }

    @Override
    public void onSongClick(TrackContainer trackContainer, int position, Context context) {
        MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
        mediaPlayer.setOnCompletionListener(null);
        MyMediaPlayer.getInstance().reset();
        MyMediaPlayer.currentIndex = position;

        MyMediaPlayer.isTrackClicked = true;
        MyMediaPlayer.clickedTrackPosition = position;
        MyMediaPlayer.isShuffling = false;
        MyMediaPlayer.isLooping = false;
        MyMediaPlayer.isChanged = false;
        MyMediaPlayer.isTrackClicked2 = false;

        listener.secondContainerChangeFragment(4);
    }

    @Override
    public void onDotsClick(TrackContainer trackContainer, int position, Context context, View v) {
//        showPopMenu(v);
        DotsDialogFragment dotsDialogFragment = new DotsDialogFragment().newInstance(trackContainer);
        dotsDialogFragment.show(getParentFragmentManager(), "DotsDialogFragment");
    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(getActivity(), "READ PERMISSION IS REQUIRED, PLEASE ALLOW FROM SETTINGS", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        }
    }

    public void initSongs(){
        if (!checkPermission()){
            requestPermission();
        }
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = requireContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        while (cursor.moveToNext()){
            TrackContainer trackContainer = new TrackContainer(new Track(
                    // TODO: 24/04/2024 if there are not arist or album (index 1 and 2), set it to empty string
                    cursor.getString(0),
                    cursor.getString(1) == null || cursor.getString(1).isEmpty() || cursor.getString(1).equals("<unknown>") ? "" : cursor.getString(1),
                    cursor.getString(2) == null || cursor.getString(2).isEmpty() || cursor.getString(2).equals("Music")? "" : cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    R.drawable.music
            ));
            if (new File(trackContainer.getTrack().getPath()).exists()){
                allTracks.add(trackContainer);
            }
        }
        if (allTracks.isEmpty()){
            Toast.makeText(requireContext(), "No Songs!", Toast.LENGTH_SHORT).show();
        }
    }



}