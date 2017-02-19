package com.semistone.donately.video;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.semistone.donately.R;
import com.semistone.donately.data.History;
import com.semistone.donately.data.User;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class VideoActivity extends AppCompatActivity {

    private static final long SYNC_INTERVAL = 1000;
    private static final int AD_LENGTH_15 = 15;
    private static final int AD_LENGTH_30 = 30;
    private static final int AD_LENGTH_60 = 60;

    @BindView(R.id.video_view)
    protected VideoView mVideoView;

    @BindView(R.id.video_progress_bar)
    protected ProgressBar mVideoProgressBar;

    @BindString(R.string.pref_advertisement_length_key)
    protected String mAdLengthKey;

    private int mPausePosition;
    private Realm mRealm;
    private History mHistory;

    private Runnable syncVideoProgress = new Runnable() {
        @Override
        public void run() {
            if (mVideoProgressBar != null) {
                mVideoProgressBar.setProgress(mVideoView.getCurrentPosition());
            }
            if (mVideoView.isPlaying()) {
                mVideoProgressBar.postDelayed(syncVideoProgress, SYNC_INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        setFinishOnTouchOutside(false);
        mHistory = new History();

        Intent prev = getIntent();
        if (prev.hasExtra(getString(R.string.beneficiary_key))) {
            // TODO: 2017-02-20  
//            mHistory.setBeneficiary(prev.getIntExtra(R.string.beneficiary_key, 0));
        }

        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.advertisement_start,
                Snackbar.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String adLength = sharedPreferences.getString(mAdLengthKey, getString(R.string.pref_advertisement_length_15));
        mHistory.setAdLength(Integer.valueOf(adLength));

        int advertisementRscId = -1;
        switch (mHistory.getAdLength()) {
            case AD_LENGTH_15:
                advertisementRscId = R.raw.ad_15;
                break;
            case AD_LENGTH_30:
                advertisementRscId = R.raw.ad_30;
                break;
            case AD_LENGTH_60:
                advertisementRscId = R.raw.ad_60;
                break;
            default:
                break;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("android.resource://");
        stringBuilder.append(getPackageName());
        stringBuilder.append("/");
        stringBuilder.append(advertisementRscId);
        mVideoView.setVideoURI(Uri.parse(stringBuilder.toString()));
        mVideoView.requestFocus();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0, 0);
                mVideoView.start();
                mVideoProgressBar.setMax(mVideoView.getDuration());
                mVideoProgressBar.postDelayed(syncVideoProgress, SYNC_INTERVAL);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                doTasksAfterAdsEnded();
            }
        });

        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mHistory.isClicked()) {
                    openWebPage("http://www.naver.com");
                    doTasksAfterAdsEnded();
                    mHistory.setClicked(true);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("aaaa", "onResume: " + String.valueOf(mPausePosition));
//         TODO: 2017-02-19 seekTo is not working...
//        mVideoView.seekTo(mPausePosition);
//        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPausePosition = mVideoView.getCurrentPosition();
        Log.w("aaaa", "onPause: " + String.valueOf(mPausePosition));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void openWebPage(String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void doTasksAfterAdsEnded() {
        final User user = mRealm.where(User.class).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                History history = mRealm.createObject(History.class, History.getNextKey(mRealm));
                history.setUserId(user.getId());
                history.setDonateDate(System.currentTimeMillis());
                history.setBeneficiary(mHistory.getBeneficiary());
                history.setAdLength(mHistory.getAdLength());
                history.setClicked(mHistory.isClicked());
            }
        });
        setResult(RESULT_OK);
        finish();
    }
}
