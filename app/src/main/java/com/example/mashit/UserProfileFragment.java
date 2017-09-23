package com.example.mashit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class UserProfileFragment extends Fragment {

    UserData userData;
    TextView username, hotRate;
    ImageView myImage;
    Fonts myFontType;

    public UserProfileFragment() {

    }

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle!=null)
        {
            userData= (UserData) bundle.getSerializable("userObject");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_profile, container, false);
        username = (TextView)view.findViewById(R.id.user_name);
        myImage = (ImageView)view.findViewById(R.id.user_image);
        hotRate = (TextView)view.findViewById(R.id.hotness_score);

        myFontType = new Fonts(getContext());
        username.setTypeface(myFontType.getCinzelBoldFont());
        hotRate.setTypeface(myFontType.getCourgetteFont());

        username.setText(userData.getName());
        Glide.with(getContext()).load(Uri.parse(userData.getProfilePicUri())).fitCenter().into(myImage);
        hotRate.setText("Hotness score: "+userData.getHotScore());

        return view;
    }
}
