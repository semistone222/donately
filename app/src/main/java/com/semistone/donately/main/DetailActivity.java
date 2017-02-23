/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.semistone.donately.main;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.data.Content;
import com.semistone.donately.data.History;
import com.semistone.donately.utility.IntentUtils;
import com.semistone.donately.utility.PointUtils;
import com.semistone.donately.video.VideoActivity;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_DETAIL_CONTENT_ID = "extra-detail-content-id";

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.text_view_detail)
    protected TextView mTextViewDetail;
    @BindView(R.id.text_view_item)
    protected TextView mTextViewItem;
    @BindView(R.id.image_view)
    protected ImageView mImageView;
    @BindView(R.id.fund_progress_bar)
    protected ProgressBar mFundProgressBar;
    @BindView(R.id.progress_text_view)
    protected TextView mProgressText;
    @BindColor(R.color.heart)
    protected int colorHeart;
    @BindColor(R.color.white)
    protected int colorWhite;

    private Realm mRealm;
    private Content mContent;
    private Menu mMenu;
    private int mCurrentPoint = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int contentId = getIntent().getIntExtra(EXTRA_DETAIL_CONTENT_ID, 0);
        mRealm = Realm.getDefaultInstance();

        mContent = mRealm.where(Content.class).equalTo(Content.ID, contentId).findFirst();

        mCollapsingToolbarLayout.setTitle(mContent.getTitle());
        mTextViewDetail.setText(mContent.getDescription());
        mTextViewItem.setText(mContent.getDescription2());
        Glide.with(this).load(mContent.getPictureUrl()).into(mImageView);
        mFundProgressBar.setMax(mContent.getGoal());
        final RealmResults<History> results = mRealm.where(History.class).equalTo(History.CONTENT_ID, mContent.getId()).findAll();
        updateProgressUI(results);
        results.addChangeListener(new RealmChangeListener<RealmResults<History>>() {
            @Override
            public void onChange(RealmResults<History> element) {
                updateProgressUI(results);
            }
        });
        // TEST
        Log.e("abc", "onBindViewHolder: " + mCurrentPoint + " / " + mContent.getGoal());
    }

    private void updateProgressUI(RealmResults<History> results) {
        mCurrentPoint = 0;
        for (History history : results) {
            mCurrentPoint += history.getPoint();
        }
        mFundProgressBar.setProgress(mCurrentPoint);
        mProgressText.setText(PointUtils.getProgressPercent(mCurrentPoint, mContent.getGoal()));
    }

    @OnClick(R.id.action_button)
    protected void onClickActionButton(View v) {
        Intent intent = new Intent(DetailActivity.this, VideoActivity.class);
        intent.putExtra(VideoActivity.EXTRA_VIDEO_CONTENT_ID, mContent.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateFavoriteMenuItem(mContent.isFavorite());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_favorite) {
            final Content content = mRealm.where(Content.class).equalTo(Content.ID, mContent.getId()).findFirst();
            final boolean newFavorite = !content.isFavorite();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    content.setFavorite(newFavorite);
                    updateFavoriteMenuItem(newFavorite);
                }
            });
            if (newFavorite) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Added to Favorites.", Snackbar.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_link) {
            IntentUtils.openWebPage(this, mContent.getLinkUrl());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFavoriteMenuItem(boolean isFavorite) {
        int iconColor = isFavorite ? colorHeart : colorWhite;
        MenuItem targetItem = mMenu.findItem(R.id.action_favorite);
        targetItem.getIcon().setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
