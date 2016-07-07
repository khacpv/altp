package com.example.gcs.faster5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;



/**
 * Created by Kien on 07/05/2016.
 */
public class LoginScreen extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    public String idUserFB, nameUserFB;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    public Intent startActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login_screen);

        startActivity = new Intent(LoginScreen.this, InfoScreen.class);
        disconnectFromFacebook();
        loginButton = (LoginButton) findViewById(R.id.fblogin_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginButton.setVisibility(View.INVISIBLE);
                        AccessToken accessToken = loginResult.getAccessToken();
                        idUserFB = accessToken.getUserId();
                        if (Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    mProfileTracker.stopTracking();
                                    Profile.setCurrentProfile(currentProfile);
                                    Profile profile = Profile.getCurrentProfile();
                                    nameUserFB = profile.getFirstName();
                                }
                            };
                            mProfileTracker.startTracking();
                        } else {
                            Profile profile = Profile.getCurrentProfile();
                            nameUserFB = profile.getFirstName();
                        }
                        startActivity.putExtra("ID", idUserFB);
                        startActivity.putExtra("NAME", nameUserFB);
                        if (nameUserFB != null) {
                            startActivity(startActivity);
                            finish();
                        }

                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }

        );

    }


//        accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(
//                    AccessToken oldAccessToken,
//                    AccessToken currentAccessToken) {
//                // Set the access token using
//                // currentAccessToken when it's loaded or set.
//            }
//        };
//        // If the access token is available already assign it.
//        accessToken = AccessToken.getCurrentAccessToken();
//        // If already logged in show the home view
//        if (accessToken != null) {//<- IMPORTANT
//            Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
//            startActivity(intent);
//            finish();//<- IMPORTANT
//        }


    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

}