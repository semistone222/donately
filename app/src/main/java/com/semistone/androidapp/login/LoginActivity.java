package com.semistone.androidapp.login;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.semistone.androidapp.R;
import com.semistone.androidapp.data.User;
import com.semistone.androidapp.main.MainActivity;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;

    private Realm mRealm;

    @BindView(R.id.test)
    protected TextView tv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();

        if (mRealm.where(User.class).findAll().size() != 0) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    // TODO: 2017-02-09
    @OnClick(R.id.facebook_logout)
    void onClickFacebookLogout(View view) {
        LoginManager.getInstance().logOut();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.where(User.class).findAll().deleteAllFromRealm();
            }
        });
    }

    // TODO: 2017-02-09
    @OnClick(R.id.google_login)
    void onClickGoogleLogin(View view) {
        Snackbar.make(view, "Coming soon.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.facebook_login)
    void onClickFacebookLogin(View view) {

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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

                        mRealm.beginTransaction();
                        User user = mRealm.createObject(User.class, id);
                        user.setName(name);
                        user.setEmail(email);
                        user.setAccessToken(accessToken);
                        user.setType(type);
                        mRealm.commitTransaction();

                        // test
                        tv_test.setText(user.toString());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
