package com.semistone.donately.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.semistone.donately.R;
import com.semistone.donately.data.User;
import com.semistone.donately.main.MainActivity;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;

    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();

        if (mRealm.where(User.class).findAll().size() != 0) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        setUpFacebookLogin();
        setUpGoogleLogin();
    }

    private void setUpFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        String id = object.optString("id");
                        String name = object.optString("name");
                        String email = object.optString("email");
                        String accessToken = loginResult.getAccessToken().getToken();
                        String type = "facebook";
                        String photoUrl = "https://graph.facebook.com/" + id + "/picture?type=large";

                        mRealm.beginTransaction();
                        User user = mRealm.createObject(User.class, id);
                        user.setName(name);
                        user.setEmail(email);
                        user.setAccessToken(accessToken);
                        user.setType(type);
                        user.setPhotoUrl(photoUrl);
                        mRealm.commitTransaction();

                        // TODO: 2017-02-13 액티비티 순서
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.login_canceled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @OnClick(R.id.google_login)
    void onClickGoogleLogin(View view) {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @OnClick(R.id.facebook_login)
    void onClickFacebookLogin(View view) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            String id = acct.getId();
            String name = acct.getDisplayName();
            String email =  acct.getEmail();
            String accessToken = acct.getIdToken();
            String type = User.GOOGLE;
            String photoUrl = acct.getPhotoUrl().toString();

            mRealm.beginTransaction();
            User user = mRealm.createObject(User.class, id);
            user.setName(name);
            user.setEmail(email);
            user.setAccessToken(accessToken);
            user.setType(type);
            user.setPhotoUrl(photoUrl);
            mRealm.commitTransaction();

            // TODO: 2017-02-13 액티비티 순서
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            // TEST
            Toast.makeText(getApplicationContext(), String.valueOf(mGoogleApiClient.isConnected()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }

    // TEST
    @OnClick(R.id.gotomainactivity)
    protected void onClickGoToMainActivity(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
