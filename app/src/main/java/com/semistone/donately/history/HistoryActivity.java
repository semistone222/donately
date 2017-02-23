package com.semistone.donately.history;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.semistone.donately.R;
import com.semistone.donately.data.History;
import com.semistone.donately.data.User;
import com.semistone.donately.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.rv_history)
    protected RecyclerView mRvHistory;
    @BindView(R.id.tv_history_empty)
    protected TextView tvHistoryEmpty;

    private Realm mRealm;
    private HistoryAdapter mHistoryAdapter;
    private List<History> mHistory;

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

        mHistory = new ArrayList<>();
        mHistoryAdapter = new HistoryAdapter(this, mHistory);
        mRvHistory.setLayoutManager(new LinearLayoutManager(this));
        mRvHistory.setAdapter(mHistoryAdapter);
        mRvHistory.setHasFixedSize(true);
        mRvHistory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRealm = Realm.getDefaultInstance();
        User user = mRealm.where(User.class).findFirst();
        Call<List<History>> getHistories = NetworkManager.service.getHistories(user.getId());
        getHistories.enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful()) {
                    List<History> histories = response.body();
                    Log.e("semistone", "onResponse: " + histories.toString());
                    mHistory.clear();
                    mHistory.addAll(histories);
                    mHistoryAdapter.notifyDataSetChanged();
                    if (mHistory.size() > 0) {
                        tvHistoryEmpty.setVisibility(View.GONE);
                    } else {
                        tvHistoryEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<History>> call, Throwable t) {
                Log.e("semistone", "onFailure: ");
            }
        });
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
