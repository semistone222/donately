package com.semistone.donately.history;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.semistone.donately.R;
import com.semistone.donately.data.History;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class HistoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.rv_history)
    protected RecyclerView mRvHistory;

    @BindView(R.id.tv_history_empty)
    protected TextView tvHistoryEmpty;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRealm = Realm.getDefaultInstance();

        String[] sortField = {History.DONATE_DATE};
        Sort sort[] = {Sort.DESCENDING};
        mRvHistory.setLayoutManager(new LinearLayoutManager(this));
        RealmResults<History> results = mRealm.where(History.class).findAllSorted(sortField, sort);
        mRvHistory.setAdapter(new HistoryAdapter(this, results));
        mRvHistory.setHasFixedSize(true);
        mRvHistory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if(results.size() > 0) {
            tvHistoryEmpty.setVisibility(View.GONE);
        } else {
            tvHistoryEmpty.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.fab)
    void onClickFab(View view) {
        Snackbar.make(view, R.string.message_history, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
