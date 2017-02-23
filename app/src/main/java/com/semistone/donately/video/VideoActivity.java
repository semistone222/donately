package com.semistone.donately.video;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
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
import com.semistone.donately.data.Advertisement;
import com.semistone.donately.utility.IntentUtils;
import com.semistone.donately.utility.PointUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class VideoActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_CONTENT_ID = "extra-video-content-id";
    private static final long SYNC_INTERVAL = 1000;

    @BindView(R.id.video_view)
    protected VideoView mVideoView;
    @BindView(R.id.video_progress_bar)
    protected ProgressBar mVideoProgressBar;
    @BindString(R.string.pref_advertisement_length_key)
    protected String mAdLengthKey;

    private int mPausePosition;
    private Realm mRealm;
    private History mHistory;
    private Advertisement mAdvertisement;
    private int mAdLength;

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
        setFinishOnTouchOutside(false);
        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        mHistory = new History();

        int contentId = getIntent().getIntExtra(EXTRA_VIDEO_CONTENT_ID, 0);
        mHistory.setContentId(contentId);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String adLengthStr = sharedPreferences.getString(mAdLengthKey, getString(R.string.pref_advertisement_length_15));
        mAdLength = Integer.valueOf(adLengthStr);
        mAdvertisement = mRealm.where(Advertisement.class).equalTo(Advertisement.LENGTH, mAdLength).findFirst();

        mVideoView.setVideoURI(Uri.parse(mAdvertisement.getFileUrl()));
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
                    IntentUtils.openWebPage(v.getContext(), mAdvertisement.getPromotionUrl());
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

    private void doTasksAfterAdsEnded() {
        final User user = mRealm.where(User.class).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                History history = realm.createObject(History.class, History.getNextKey(mRealm));
                history.setUserId(user.getId());
                history.setContentId(mHistory.getContentId());
                history.setAdvertisementId(mAdvertisement.getId());
                history.setDonateDate(System.currentTimeMillis());
                history.setPoint(PointUtils.calculate(mAdLength, mHistory.isClicked()));
                history.setClicked(mHistory.isClicked());
            }
        });
        finish();
    }
}
