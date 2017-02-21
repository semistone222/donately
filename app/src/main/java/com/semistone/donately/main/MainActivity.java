package com.semistone.donately.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.about.AboutActivity;
import com.semistone.donately.data.Content;
import com.semistone.donately.data.History;
import com.semistone.donately.data.User;
import com.semistone.donately.history.HistoryActivity;
import com.semistone.donately.intro.IntroActivity;
import com.semistone.donately.settings.SettingsActivity;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import xyz.hanks.library.SmallBang;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_EXIT = 1342;
    private final static int REQUEST_ADS = 1523;

    @BindString(R.string.app_name)
    protected String mAppName;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;

    @BindView(R.id.nav_view)
    protected NavigationView mNavigationView;

    @BindView(R.id.tv_donate_point)
    protected TextView mTvDonatePoint;

    private SmallBang mSmallBang;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSmallBang = SmallBang.attach2Window(this);

        mRealm = Realm.getDefaultInstance();
        User user = mRealm.where(User.class).findFirst();

        if (user == null || user.equals(null)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        updatePointView(false);

        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.nav_open,
                R.string.nav_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

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

        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(CardContentFragment.newInstance(Content.TYPE_ORG), "Org");
        adapter.addFragment(CardContentFragment.newInstance(Content.TYPE_PEOPLE), "People");
        adapter.addFragment(new FavoriteTileContentFragment(), "♡");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

// TODO: 2017-02-21
//    @OnClick(R.id.fab)
//    void onClickFab(View view) {
//        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
//        intent.putExtra(getString(R.string.beneficiary_key),
//                mTabLayout.getSelectedTabPosition());
//        startActivityForResult(intent, REQUEST_ADS);
//    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_donate) {

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class),
                    REQUEST_EXIT);
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_tell) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.show_case_web_site));
            Intent chooser = Intent.createChooser(intent, getString(R.string.tell_a_friend));
            if (chooser.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
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
        switch (requestCode) {
            case REQUEST_EXIT:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
            case REQUEST_ADS:
                if (resultCode == RESULT_OK) {
                    updatePointView(true);
                }
                break;
            default:
                break;
        }
    }

    // TODO: 2017-02-20 이쁘게 배치... 툴바 없앨까 고려도 해보자 UI 제대로...
    private void updatePointView(boolean isFromWatchingAd) {
        int count = 0;
        RealmResults<History> results = mRealm.where(History.class).findAll();
        for (History history : results) {
            count += history.getAdLength();
        }
        mTvDonatePoint.setText(String.valueOf(count));

        if (isFromWatchingAd) {
            mSmallBang.bang(mTvDonatePoint);
        }
    }

    //    test
    @OnClick(R.id.fab)
    protected void onClickFab(View view) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Content content = mRealm.createObject(Content.class, Content.getNextKey(mRealm));
                content.setTitle(getString(R.string.item_title));
                content.setDescription(getString(R.string.detail_desc));
                content.setDescription2(getString(R.string.detail_desc2));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("android.resource://");
                stringBuilder.append(getPackageName());
                stringBuilder.append("/");
                stringBuilder.append(R.drawable.a);
                content.setPictureUrl(stringBuilder.toString());
                content.setLinkUrl(getString(R.string.show_case_web_site));
                content.setType(getRandomType());
                content.setFavorite(getRandomBoolean());
            }
        });
    }

    // test
    public static String getRandomType() {
        if (getRandomBoolean()) {
            return Content.TYPE_ORG;
        } else {
            return Content.TYPE_PEOPLE;
        }
    }

    //test
    public static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }
}
