package com.example.mashit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by anjali desai on 22-09-2017.
 */

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Fonts myFontType;
    TextView appname;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
//        FirebaseAuth.getInstance().signOut();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        appname = (TextView)findViewById(R.id.app_name);



        myFontType = new Fonts(getApplicationContext());
        appname.setTypeface(myFontType.getLobsterFont());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    SharedPreferences sharedpreferences = getSharedPreferences("myRef", Context.MODE_PRIVATE);
                    String fbId = sharedpreferences.getString("fbId","none");
                    if(!fbId.equals("none"))
                    {
                        LoginActivity loginActivity = new LoginActivity();
                        loginActivity.AddUser(fbId,getApplicationContext());
                    }else {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
