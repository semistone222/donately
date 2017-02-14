package com.semistone.donately.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;
import com.semistone.donately.R;
import com.semistone.donately.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends OnboarderActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OnboarderPage> onboarderPages = new ArrayList<>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNotFirst = sharedPref.getBoolean(getString(R.string.pref_intro_not_first_key), getResources().getBoolean(R.bool.pref_intro_not_first_default));
        if (isNotFirst) {
            FinishIntro();
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.pref_intro_not_first_key), true);
            editor.apply();
        }

        OnboarderPage onboarderPage1 = new OnboarderPage(R.string.onboaderpage1_title, R.string.onboaderpage1_description, R.drawable.ic_loving);
        OnboarderPage onboarderPage2 = new OnboarderPage(R.string.onboaderpage2_title, R.string.onboaderpage2_description, R.drawable.ic_video_play);
        OnboarderPage onboarderPage3 = new OnboarderPage(R.string.onboaderpage3_title, R.string.onboaderpage3_description, R.drawable.ic_free);

        onboarderPage1.setTitleColor(R.color.white);
        onboarderPage1.setDescriptionColor(R.color.white);
        onboarderPage1.setBackgroundColor(R.color.colorPrimaryDark);

        onboarderPage2.setTitleColor(R.color.white);
        onboarderPage2.setDescriptionColor(R.color.white);
        onboarderPage2.setBackgroundColor(R.color.colorPrimary);

        onboarderPage3.setTitleColor(R.color.white);
        onboarderPage3.setDescriptionColor(R.color.white);
        onboarderPage3.setBackgroundColor(R.color.colorPrimaryDark);

        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);

        setOnboardPagesReady(onboarderPages);

        setDividerVisibility(View.GONE);
        shouldUseFloatingActionButton(true);
    }

    @Override
    public void onSkipButtonPressed() {
        super.onSkipButtonPressed();
    }

    @Override
    public void onFinishButtonPressed() {
        FinishIntro();
    }

    private void FinishIntro() {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
    }
}
