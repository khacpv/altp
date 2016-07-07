package com.example.gcs.faster5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


/**
 * Created by Kien on 07/05/2016.
 */
public class LoginScreen extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
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

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                //AccessToken is for us to check whether we have previously logged in into
                //this app, and this information is save in shared preferences and sets it during SDK initialization
                accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken == null) {
                } else {
                    Intent main = new Intent(LoginScreen.this, InfoScreen.class);
                    startActivity(main);

                }
            }
        });
        setContentView(R.layout.login_screen);

        //register a callback to respond to a login result,
        callbackManager = CallbackManager.Factory.create();

        //register access token to check whether user logged in before
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                accessToken = newToken;
            }
        };

        startActivity = new Intent(LoginScreen.this, InfoScreen.class);
        //disconnectFromFacebook();
        loginButton = (LoginButton) findViewById(R.id.fblogin_button);
        loginButton.setReadPermissions("public_profile");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginButton.setVisibility(View.INVISIBLE);
                        accessToken = loginResult.getAccessToken();
                        startActivity(startActivity);
                        finish();
                    }
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }
        );
        accessTokenTracker.startTracking();

    }

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}