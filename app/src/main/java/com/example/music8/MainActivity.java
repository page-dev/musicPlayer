package com.example.music8;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.music8.Fragments.MiniPlayerFragment;
import com.example.music8.Fragments.QueueFragment;
import com.example.music8.Fragments.SongsFragment;
import com.example.music8.Interfaces.MiniPlayerFragmentListener;
import com.example.music8.Interfaces.OnSecondContainerChangeFragment;
import com.example.music8.Interfaces.QueueFragmentListener;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnSecondContainerChangeFragment, QueueFragmentListener, MiniPlayerFragmentListener {
    TabLayout tabLayout;
    public static FragmentContainerView fragmentContainerView;
    public static RelativeLayout.LayoutParams layoutParams;
    int selectedTab = 1;

    public static ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;

    // TODO: 09/05/2024 FORMAT TEXT, pili ug chad nga font
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tablayout);
        fragmentContainerView = findViewById(R.id.fragment_container_for_queue);
        viewPager2 = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.setUserInputEnabled(false);

        fragmentContainerView.setOnClickListener(this);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        selectTab(selectedTab);
    }

    private void selectTab(int tabNumber){
        layoutParams = (RelativeLayout.LayoutParams) fragmentContainerView.getLayoutParams();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_bottom_to_top,
                R.anim.exit_top_to_bottom,
                R.anim.enter_bottom_to_top,
                R.anim.exit_top_to_bottom
        );
        switch (tabNumber){
            case 4:
                QueueFragment queueFragment = (QueueFragment) getSupportFragmentManager().findFragmentByTag("QueueFragment");
                if (queueFragment != null && queueFragment.getView() == null){
                    transaction.replace(R.id.fragment_container_for_queue, queueFragment, "QueueFragment");
                } else {
                    transaction.replace(R.id.fragment_container_for_queue, new QueueFragment(), "QueueFragment");
                }
                transaction.addToBackStack(null).commit();

                // TODO: 03/05/2024 fix animation visual bug, now playing appears in top right

                fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                        super.onFragmentViewDestroyed(fm, f);
                        if (f instanceof QueueFragment){
                            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                            fragmentContainerView.setLayoutParams(layoutParams);
                        }
                    }
                }, false);
                break;
            case 5:
                transaction.replace(R.id.fragment_container_for_queue, new MiniPlayerFragment(), "MiniPlayerFragment")
                        .addToBackStack(null)
                        .commit();

                fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                        super.onFragmentViewDestroyed(fm, f);
                        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        fragmentContainerView.setLayoutParams(layoutParams);
                    }
                }, false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_for_queue);
        if (fragment instanceof QueueFragment){
            Handler handler = new Handler();
            handler.removeCallbacksAndMessages(null);
            Fragment queueFragment = getSupportFragmentManager().findFragmentByTag("QueueFragment");
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_top_to_bottom)
                    .remove(queueFragment)
                    .commit();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    MainActivity.fragmentContainerView.setLayoutParams(MainActivity.layoutParams);
                }
            }, 500);
            secondContainerChangeFragment(5);
        } else if (fragment instanceof MiniPlayerFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_container_for_queue){
            secondContainerChangeFragment(4);
        }
    }


    @Override
    public void secondContainerChangeFragment(int tabnumber) {
        if (fragmentContainerView.getVisibility() == View.GONE){
            fragmentContainerView.setVisibility(View.VISIBLE);
        }
        selectTab(tabnumber);
    }

    @Override
    public void onQueueFragmentReady() {
        ((QueueFragment) getSupportFragmentManager().findFragmentByTag("QueueFragment")).setOriginalQueue(SongsFragment.allTracks);
    }

    @Override
    public void onMiniPlayerFragmentReady() {
        ((MiniPlayerFragment) getSupportFragmentManager().findFragmentByTag("MiniPlayerFragment")).showCurrentPlayingTrack(QueueFragment.currentTrack);
    }
}