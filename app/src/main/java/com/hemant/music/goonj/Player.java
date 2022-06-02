package com.hemant.music.goonj;

import static com.hemant.music.goonj.MainActivity.songList;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Player extends AppCompatActivity {

    TextView musicTitle, artist, playedTime, totalTime;
    ImageView backBtn, hamburger, previousBtn, nextBtn, shuffleBtn,repeatBtn, playPauseBtn,musicArt,favIcon;
    SeekBar seekbar;
    int position = 0, musicProgress = 0;
    Boolean repeatFlag = false, randomFlag = false, favFlag=false;
    static ArrayList<AudioModel> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private Thread playThread, previousThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        getIntentMethod();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicProgress = progress;
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
                if(formattedTime(mediaPlayer.getDuration() / 1000)
                        .equals(formattedTime(mediaPlayer.getCurrentPosition()/1000))){

                        if(repeatFlag == true) {
                            Log.d("Repeat flag true", "onProgressChanged: ");
                            repeatSong();
                        }
                        else if(randomFlag == true){
                            Log.d("random flag true", "onProgressChanged: ");
                            position = getRandomNumber();
                            playRandomSong(position);
                        }
                        else{
                            nextBtnClicked();
                        }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((!repeatFlag) && (randomFlag = true)) || (randomFlag = false)){
                    repeatFlag = true;
                    randomFlag = false;
                    shuffleBtn.setColorFilter(R.color.black);
                    repeatBtn.setColorFilter(R.color.purple_700);
                }else{
                    repeatFlag = false;
                    repeatBtn.setColorFilter(R.color.black);
                }
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((!randomFlag) && (repeatFlag = true)) || (repeatFlag = false)){
                    randomFlag = true;
                    repeatFlag = false;
                    shuffleBtn.setColorFilter(R.color.purple_700);
                    repeatBtn.setColorFilter(R.color.black);
                }else{
                    randomFlag = false;
                    shuffleBtn.setColorFilter(R.color.black);
                }
            }
        });
        
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favFlag=false){
                    favFlag=true;
                    addFavSong(mediaPlayer);
                }else{
                    favFlag=false;
                    removeFavSong(mediaPlayer);
                }
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
                handler.postDelayed(this, 500);
            }
        });

    }

    private void removeFavSong(MediaPlayer mediaPlayer) {
    }

    private void addFavSong(MediaPlayer mediaPlayer) {
        
    }

    private void playRandomSong(int position) {
        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekbar.setProgress(currentPosition);
                    playedTime.setText(formattedTime(currentPosition));
                }
                handler.postDelayed(this, 500);
            }
        });
        seekbar.setMax(mediaPlayer.getDuration() / 1000);
        playPauseBtn.setImageResource(R.drawable.ic_circle_pause_solid);
        mediaPlayer.start();
    }

    public int getRandomNumber() {
        return (int) ((Math.random() * (listSongs.size() - 0)) + 0);
    }

    private void repeatSong() {
        mediaPlayer.seekTo(0);
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        previousThreadBtn();
        super.onResume();
    }

    private void previousThreadBtn() {
        previousThread = new Thread(){
            @Override
            public void run() {
                super.run();
                previousBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previousBtnClicked();
                    }
                });
            }
        };
        previousThread.start();
    }

    private void previousBtnClicked() {

            mediaPlayer.stop();
            mediaPlayer.release();
            if(position == 0) {
                position = listSongs.size() - 1;
            }
            else {
                position = position - 1;
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(currentPosition);
                        playedTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 500);
                }
            });
            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            playPauseBtn.setImageResource(R.drawable.ic_circle_pause_solid);
            mediaPlayer.start();

    }

    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {

            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % listSongs.size());
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(currentPosition);
                        playedTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 500);
                }
            });
            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            playPauseBtn.setImageResource(R.drawable.ic_circle_pause_solid);
            mediaPlayer.start();

    }

    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.ic_play_solid);
            mediaPlayer.pause();
            seekbar.setMax(mediaPlayer.getDuration() / 1000);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(currentPosition);
                        playedTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 500);
                }
            });
        }
        else{
            playPauseBtn.setImageResource(R.drawable.ic_circle_pause_solid);
            mediaPlayer.start();
            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(currentPosition);
                        playedTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 500);
                }
            });
        }
    }

    static String formattedTime(int currentPosition){
        String totalOut;
        String totalNew;
        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":0" + seconds;
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
            uri = Uri.parse(listSongs.get(position).getPath());
        }

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        totalTime.setText(formattedTime(mediaPlayer.getDuration() / 1000));
        seekbar.setMax(mediaPlayer.getDuration() / 1000);
        // for cover art
        metaData(uri);
        mediaPlayer.start();
    }


    private void initViews(){
        musicTitle = findViewById(R.id.musicTitle);
        artist = findViewById(R.id.musicArtist);
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
        musicArt = findViewById(R.id.musicArt);
        favIcon = findViewById(R.id.favIcon);
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri.toString());

        musicTitle.setText(songList.get(position).getTitle());
        artist.setText(songList.get(position).getArtist());
        totalTime.setText(formattedTime(mediaPlayer.getDuration() / 1000));

        byte[] art = mediaMetadataRetriever.getEmbeddedPicture();
        if (art != null){
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(musicArt);
        }
        else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.ic_launcher_foreground)
                    .into(musicArt);
        }

    }
}