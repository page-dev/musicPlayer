package com.example.music8.Fragments.BottomDialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.music8.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class PlaylistDialogFragment extends BottomSheetDialogFragment {
    Button playlistDialogSave;

    public static PlaylistDialogFragment newInstance(){
        PlaylistDialogFragment fragment =new PlaylistDialogFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_dialog, container, false);

        playlistDialogSave = view.findViewById(R.id.playlist_dialog_save);
        
        playlistDialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "same add sa add button sa playlist tab", Toast.LENGTH_SHORT).show();
                dismiss();
                // TODO: 11/05/2024 on click, dismiss all the dialogs
            }
        });

        return view;
    }
}