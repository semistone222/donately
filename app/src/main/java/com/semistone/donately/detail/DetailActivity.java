package com.semistone.donately.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.data.Content;
import com.semistone.donately.data.Favorite;
import com.semistone.donately.data.User;
import com.semistone.donately.main.MainActivity;
import com.semistone.donately.network.NetworkManager;
import com.semistone.donately.utility.IntentUtils;
import com.semistone.donately.utility.PointUtils;
import com.semistone.donately.video.VideoActivity;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_DETAIL_CONTENT_ID = "extra-detail-content-id";

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.text_view_detail)
    protected TextView mTextViewDetail;
    /*@BindView(R.id.text_view_item)
    protected TextView mTextViewItem;*/
    @BindView(R.id.image_view)
    protected ImageView mImageView;
    @BindView(R.id.fund_progress_bar)
    protected ProgressBar mFundProgressBar;
    @BindView(R.id.progress_text_view)
    protected TextView mProgressText;
    @BindView(R.id.progress_text_view2)
    protected TextView mProgressText2;
    @BindView(R.id.favorite_button)
    protected ToggleButton mFavoriteButton;
    @BindView(R.id.link_button)
    protected ImageButton mLinkButton;
    @BindView(R.id.text_view_title)
    protected TextView mTextViewTitle;

    private String TAG = getClass().getName();
    private Realm mRealm;
    private Content mContent;
    private int mContentId;
    private User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRealm = Realm.getDefaultInstance();
        mUser = mRealm.where(User.class).findFirst();
        mContentId = getIntent().getIntExtra(EXTRA_DETAIL_CONTENT_ID, 0);
        mContent = new Content();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Call<Content> getContent = NetworkManager.service.getContent(mUser.getId(), mContentId);
        getContent.enqueue(new Callback<Content>() {
            @Override
            public void onResponse(Call<Content> call, Response<Content> response) {
                Content content = response.body();
                mContent = content;
                Log.e("semistone", "onResponse: " + content.toString());
                updateUI();
            }

            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                Log.e("semistone", "onFailure: ");
            }
        });

        mFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Call<Favorite> insertFavorite = NetworkManager.service.insertFavorite(mUser.getId(), mContent.getId());
                    insertFavorite.enqueue(new Callback<Favorite>() {
                        @Override
                        public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(getWindow().getDecorView(), R.string.message_add_favorite, Snackbar.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: ");
                            }
                        }

                        @Override
                        public void onFailure(Call<Favorite> call, Throwable t) {
                            Log.e(TAG, "onFailure: ");
                        }
                    });
                } else {
                    Call<Favorite> deleteFavorite = NetworkManager.service.deleteFavorite(mUser.getId(), mContent.getId());
                    deleteFavorite.enqueue(new Callback<Favorite>() {
                        @Override
                        public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: ");
                            }
                        }

                        @Override
                        public void onFailure(Call<Favorite> call, Throwable t) {
                            Log.e(TAG, "onFailure: ");
                        }
                    });
                }
            }
        });

        mLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.openWebPage(v.getContext(), mContent.getLinkUrl());
            }
        });
    }

    private void updateUI() {
        mCollapsingToolbarLayout.setTitle(mContent.getTitle());
        Glide.with(this).load(mContent.getPictureUrl()).into(mImageView);
        mTextViewTitle.setText(mContent.getTitle());
        mTextViewDetail.setText(mContent.getDescription());
        /*mTextViewItem.setText(mContent.getDescription2());*/
        mFundProgressBar.setMax(mContent.getGoal());
        mFundProgressBar.setProgress(mContent.getCurrentPoint());
        mProgressText.setText(PointUtils.getProgressPercent(mContent.getCurrentPoint(), mContent.getGoal()));
        mProgressText2.setText(PointUtils.getProgressPercent2(mContent.getCurrentPoint(), mContent.getGoal()));
        mFavoriteButton.setChecked(mContent.isFavorite());
    }

    @OnClick(R.id.action_button)
    protected void onClickActionButton(View v) {
        Intent intent = new Intent(DetailActivity.this, VideoActivity.class);
        intent.putExtra(VideoActivity.EXTRA_VIDEO_CONTENT_ID, mContent.getId());
        startActivityForResult(intent, MainActivity.REQUEST_DONATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_DONATE) {
            if (resultCode == RESULT_OK) {
                int point = data.getIntExtra(getString(R.string.point_key), 15);
                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_coin)
                        .setTitle(R.string.message_thank_title)
                        .setMessage(String.format(getString(R.string.message_thank_detail), point))
                        .show();
            }
        }
    }
}
