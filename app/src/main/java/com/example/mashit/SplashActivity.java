package com.example.mashit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by anjali desai on 22-09-2017.
 */

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    Fonts myFontType;
    TextView appname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        appname = (TextView)findViewById(R.id.app_name);



        myFontType = new Fonts(getApplicationContext());
        appname.setTypeface(myFontType.getLobsterFont());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
