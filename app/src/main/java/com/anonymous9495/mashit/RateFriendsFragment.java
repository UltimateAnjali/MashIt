package com.anonymous9495.mashit;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RateFriendsFragment extends Fragment {

    ImageView imageView;
    ImageView hot,skip;
    TextView friend, hotText, skipText;
    UserData userData;
    static String id;
    static int i=0;
    ImageButton imghot,imgskip;
    Fonts myFontType;
    ProgressBar progressBar;
    Button invite;
    String applink="https://play.google.com/store/apps/details?id=com.staffone.mashit";

    public RateFriendsFragment() {
    }

    public static RateFriendsFragment newInstance() {
        RateFriendsFragment fragment = new RateFriendsFragment();
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

        getFriends();
    }

    @Override
    public void onResume() {
        super.onResume();
        i=0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_rate_friends, container, false);
        imageView = (ImageView) view.findViewById(R.id.profileImage);
        friend = (TextView)view.findViewById(R.id.friend_name);
        imghot = (ImageButton)view.findViewById(R.id.hot_round_btn);
        imgskip = (ImageButton)view.findViewById(R.id.skip_round_btn);
        hotText = (TextView)view.findViewById(R.id.textHot);
        skipText = (TextView)view.findViewById(R.id.textSkip);
        invite = (Button)view.findViewById(R.id.inviteFriendsBtn);

        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar1);

        myFontType = new Fonts(getContext());
        friend.setTypeface(myFontType.getCinzelBoldFont());
        hotText.setTypeface(myFontType.getCourgetteFont());
        skipText.setTypeface(myFontType.getCourgetteFont());

        imageView.setVisibility(View.VISIBLE);

        imghot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(id!=null)
                {
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("HotOrNot").child(id).child("hotScore").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshotScore) {

                            final DatabaseReference newOne = FirebaseDatabase.getInstance().getReference();

                            newOne.child("HotOrNot").child("UserViewedList").child(userData.getFbId()).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()) {
                                        if (dataSnapshotScore != null) {
                                            int temp = dataSnapshotScore.getValue(Integer.class);
                                            dataSnapshotScore.getRef().setValue(temp + 1);
                                            newOne.child("HotOrNot").child("UserViewedList").child(userData.getFbId()).child(id).getRef().setValue(true);

                                            SendNotification(id);
                                        }
                                        i++;
                                        getFriends();
                                    }else{
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        imgskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference newOne = FirebaseDatabase.getInstance().getReference();
                newOne.child("HotOrNot").child("UserViewedList").child(userData.getFbId()).child(id).getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        i++;
                        getFriends();
                    }
                });
            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShareDialog.canShow(ShareLinkContent.class)){
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(applink))
                            .build();
                    ShareDialog.show(getActivity(),linkContent);
                }
                else {
                    Toast.makeText(getActivity(),"Unable to share.. Try Again.. ",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void SendNotification(String id) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        HttpURLConnection urlConnection;
        JSONObject json = new JSONObject();
        JSONObject info = new JSONObject();
        try {
            info.put("title", "Hotness Score");   // Notification title
            info.put("body", "You were rated hot by your friend"); // Notification body
            info.put("sound", "default"); // Notification sound
            json.put("notification", info);
            json.put("to", "/topics/"+id);
        }catch (Exception ex)
        {

        }

        Log.e("jsonn==> ",json.toString());
        String data = json.toString();
        String result = "kaipan";
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL("https://fcm.googleapis.com/fcm/send").openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "key=AIzaSyD6z1BkQnkUsd9X9GL0NghJyKqxqZ5SHZg");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       // return result;

    }

    private void getFriends() {
        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(
                                    JSONArray jsonArray,
                                    GraphResponse response) {
                                // Application code for users friends
                                System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);
                                System.out.println("getFriendsData onCompleted : response " + response);
                                try {
                                    //  JSONObject json = java.util.Map(response);
                                    if(i<jsonArray.length())
                                    {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String name = jsonObject.getString("name");
                                        id = jsonObject.getString("id");
                                        System.out.println("---> name="+name+" id="+id);
                                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.child("HotOrNot").child("UserViewedList").child(userData.getFbId()).child(id).addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshotOne) {
                                                if(!dataSnapshotOne.exists())
                                                {
                                                    imghot.setVisibility(View.VISIBLE);
                                                    imgskip.setVisibility(View.VISIBLE);
                                                    imageView.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    invite.setVisibility(View.GONE);
                                                    final DatabaseReference newOne = FirebaseDatabase.getInstance().getReference();
                                                    newOne.child("HotOrNot").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                UserData userFriendData = dataSnapshot.getValue(UserData.class);

                                                                if(userFriendData!=null && userFriendData.getProfilePicUri()!=null && !(userFriendData.getGender().equals(userData.getGender())))
                                                                {
                                                                    Glide.with(getContext())
                                                                            .load(Uri.parse(userFriendData.getProfilePicUri()))
                                                                            .listener(new RequestListener<Uri, GlideDrawable>() {
                                                                                @Override
                                                                                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                                                    progressBar.setVisibility(View.GONE);
                                                                                    return false;
                                                                                }

                                                                                @Override
                                                                                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                                                    progressBar.setVisibility(View.GONE);
                                                                                    return false;
                                                                                }
                                                                            })
                                                                            .fitCenter()
                                                                            .into(imageView);
                                                                    friend.setText(userFriendData.getName());

                                                                }else {
                                                                    i++;
                                                                    getFriends();
                                                                }

                                                            }else{
                                                                i++;
                                                                getFriends();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });


                                                }else {
                                                    i++;
                                                    getFriends();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }else{
                                        invite.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        imageView.setVisibility(View.GONE);
                                        friend.setText("");
                                        hotText.setText("");
                                        skipText.setText("");
                                        Toast.makeText(getContext(),"No more friends left,Please invite your friends",Toast.LENGTH_LONG).show();
                                        imghot.setVisibility(View.GONE);
                                        imgskip.setVisibility(View.GONE);
                                        //imghot.setEnabled(false);
                                        //imgskip.setEnabled(false);
                                    }
//                                    JSONObject data = jsonObject.getJSONObject("data");
//                                    System.out.println("----data"+data);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })

        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
            }
        });
        batch.executeAsync();

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");
    }
}
