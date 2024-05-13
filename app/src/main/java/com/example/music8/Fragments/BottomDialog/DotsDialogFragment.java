package com.example.music8.Fragments.BottomDialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music8.Fragments.QueueFragment;
import com.example.music8.R;
import com.example.music8.Track.TrackContainer;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class DotsDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    TextView dotsTrackName, addToFavorite, addToPlaylist, addToQueue;
    // TODO: 10/05/2024 finish dialog

    public DotsDialogFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dotsTrackName = view.findViewById(R.id.btm_dialog_track_name);
        addToFavorite = view.findViewById(R.id.btm_dialog_add_favorite);
        addToPlaylist = view.findViewById(R.id.btm_dialog_add_playlist);
        addToQueue = view.findViewById(R.id.btm_dialog_add_queue);

        addToFavorite.setOnClickListener(this::onClick);
        addToPlaylist.setOnClickListener(this::onClick);
        addToQueue.setOnClickListener(this::onClick);

        dotsTrackName.setText( ((TrackContainer) getArguments().getParcelable("track")).getTrack().getName());
    }

    public static DotsDialogFragment newInstance(TrackContainer trackToAdd){
        DotsDialogFragment fragment = new DotsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("track", trackToAdd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dots_dialog, container, false);
        return view;
    }

    public void showPlaylistDialog(){
        PlaylistDialogFragment playlistDialogFragment = PlaylistDialogFragment.newInstance();
        playlistDialogFragment.show(getChildFragmentManager(), "PlaylistDialogFragment");

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btm_dialog_add_playlist){
            showPlaylistDialog();
        } else if (vId == R.id.btm_dialog_add_favorite) {
            Toast.makeText(getActivity(), "add to favorite", Toast.LENGTH_SHORT).show();
        } else if (vId == R.id.btm_dialog_add_queue) {
            QueueFragment.addToQueue((TrackContainer) getArguments().getParcelable("track"));
            this.dismiss();
            Toast.makeText(getActivity(), "Track Added to Queue", Toast.LENGTH_SHORT).show();
        }
    }
}