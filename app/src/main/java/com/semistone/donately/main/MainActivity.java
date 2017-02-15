package com.semistone.donately.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.data.User;
import com.semistone.donately.history.HistoryActivity;
import com.semistone.donately.intro.IntroActivity;
import com.semistone.donately.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_EXIT = 1342;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    @BindView(R.id.collapsing_toolbar_layout)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.tl_main)
    protected TabLayout mTlMain;

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;

    @BindView(R.id.nav_view)
    protected NavigationView mNavigationView;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
        User user = mRealm.where(User.class).findFirst();

        if(user == null || user.equals(null)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 툴바
        setSupportActionBar(mToolbar);

        // 접히는 툴바 레이아웃
        mCollapsingToolbarLayout.setTitle("gsdfsdfgsfd");

        // 토글
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // 네비게이션
        mNavigationView.setNavigationItemSelectedListener(this);
        View mNavHeader = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        ((TextView) mNavHeader.findViewById(R.id.tv_user_name)).setText(user.getName());
        ((TextView) mNavHeader.findViewById(R.id.tv_user_email)).setText(user.getEmail());
        Glide.with(this)
                .load(user.getPhotoUrl())
                .bitmapTransform(new CropCircleTransformation(this))
                .placeholder(R.drawable.ic_face_black_24dp)
                .error(R.drawable.ic_report_black_24dp)
                .thumbnail(0.1f)
                .override(150, 150)
                .into((ImageView) mNavHeader.findViewById(R.id.iv_user_image));

        // 탭 레이아웃
        // mTlMain;


        MainAdapter mainAdpater = new MainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mainAdpater);
        mTlMain.setupWithViewPager(mViewPager);


    }

    @OnClick(R.id.fab)
    void onClickFab(View view) {
        Snackbar.make(view, "TEST", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_donate) {

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), REQUEST_EXIT);
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_share) {

        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }
}
