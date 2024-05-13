package com.example.music8.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music8.Interfaces.MediaControls;
import com.example.music8.Interfaces.MiniPlayerFragmentListener;
import com.example.music8.Interfaces.OnDotsClick;
import com.example.music8.Interfaces.OnSecondContainerChangeFragment;
import com.example.music8.Interfaces.OnSongClick;
import com.example.music8.Interfaces.QueueFragmentListener;
import com.example.music8.MainActivity;
import com.example.music8.Player.MyMediaPlayer;
import com.example.music8.Queue.QueueAdapter;
import com.example.music8.R;
import com.example.music8.Songs.SongsAdapter;
import com.example.music8.Track.TrackContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class QueueFragment extends Fragment implements OnSongClick, OnDotsClick, MediaControls {
    TextView queueCurrentTime, queueTotalTime;
    SeekBar queueSeekbar;
    ImageView queuePausePlay, queueNextTrack, queuePrevTrack, queueShuffleQueue, queueLoopQueue, queueBack;
    public static RecyclerView queueRecyclerView;
    public static TrackContainer currentTrack;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    public static QueueAdapter queueAdapter;
    LinearLayoutManager layoutManager;
    private OnSecondContainerChangeFragment listener;
    public static ArrayList<TrackContainer> originalQueue = new ArrayList<>(), shuffledQueue = new ArrayList<>();
    private QueueFragmentListener queueFragmentListener;
    private MiniPlayerFragmentListener miniPlayerFragmentListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
//        Register touches only in this fragment, not pass through it
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    return false;
                }
                return true;
            }
        });

        queueCurrentTime = view.findViewById(R.id.queue_current_time);
        queueTotalTime = view.findViewById(R.id.queue_total_time);
        queueSeekbar = view.findViewById(R.id.queue_seek_bar);
        queuePausePlay = view.findViewById(R.id.queue_play_pause);
        queueNextTrack = view.findViewById(R.id.queue_next);
        queuePrevTrack = view.findViewById(R.id.queue_previous);
        queueShuffleQueue = view.findViewById(R.id.queue_shuffle);
        queueLoopQueue = view.findViewById(R.id.queue_loop);
        queueBack = view.findViewById(R.id.queue_back);
        queueRecyclerView = view.findViewById(R.id.queue_recycler_view);
        
        queueBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("QueueFragment");
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_top_to_bottom)
                        .remove(fragment)
                        .commit();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        MainActivity.fragmentContainerView.setLayoutParams(MainActivity.layoutParams);
                    }
                }, 500);
                listener.secondContainerChangeFragment(5);
            }
        });


        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    queueSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                    queueCurrentTime.setText(convertToTimeFormat(String.valueOf(mediaPlayer.getCurrentPosition())));
                    if (mediaPlayer.isPlaying()){
                        queuePausePlay.setImageResource(R.drawable.pause_fill);
                    } else {
                        queuePausePlay.setImageResource(R.drawable.play_fill);
                    }
                }
                handler.postDelayed(this, 100);
            }
        });

        queueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        queueFragmentListener.onQueueFragmentReady();

        MyMediaPlayer.isChanged = false;
        setupButtonRes();
        setupPlayer();

        return view;
    }

    public static ArrayList<TrackContainer> getOriginalQueue() {
        return originalQueue;
    }

    public static void setOriginalQueue(ArrayList<TrackContainer> originalQueue) {
        QueueFragment.originalQueue = originalQueue;
    }

    public static ArrayList<TrackContainer> getShuffledQueue() {
        return shuffledQueue;
    }

    public static void setShuffledQueue(ArrayList<TrackContainer> shuffledQueue) {
        QueueFragment.shuffledQueue = shuffledQueue;
    }

    public static void addToQueue(TrackContainer track){
        originalQueue.add(track);
        shuffledQueue.add(track);
        queueAdapter.notifyDataSetChanged();
    }

    private void recyclerViewInit(ArrayList<TrackContainer> queue){
        layoutManager = new LinearLayoutManager(getContext());
        queueRecyclerView.setLayoutManager(layoutManager);
        queueAdapter = new QueueAdapter(requireContext(), queue, this::onSongClick, this::onDotsClick);
        queueRecyclerView.setAdapter(queueAdapter);
        if (MyMediaPlayer.isTrackClicked){
            layoutManager.scrollToPosition(MyMediaPlayer.clickedTrackPosition);
            MyMediaPlayer.isTrackClicked = false;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSecondContainerChangeFragment) {
            listener = (OnSecondContainerChangeFragment) context;
        }
        if (context instanceof QueueFragmentListener) {
            queueFragmentListener = (QueueFragmentListener) context;
        }
        if (context instanceof MiniPlayerFragmentListener){
            miniPlayerFragmentListener = (MiniPlayerFragmentListener) context;
        }
    }

    @Override
    public void onSongClick(TrackContainer trackContainer, int position, Context context) {
        mediaPlayer.setOnCompletionListener(null);
        MyMediaPlayer.getInstance().reset();
        MyMediaPlayer.currentIndex = position;

        MyMediaPlayer.isTrackClicked2 = true;
//        copied from play next and previous to fix, click on queue, shuffle on, shuffle off
        MyMediaPlayer.isChanged = true;

        mediaPlayer.reset();

        setupPlayer();
    }

    @Override
    public void onDotsClick(TrackContainer trackContainer, int position, Context context, View v) {

    }

    @Override
    public void playTrack() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentTrack.getTrack().getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            queueSeekbar.setProgress(0);
            queueSeekbar.setMax(mediaPlayer.getDuration());
            if (((MiniPlayerFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("MiniPlayerFragment")) != null){
                miniPlayerFragmentListener.onMiniPlayerFragmentReady();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (MyMediaPlayer.currentIndex == originalQueue.size() - 1 && !MyMediaPlayer.isLooping){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        getActivity().getSupportFragmentManager().popBackStack();
                        MyMediaPlayer.isShuffling = false;
                        MyMediaPlayer.isLooping = false;
                        MyMediaPlayer.isChanged = false;
                        MyMediaPlayer.isTrackClicked2 = false;
                    } else {
//                        auto next AND auto next on last song when looping
                        playNextTrack();
                    }
                    queueAdapter.notifyDataSetChanged();
                }

            });
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setupPlayer() {
//        for first setup || isTrackClicked and 2 || next or previous || shuffleClicked     && not shuffling
        if ((currentTrack == null || MyMediaPlayer.isTrackClicked || MyMediaPlayer.isChanged || MyMediaPlayer.isTrackClicked2 || MyMediaPlayer.shuffleClicked)
                && !MyMediaPlayer.isShuffling){
            if (MyMediaPlayer.isTrackClicked){
                MyMediaPlayer.currentIndex = MyMediaPlayer.clickedTrackPosition;
            } else if (MyMediaPlayer.shuffleClicked) {
                MyMediaPlayer.currentIndex = getTrackOriginalPosition(currentTrack);
                MyMediaPlayer.shuffleClicked = false;
            }
//            Toast.makeText(getActivity(), "IF", Toast.LENGTH_SHORT).show();
            currentTrack = originalQueue.get(MyMediaPlayer.currentIndex);
        } else if (MyMediaPlayer.isShuffling){
//            for shuffle one
//            Toast.makeText(getActivity(), "2 IF", Toast.LENGTH_SHORT).show();
            currentTrack = shuffledQueue.get(MyMediaPlayer.currentIndex);
        } 
//        else if (!MyMediaPlayer.isShuffling){
////            for shuffle off
//            Toast.makeText(getActivity(), "3 IF", Toast.LENGTH_SHORT).show();
//            MyMediaPlayer.currentIndex = getTrackOriginalPosition(currentTrack);
//            currentTrack = originalQueue.get(MyMediaPlayer.currentIndex);
//        }

//        for shuffle recyclerinit
        /*
        i dont want to reset recyclerview (refresh view) when:
        clicking play next or previous
        clicking track on the queue (given always as true, use ! so that statement does not execute, which we want)
        isChanged - generally, reset/update/change the recyclerview
         */
        if (!MyMediaPlayer.isChanged){
            if (MyMediaPlayer.isShuffling){
//                Toast.makeText(getActivity(), "recycleview shuffle", Toast.LENGTH_SHORT).show();
                recyclerViewInit(shuffledQueue);
            } else {
//                Toast.makeText(getActivity(), "recyclerview original", Toast.LENGTH_SHORT).show();
                recyclerViewInit(originalQueue);
            }
        }

        setupControls();
        if (!mediaPlayer.isPlaying()) {
//            Toast.makeText(getActivity(), "playTrack", Toast.LENGTH_SHORT).show();
            playTrack();
        }
    }

    @Override
    public void setupControls() {
        queueSeekbar.setProgress(0);
        queueSeekbar.setMax(mediaPlayer.getDuration());
        queueTotalTime.setText(convertToTimeFormat(currentTrack.getTrack().getDuration()));
        queuePausePlay.setOnClickListener(v-> pausePlay());
        queueNextTrack.setOnClickListener(v-> playNextTrack());
        queuePrevTrack.setOnClickListener(v-> playPreviousTrack());
        queueShuffleQueue.setOnClickListener(v-> shuffleQueue());
        queueLoopQueue.setOnClickListener(v-> loopQueue());

        queueAdapter.notifyDataSetChanged();
    }

    @Override
    public void playNextTrack() {
        if (!MyMediaPlayer.isShuffling){
            MyMediaPlayer.isChanged = true;
        }
        if (MyMediaPlayer.currentIndex == originalQueue.size() - 1 && MyMediaPlayer.isLooping){
            MyMediaPlayer.currentIndex = -1;
        } else if (MyMediaPlayer.currentIndex == originalQueue.size() - 1 && !MyMediaPlayer.isLooping){
            Toast.makeText(getActivity(), "No Next Track!", Toast.LENGTH_SHORT).show();
            return;
        }
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setupPlayer();
    }

    @Override
    public void playPreviousTrack() {
        if (!MyMediaPlayer.isShuffling){
            MyMediaPlayer.isChanged = true;
        }
        if (MyMediaPlayer.currentIndex == 0){
            Toast.makeText(getActivity(), "No Previous Track!", Toast.LENGTH_SHORT).show();
            return;
        }
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setupPlayer();
    }

    @Override
    public void pausePlay() {
        // TODO: 08/05/2024 when pausing in miniplayer and click queue, it will restart music 
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    @Override
    public void shuffleQueue() {
        MyMediaPlayer.shuffleClicked = true;
        MyMediaPlayer.isShuffling = !MyMediaPlayer.isShuffling;
        if (MyMediaPlayer.isShuffling){
            queueShuffleQueue.setImageResource(R.drawable.shuffle_fill);
//            Toast.makeText(getActivity(), "Shuffling enabled", Toast.LENGTH_SHORT).show();
            shuffledQueue =  (ArrayList<TrackContainer>) originalQueue.clone();
            swapTracks(0, MyMediaPlayer.currentIndex);
            randomizeTracks();
            MyMediaPlayer.currentIndex = 0;
            layoutManager.scrollToPosition(0);
        } else {
            queueShuffleQueue.setImageResource(R.drawable.shuffle_outline);
//            Toast.makeText(getActivity(), "Shuffling disabled", Toast.LENGTH_SHORT).show();
            shuffledQueue.clear();
        }
        queueAdapter.notifyDataSetChanged();
        MyMediaPlayer.isChanged = false;
        setupPlayer();
    }

    @Override
    public void swapTracks(int i, int j) {
        TrackContainer temp = shuffledQueue.get(i);
        shuffledQueue.set(i, shuffledQueue.get(j));
        shuffledQueue.set(j, temp);
    }

    private void randomizeTracks(){
        Random rand = new Random();
        for (int i = 1; i < shuffledQueue.size(); i++) {
            int j = rand.nextInt(i) + 1;
            swapTracks(i, j);
        }
    }

    @Override
    public void loopQueue() {
        MyMediaPlayer.isLooping = !MyMediaPlayer.isLooping;
        if (MyMediaPlayer.isLooping){
            queueLoopQueue.setImageResource(R.drawable.repeat_fill);
//            Toast.makeText(getActivity(), "Looping enabled", Toast.LENGTH_SHORT).show();
        } else {
            queueLoopQueue.setImageResource(R.drawable.repeat_outline);
//            Toast.makeText(getActivity(), "Looping disabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public String convertToTimeFormat(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    private int getTrackOriginalPosition(TrackContainer trackContainer){
        for (int i = 0; i < originalQueue.size(); i++) {
            if (originalQueue.get(i).getTrack().getName().equals(trackContainer.getTrack().getName())){
               return i;
            }
        }
        return 0;
    }

    private void setupButtonRes(){
        if (MyMediaPlayer.isShuffling){
            queueShuffleQueue.setImageResource(R.drawable.shuffle_fill);
        } else {
            queueShuffleQueue.setImageResource(R.drawable.shuffle_outline);
        }

        if (MyMediaPlayer.isLooping){
            queueLoopQueue.setImageResource(R.drawable.repeat_fill);
        } else {
            queueLoopQueue.setImageResource(R.drawable.repeat_outline);
        }
    }




}