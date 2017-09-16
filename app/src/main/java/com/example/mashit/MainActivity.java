package com.example.mashit;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    private AdView mAdView;
    ImageView imageView;
    ImageView hot,skip;
    UserData userData;
    static String id;
    static int i=0;
    TextView friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        imageView = (ImageView) findViewById(R.id.profileImage);
        mAdView = (AdView) findViewById(R.id.adView);
        hot = (ImageView) findViewById(R.id.hotImage);
        skip = (ImageView) findViewById(R.id.skipImage);
        friend = (TextView)findViewById(R.id.friend_name);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.profile:
                                Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                                startActivity(intent);

                        }
                        return true;
                    }
                });

       /* Glide.with(getApplicationContext())
                .load("http://graph.facebook.com/1560580777339457/picture?width=9999")
                .fitCenter()
                .into(imageView);*/

//        Glide.with(getApplicationContext()).load("http://graph.facebook.com/1560580777339457/picture?width=9999").fitCenter().into(imageView);
        Bundle bundle = this.getIntent().getExtras();

        if(bundle!=null)
        {
            userData= (UserData) bundle.getSerializable("userObject");

        }
        getFriends();
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                                   final DatabaseReference newOne = FirebaseDatabase.getInstance().getReference();
                                                   newOne.child("HotOrNot").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                                           if(dataSnapshot.exists()){
                                                               UserData userFriendData = dataSnapshot.getValue(UserData.class);

                                                               if(userFriendData!=null && userFriendData.getProfilePicUri()!=null && !(userFriendData.getGender().equals(userData.getGender())))
                                                               {
                                                                   Glide.with(getApplicationContext()).load(Uri.parse(userFriendData.getProfilePicUri())).fitCenter().into(imageView);
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
                                       Glide.with(getApplicationContext()).load(R.drawable.nofriendspic).fitCenter().into(imageView);
                                       friend.setText("");
                                       Toast.makeText(getApplicationContext(),"No more friends left,Please invite your friends",Toast.LENGTH_SHORT).show();
                                       hot.setEnabled(false);
                                       skip.setEnabled(false);
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
