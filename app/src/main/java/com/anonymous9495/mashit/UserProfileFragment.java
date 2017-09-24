package com.anonymous9495.mashit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class UserProfileFragment extends Fragment {

    UserData userData;
    TextView username, hotRate;
    ImageView myImage;
    Fonts myFontType;
    Button userProfile;

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
        userProfile = view.findViewById(R.id.shareButton);
        myFontType = new Fonts(getContext());
        username.setTypeface(myFontType.getCinzelBoldFont());
        hotRate.setTypeface(myFontType.getCourgetteFont());

        username.setText(userData.getName());
        Glide.with(getContext()).load(Uri.parse(userData.getProfilePicUri())).fitCenter().into(myImage);
        hotRate.setText("Hotness score: "+userData.getHotScore());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "My hotness Score: "+userData.getHotScore()+ " Download an app from https://play.google.com/store/apps/details?id=com.staffone.mashit  ";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");

                share.putExtra(Intent.EXTRA_TEXT, message);

                getContext().startActivity(Intent.createChooser(share, "Share it with your friends :)"));
                }

        });
    }
}
