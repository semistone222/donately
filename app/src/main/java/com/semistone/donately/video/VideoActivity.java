package com.semistone.donately.video;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.semistone.donately.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {

    String TAG = getPackageName();
    final private int PROGRESS_BAR_MAX = 100;

    @BindView(R.id.video_view)
    VideoView mVideoView;

    @BindView(R.id.video_progress_bar)
    ProgressBar mVideoProgressBar;

    private int mPausePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // style이 dialog인데 다른 부분 터치하는 부분에 대한 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        // 왜 로그가 안찍히지...? 중간 재생 영상 불러오는게 되지 않는다.
        Log.e(TAG, "onCreate: ");
        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.advertisement_start, Snackbar.LENGTH_SHORT).show();

        if (savedInstanceState != null) {
            mPausePosition = savedInstanceState.getInt(getString(R.string.video_current_playing_time_key));
        } else {
            mPausePosition = 0;
        }

        // TODO: 2017-02-16 나중에 서버에서 저용량 광고영상을 내부 저장소로 가져오도록 바꾸자.
        // TODO: 2017-02-16 앱 시작하면 서비스를 이용해서 서버에서 가져오는게 맞을듯...
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("android.resource://");
        stringBuilder.append(getPackageName());
        stringBuilder.append("/");
        stringBuilder.append(R.raw.sample_video);
        mVideoView.setVideoURI(Uri.parse(stringBuilder.toString()));
        mVideoView.requestFocus();

        // 액티비티가 시작되고 준비가 되면
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 소리는 안들리게
                mp.setVolume(0, 0);
                // 영상이 자동 시작
                mVideoView.seekTo(mPausePosition);
                mVideoView.start();
            }
        });

        // 영상이 끝나면
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                doTasksAfterAdsEnded(false);
            }
        });

        // 비디오 뷰를 누르면
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                openWebPage("http://www.naver.com");
                // TODO: 2017-02-16 광고 db의 url로 바꾸기
                doTasksAfterAdsEnded(true);
                return true;
            }
        });

        // 레이아웃 문제가 있음. 내용이 다 안 보임.

        // 남은시간 표시 또는 프로그레스 바로 표시
        //mProgressBar.setProgress(0);
        //mProgressBar.setMax(PROGRESS_BAR_MAX);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        mVideoView.seekTo(mPausePosition);
        mVideoView.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            doNothing();
            return true;
        }
        // 뭐지 뒤 액티비티가 클릭이 되네?

        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        doNothing();
    }

    private void doNothing() {

    }

    private void openWebPage(String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void doTasksAfterAdsEnded(boolean isClicked) {
        // TODO: 2017-02-16 mainactivity로 돌아가 포인트 쌓이는 애니메이션을 했으면 좋겠음.
        //setResult(something);
        // TODO: 2017-02-16   db에 기록
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        int currentPlayingTime = mVideoView.getCurrentPosition();
        mVideoView.pause();
        outState.putInt(getString(R.string.video_current_playing_time_key), currentPlayingTime);
    }
}
