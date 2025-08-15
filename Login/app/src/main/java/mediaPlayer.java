package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

public class mediaPlayer extends AppCompatActivity {

    private ImageView imgplaypause;
    private TextView textcurrentTime,totalduration;
    private SeekBar playerseekbar;
    MediaPlayer mediaplayer;
    String audioUrl="";

    private Handler handler=new Handler();

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        imgplaypause=findViewById(R.id.ImgPlayPause);
        textcurrentTime=findViewById(R.id.textcurrenttime);
        totalduration=findViewById(R.id.Totalduration);
        playerseekbar=findViewById(R.id.playerseekbar);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            audioUrl= bundle.getString("audio");
        }
        mediaplayer=new MediaPlayer();
        playerseekbar.setMax(100);

        imgplaypause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaplayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaplayer.pause();
                    imgplaypause.setImageResource(R.drawable.mplay);
                }else {
                    mediaplayer.start();
                    imgplaypause.setImageResource(R.drawable.mpause);
                    updateSeekBar();

                }
            }
        });
        preparedMediaPlayer();
        playerseekbar.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekBar=(SeekBar) v;
                int PlayPosition=(mediaplayer.getDuration()/100)*seekBar.getProgress();
                mediaplayer.seekTo(PlayPosition);
                textcurrentTime.setText(milliSecondsToTimer(mediaplayer.getCurrentPosition()));
                return false;
            }
        });
        mediaplayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playerseekbar.setSecondaryProgress(percent);
            }
        });
        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerseekbar.setProgress(0);
                imgplaypause.setImageResource(R.drawable.mplay);
                textcurrentTime.setText(R.string.zero);
                totalduration.setText(R.string.zero);
                mediaplayer.reset();
                preparedMediaPlayer();
            }
        });
    }

    private void preparedMediaPlayer(){

        try {
            mediaplayer.setDataSource(audioUrl);
            mediaplayer.prepare();
            totalduration.setText(milliSecondsToTimer(mediaplayer.getDuration()));
        }catch (Exception e){
            Toast.makeText(mediaPlayer.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
    private Runnable updater=new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration=mediaplayer.getCurrentPosition();
            textcurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };
    private void updateSeekBar(){
        if(mediaplayer.isPlaying()){
            playerseekbar.setProgress((int) (((float)mediaplayer.getCurrentPosition()/ mediaplayer.getDuration()) * 100));
            handler.postDelayed(updater,1000);
        }
    }



    private String milliSecondsToTimer(long milliseconds)
    {
        String timerString="";
        String secondString;

        int hours=(int)(milliseconds/(1000*60*60));
        int minutes=(int)(milliseconds%(1000*60*60)) / (1000*60);
        int seconds=(int)(milliseconds%(1000*60*60)) %(1000*60) / 1000;
        if(hours>0){
            timerString=hours+":";
        }
        if(seconds<10)
        {
            secondString="0"+seconds;
        }else{
            secondString=""+seconds;
        }
        timerString=timerString+minutes+":"+secondString;
        return timerString;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaplayer != null) {
            mediaplayer.stop();
        }
    }
}