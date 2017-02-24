package com.semistone.donately.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.about.AboutActivity;
import com.semistone.donately.data.Favorite;
import com.semistone.donately.data.User;
import com.semistone.donately.favorite.FavoriteActivity;
import com.semistone.donately.history.HistoryActivity;
import com.semistone.donately.intro.IntroActivity;
import com.semistone.donately.settings.SettingsActivity;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_EXIT = 1342;
    public final static int REQUEST_DONATE = 7542;

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
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
        User user = mRealm.where(User.class).findFirst();

        if (user == null || user.equals(null)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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
        adapter.addFragment(new OrgFragment(), OrgFragment.NAME);
        adapter.addFragment(new PeopleFragment(), PeopleFragment.NAME);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

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

        } else if (id == R.id.nav_favorite) {
            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
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
            case REQUEST_DONATE:
                if (resultCode == RESULT_OK) {
                    int point = data.getIntExtra(getString(R.string.point_key), 15);
                    new LovelyInfoDialog(this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_coin)
                            .setTitle(R.string.message_thank_title)
                            .setMessage(String.format(getString(R.string.message_thank_detail), point))
                            .show();
                }
            default:
                break;
        }
    }
}