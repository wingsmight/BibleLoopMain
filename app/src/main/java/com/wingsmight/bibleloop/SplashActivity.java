package com.wingsmight.bibleloop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity
{
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        final Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();

        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(intent);
            }
        };

        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(handler != null && runnable != null)
        {
            handler.removeCallbacks(runnable);
        }
    }
}