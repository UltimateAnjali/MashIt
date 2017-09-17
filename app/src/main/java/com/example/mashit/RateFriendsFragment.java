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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class RateFriendsFragment extends Fragment {

    ImageView imageView;
    ImageView hot,skip;
    TextView friend;
    UserData userData;
    static String id;
    static int i=0;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_rate_friends, container, false);
        imageView = (ImageView) view.findViewById(R.id.profileImage);
        hot = (ImageView) view.findViewById(R.id.hotImage);
        skip = (ImageView) view.findViewById(R.id.skipImage);
        friend = (TextView)view.findViewById(R.id.friend_name);

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
        return view;
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
                                                                    Glide.with(getContext()).load(Uri.parse(userFriendData.getProfilePicUri())).fitCenter().into(imageView);
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
                                        Glide.with(getContext()).load(R.drawable.nofriendspic).fitCenter().into(imageView);
                                        friend.setText("");
                                        Toast.makeText(getContext(),"No more friends left,Please invite your friends",Toast.LENGTH_SHORT).show();
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