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

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.data.Content;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {

    public static final String CONTENT_ID = "content-id";

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

    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int contentId = getIntent().getIntExtra(CONTENT_ID, 0);
        mRealm = Realm.getDefaultInstance();

        Content content = mRealm.where(Content.class).equalTo(Content.ID, contentId).findFirst();

        mCollapsingToolbarLayout.setTitle(content.getTitle());
        mTextViewDetail.setText(content.getDescription());
        mTextViewItem.setText(content.getDescription2());
        Glide.with(this).load(content.getPictureUrl()).into(mImageView);
    }

    @OnClick(R.id.action_button)
    protected void onClickActionButton(View v) {
        Snackbar.make(v, "Action",
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.string.action_favorite) {
            Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.string.action_link) {
            Toast.makeText(this, "Link", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
