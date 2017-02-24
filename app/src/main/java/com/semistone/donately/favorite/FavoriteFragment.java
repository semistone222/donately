package com.semistone.donately.favorite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.data.Content;
import com.semistone.donately.data.User;
import com.semistone.donately.detail.DetailActivity;
import com.semistone.donately.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by semistone on 2017-02-23.
 */

public class FavoriteFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty)
    TextView mTextViewEmpty;

    public static final String NAME = "favorite";
    private Adapter mAdapter;
    private Realm mRealm;
    private List<Content> mContents;
    private User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.bind(this, view);
        mContents = new ArrayList<>();
        mAdapter = new Adapter(getContext(), mContents);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        mRecyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRealm = Realm.getDefaultInstance();
        mUser = mRealm.where(User.class).findFirst();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Call<List<Content>> getContents = NetworkManager.service.getFavoriteContents(mUser.getId());
        getContents.enqueue(new Callback<List<Content>>() {
            @Override
            public void onResponse(Call<List<Content>> call, Response<List<Content>> response) {
                if (response.isSuccessful()) {
                    List<Content> contents = response.body();
                    Log.e("semistone", "onResponse: " + contents.toString());
                    mContents.clear();
                    mContents.addAll(contents);
                    mAdapter.notifyDataSetChanged();
                    if (mContents.size() > 0) {
                        mTextViewEmpty.setVisibility(View.GONE);
                    } else {
                        mTextViewEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Content>> call, Throwable t) {
                Log.e("semistone", "onFailure: ");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    class Adapter extends RecyclerView.Adapter<Holder> {

        private final Context mContext;
        private final List<Content> mContents;

        Adapter(Context context, List<Content> contents) {
            mContext = context;
            mContents = contents;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_tile, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Content content = mContents.get(position);
            holder.content = content;
            Glide.with(mContext).load(content.getPictureUrl()).into(holder.picture);
            holder.title.setText(content.getTitle());
        }

        @Override
        public int getItemCount() {
            if (mContents == null) {
                return 0;
            } else {
                return mContents.size();
            }
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tile_picture)
        protected ImageView picture;
        @BindView(R.id.tile_title)
        protected TextView title;

        private Content content;

        public Holder(final View view) {
            super(view);
            ButterKnife.bind(this, view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_DETAIL_CONTENT_ID, content.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}