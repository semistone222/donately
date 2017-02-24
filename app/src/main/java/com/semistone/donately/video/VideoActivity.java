package com.semistone.donately.video;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.semistone.donately.MyApplication;
import com.semistone.donately.R;
import com.semistone.donately.data.Advertisement;
import com.semistone.donately.data.History;
import com.semistone.donately.data.User;
import com.semistone.donately.network.NetworkManager;
import com.semistone.donately.utility.IntentUtils;
import com.semistone.donately.utility.PointUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_CONTENT_ID = "extra-video-content-id";
    private static final long SYNC_INTERVAL = 1000;

    @BindView(R.id.video_view)
    protected VideoView mVideoView;
    @BindView(R.id.video_progress_bar)
    protected ProgressBar mVideoProgressBar;
    @BindString(R.string.pref_advertisement_length_key)
    protected String mAdLengthKey;

    private Realm mRealm;
    private Advertisement mAdvertisement;
    private int mContentId;
    private boolean mIsClicked;

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

        mContentId = getIntent().getIntExtra(EXTRA_VIDEO_CONTENT_ID, 0);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String adLengthStr = sharedPreferences.getString(mAdLengthKey, getString(R.string.pref_advertisement_length_15));

        Call<Advertisement> getAdvertisement = NetworkManager.service.getAdvertisement(Integer.valueOf(adLengthStr));
        getAdvertisement.enqueue(new Callback<Advertisement>() {
            @Override
            public void onResponse(Call<Advertisement> call, Response<Advertisement> response) {
                if (response.isSuccessful()) {
                    Advertisement advertisement = response.body();
                    mAdvertisement = advertisement;
                    setUpAdvertisement();
                    Log.e("semistone", "onResponse: " + advertisement.toString());
                }
            }

            @Override
            public void onFailure(Call<Advertisement> call, Throwable t) {
                Log.e("semistone", "onFailure: ");
            }
        });
    }

    private void setUpAdvertisement() {
        HttpProxyCacheServer proxy = MyApplication.getProxy(this);
        String proxyUrl = proxy.getProxyUrl(mAdvertisement.getFileUrl());
        mVideoView.setVideoPath(proxyUrl);
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
                if (!mIsClicked) {
                    IntentUtils.openWebPage(v.getContext(), mAdvertisement.getPromotionUrl());
                    mIsClicked = true;
                    doTasksAfterAdsEnded();
                }
                return true;
            }
        });
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
        User user = mRealm.where(User.class).findFirst();
        final int point = PointUtils.calculate(mAdvertisement.getLength(), mIsClicked);

        Call<History> insertHistory = NetworkManager.service.insertHistory(user.getId()
                , mContentId
                , mAdvertisement.getId()
                , mIsClicked ? 1 : 0
                , point);

        insertHistory.enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful()) {
                    Log.e("semistone", "onResponse: ");
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.point_key), point);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                Log.e("semistone", "onFailure: ");
            }
        });
    }
}
