package com.wingsmight.bibleloop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MediaPlayer
{
    private Context context;
    private SeekBar positionBar;
    private TextView elapsedTimeLabel;
    private TextView remainingTimeLabel;
    private int totalTime;
    private int countLooping, maxCountLooping;
    @SuppressLint("HandlerLeak")
    private Handler handler;

    public Button playBtn;
    public android.media.MediaPlayer mp;


    public MediaPlayer(View mediaView, Context context)
    {
        this.context = context;
        elapsedTimeLabel = mediaView.findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = mediaView.findViewById(R.id.remainingTimeLabel);
        positionBar = mediaView.findViewById(R.id.positionBar);
        playBtn = mediaView.findViewById(R.id.playBtn);

        InitializePlayer("default");
    }

    public void StartCurMusic()
    {
        mp.seekTo(0);

        ResumeCurMusic();
    }

    public void StartMusic(int maxCountLooping)
    {
        this.maxCountLooping = maxCountLooping;
        countLooping = 0;

        StartCurMusic();
    }

    public void ResumeCurMusic()
    {
        if(countLooping != maxCountLooping)
        {
            playBtn.setBackgroundResource(R.drawable.pause);
            mp.start();
        }
    }

    public void PauseCurMusic()
    {
        playBtn.setBackgroundResource(R.drawable.play);
        mp.pause();
    }

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public boolean IsPlaying()
    {
        return mp.isPlaying();
    }

    @SuppressLint("HandlerLeak")
    private void InitializePlayer(String filePath)
    {
        // Media Player
        if(filePath.equals("default"))
        {
            mp = android.media.MediaPlayer.create(context, R.raw.null_music);
        }
        else
        {
            mp = android.media.MediaPlayer.create(context, Uri.parse(filePath));
        }

        mp.setLooping(false);
        mp.seekTo(0);
        totalTime = mp.getDuration();

        //Когда конец среагирует это метод:
        mp.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(android.media.MediaPlayer mp)
            {
                if(countLooping == maxCountLooping)
                {
                    Log.e("tag", "Купи мою подписку");
                    playBtn.setBackgroundResource(R.drawable.stop);
                }
                else
                {
                    countLooping++;

                    StartCurMusic();
                }
                mp.seekTo(0);
            }});


        // Position Bar
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        //handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int currentPosition = msg.what;
                // Update positionBar.
                positionBar.setProgress(currentPosition);

                // Update Labels.
                String elapsedTime = createTimeLabel(currentPosition);
                elapsedTimeLabel.setText(elapsedTime);

                String remainingTime = createTimeLabel(totalTime-currentPosition);
                remainingTimeLabel.setText("- " + remainingTime);
            }
        };

        // Thread (Update positionBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
    }

    public void ChangeSong(String filePath, int maxCountLooping)
    {
        mp.stop();

        InitializePlayer(filePath);
        StartMusic(maxCountLooping);
    }
}