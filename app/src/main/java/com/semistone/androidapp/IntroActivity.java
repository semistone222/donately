package com.semistone.androidapp;

import android.content.Intent;
import android.os.Bundle;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends OnboarderActivity {

    private List<OnboarderPage> onboarderPages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboarderPages = new ArrayList<OnboarderPage>();

        // Create pages
        OnboarderPage onboarderPage1 = new OnboarderPage(R.string.onboaderpage1_title, R.string.onboaderpage1_description, R.drawable.ic_loving);
        OnboarderPage onboarderPage2 = new OnboarderPage(R.string.onboaderpage2_title, R.string.onboaderpage2_description, R.drawable.ic_video_play);
        OnboarderPage onboarderPage3 = new OnboarderPage(R.string.onboaderpage3_title, R.string.onboaderpage3_description, R.drawable.ic_free);

        // setting
        onboarderPage1.setTitleColor(R.color.white);
        onboarderPage1.setDescriptionColor(R.color.white);
        onboarderPage1.setBackgroundColor(R.color.colorPrimaryDark);

        onboarderPage2.setTitleColor(R.color.white);
        onboarderPage2.setDescriptionColor(R.color.white);
        onboarderPage2.setBackgroundColor(R.color.colorPrimary);

        onboarderPage3.setTitleColor(R.color.white);
        onboarderPage3.setDescriptionColor(R.color.white);
        onboarderPage3.setBackgroundColor(R.color.colorPrimaryDark);

        // add pages
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);

        // And pass pages to 'setOnboardPagesReady' method
        setOnboardPagesReady(onboarderPages);

    }

    @Override
    public void onSkipButtonPressed() {
        // Optional: by default it skips onboarder to the end
        super.onSkipButtonPressed();
    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
    }

}
