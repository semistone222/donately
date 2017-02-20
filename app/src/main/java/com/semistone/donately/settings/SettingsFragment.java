package com.semistone.donately.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.semistone.donately.R;
import com.semistone.donately.background.NotificationReceiver;
import com.semistone.donately.data.User;
import com.semistone.donately.intro.IntroActivity;
import com.semistone.donately.utility.AlarmUtils;

import io.realm.Realm;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleApiClient mGoogleApiClient;
    private Realm mRealm;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);

        mRealm = Realm.getDefaultInstance();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (p instanceof ListPreference) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }

        Preference logoutPref = findPreference(getString(R.string.pref_logout_key));
        logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                User user = mRealm.where(User.class).findFirst();

                switch (user.getType()) {
                    case User.FACEBOOK:
                        LoginManager.getInstance().logOut();
                        break;
                    case User.GOOGLE:
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        Toast.makeText(getActivity(), R.string.logout_success,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;
                    default:
                        break;
                }

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mRealm.deleteAll();
                    }
                });

                startActivity(new Intent(getActivity(), IntroActivity.class));
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();

                return true;
            }
        });
    }

    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    private void manageDailyNotification(boolean isActive) {
        if (isActive) {
            AlarmUtils.registerBroadcastReceiverRepeating(
                    getActivity(),
                    NotificationReceiver.class,
                    AlarmUtils.NOTIFY_EVERY_DAY_ID,
                    AlarmUtils.upComingSpecificTime(
                            getResources().getInteger(R.integer.encourage_notification_hour),
                            getResources().getInteger(R.integer.encourage_notification_minute)),
                    AlarmUtils.ONE_DAY
            );
        } else {
            AlarmUtils.unRegisterBroadcastReceiver(
                    getActivity(),
                    NotificationReceiver.class,
                    AlarmUtils.NOTIFY_EVERY_DAY_ID
            );
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference p = findPreference(key);
        if (p instanceof ListPreference) {
            String value = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSummary(p, value);
        } else if (p instanceof SwitchPreferenceCompat) {
            boolean isActive = sharedPreferences.getBoolean(p.getKey(), true);
            manageDailyNotification(isActive);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        mRealm.close();
    }
}
