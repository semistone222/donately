package com.semistone.donately.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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

/**
 * Created by semistone on 2017-02-14.
 */

public class LoginSlide extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final int RC_SIGN_IN = 9001;
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int mLayoutResId;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private Realm mRealm;

    public static LoginSlide newInstance(int layoutResId) {
        LoginSlide sampleSlide = new LoginSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            mLayoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }

        mRealm = Realm.getDefaultInstance();
        setUpFacebookLogin();
        setUpGoogleLogin();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(mLayoutResId, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setUpFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest graphRequest = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {

                                        String id = object.optString("id");
                                        String name = object.optString("name");
                                        String email = object.optString("email");
                                        String accessToken =
                                                loginResult.getAccessToken().getToken();
                                        String type = "facebook";
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("https://graph.facebook.com/");
                                        stringBuilder.append(id);
                                        stringBuilder.append("/picture?type=large");
                                        String photoUrl = stringBuilder.toString();

                                        mRealm.beginTransaction();
                                        User user = mRealm.createObject(User.class, id);
                                        user.setName(name);
                                        user.setEmail(email);
                                        user.setAccessToken(accessToken);
                                        user.setType(type);
                                        user.setPhotoUrl(photoUrl);
                                        mRealm.commitTransaction();

                                        startActivity(
                                                new Intent(getActivity(), MainActivity.class));
                                        getActivity().finish();
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),
                                R.string.login_canceled, Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),
                                R.string.login_error, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUpGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            String email = acct.getEmail();
            String accessToken = acct.getIdToken();
            String type = User.GOOGLE;
            String photoUrl;
            if (acct.getPhotoUrl() != null) {
                photoUrl = acct.getPhotoUrl().toString();
            } else {
                photoUrl = null;
            }

            mRealm.beginTransaction();
            User user = mRealm.createObject(User.class, id);
            user.setName(name);
            user.setEmail(email);
            user.setAccessToken(accessToken);
            user.setType(type);
            user.setPhotoUrl(photoUrl);
            mRealm.commitTransaction();

            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),
                    R.string.login_error, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
