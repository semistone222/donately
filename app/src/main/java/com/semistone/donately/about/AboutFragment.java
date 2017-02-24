package com.semistone.donately.about;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.semistone.donately.R;

/**
 * Created by semistone on 2017-02-20.
 */

public class AboutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_about);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();

        if (key == getString(R.string.pref_introduce_key)) {
            startActivity(new Intent(getActivity(), IntroduceActivity.class));
        } else if (key == getString(R.string.pref_open_source_key)) {
            startActivity(new Intent(getActivity(), OpenSourceActivity.class));
        }

        return super.onPreferenceTreeClick(preference);
    }
}
