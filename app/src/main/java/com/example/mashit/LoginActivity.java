package com.example.mashit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton;
    TextView textView;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    UserData userData;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
//        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    SharedPreferences sharedpreferences = getSharedPreferences("myRef", Context.MODE_PRIVATE);
                    String fbId = sharedpreferences.getString("fbId","none");
                    if(!fbId.equals("none"))
                    {
                        AddUser(fbId);
                    }else {
                        FirebaseAuth.getInstance().signOut();
                    }
                }else{

                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        loginButton = (LoginButton)findViewById(R.id.fb_login_btn);
        textView = (TextView)findViewById(R.id.login_status_text);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                textView.setText("Login Success\n"+
                        loginResult.getAccessToken().getUserId()+"\n"+
                        loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                handleFacebookAccessToken(loginResult.getAccessToken());
                                // Application code
                                if (response != null) {
                                    userData = new UserData();
                                    object.optString("email");
                                    if (!object.optString("email").equals("")) {
                                        String email = object.optString("email");
                                        userData.setEmailId(email);
                                    }
                                    if (!object.optString("gender").equals("")) {
                                        String gender = object.optString("gender");
                                        userData.setGender(gender);
                                    }
                                    if (!object.optString("name").equals("")) {
                                        userData.setName(object.optString("name"));
                                    }
                                    if(!object.optString("id").equals(""))
                                    {
                                        userData.setFbId(object.optString("id"));
                                        userData.setProfilePicUri("http://graph.facebook.com/"+userData.getFbId()+"/picture?width=9999");
                                    }

                                    SharedPreferences sharedpreferences = getSharedPreferences("myRef", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.clear();
                                    editor.putString("fbId",userData.getFbId());
                                    editor.commit();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                textView.setText("User login cancelled");
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    AddUser(userData.getFbId());
                    Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    System.out.println("------>Authentication failed."+task.getException());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void AddUser(final String fbId){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("HotOrNot").child(fbId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent;
                Bundle bundle = null;

                if(dataSnapshot.exists())
                {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    bundle = new Bundle();
                    bundle.putSerializable("userObject",userData);
                }else {
                    if(userData!=null && userData.getFbId()!=null) {
                        DatabaseReference databaseReferenceTwo = FirebaseDatabase.getInstance().getReference();

                        databaseReferenceTwo.child("HotOrNot").child(userData.getFbId()).setValue(userData);
                        bundle = new Bundle();
                        bundle.putSerializable("userObject", userData);
                    }
                }

                if(bundle!=null) {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
