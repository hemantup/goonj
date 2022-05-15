package com.hemant.music.goonj;

import static com.hemant.music.goonj.MainActivity.songList;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Player extends AppCompatActivity {

    TextView musicTitle, artist, playedTime, totalTime;
    ImageView backBtn, hamburger, previousBtn, nextBtn, shuffleBtn,repeatBtn, playPauseBtn;
    SeekBar seekbar;
    int position;
    static ArrayList<AudioModel> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        getIntentMethod();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekbar.setProgress(currentPosition);
                    playedTime.setText(formattedTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    static String formattedTime(int currentPosition){
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if(seconds.length() == 1){
            return totalNew;
        }else{
            return totalOut;
        }

    }

    private void getIntentMethod(){
        position = getIntent().getIntExtra("position",-1);
        listSongs = songList;
        if(listSongs != null){
            playPauseBtn.setImageResource(R.drawable.ic_circle_pause_solid);
            uri = Uri.parse(listSongs.get(position).getPath());
        }

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            totalTime.setText(formattedTime(mediaPlayer.getDuration() / 1000));
        }else{
             mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
             mediaPlayer.start();
             totalTime.setText(formattedTime(mediaPlayer.getDuration() / 1000));
        }
        seekbar.setMax(mediaPlayer.getDuration() / 1000);
    }

    private void initViews(){
        musicTitle = findViewById(R.id.titleMusic);
        artist = findViewById(R.id.artistMusic);
        playedTime = findViewById(R.id.playedTime);
        totalTime = findViewById(R.id.totalTime);
        backBtn = findViewById(R.id.back);
        hamburger = findViewById(R.id.hamburger);
        nextBtn = findViewById(R.id.musicForward);
        previousBtn = findViewById(R.id.musicBackward);
        shuffleBtn = findViewById(R.id.shuffle);
        repeatBtn = findViewById(R.id.musicRepeat);
        playPauseBtn = findViewById(R.id.playAndPause);
        seekbar = findViewById(R.id.musicSeekbar);
    }
}