package com.semistone.donately.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.semistone.donately.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro1_title), getString(R.string.intro1_description), R.drawable.ic_loving, ContextCompat.getColor(this, R.color.colorAccent)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro2_title), getString(R.string.intro2_description), R.drawable.ic_video_play, ContextCompat.getColor(this, R.color.colorAccent)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro3_title), getString(R.string.intro3_description), R.drawable.ic_free, ContextCompat.getColor(this, R.color.colorAccent)));
        addSlide(LoginSlide.newInstance(R.layout.intro_login));
        setDoneText(null);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        pager.setCurrentItem(mPagerAdapter.getCount() - 1);
    }
}