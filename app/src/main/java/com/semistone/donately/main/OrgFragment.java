package com.semistone.donately.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.semistone.donately.detail.DetailActivity;
import com.semistone.donately.network.NetworkManager;
import com.semistone.donately.utility.IntentUtils;
import com.semistone.donately.utility.PointUtils;
import com.semistone.donately.video.VideoActivity;

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

public class OrgFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty)
    TextView mTextViewEmpty;

    public static final String NAME = "organization";
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
        mRealm = Realm.getDefaultInstance();
        mUser = mRealm.where(User.class).findFirst();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Call<List<Content>> getContents = NetworkManager.service.getContentsByType(mUser.getId(), NAME);
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

        private Realm mRealm;
        private final Context mContext;
        private final List<Content> mContents;
        private String TAG = getClass().getName();

        Adapter(Context context, List<Content> contents) {
            mContext = context;
            mContents = contents;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_card, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final Content content = mContents.get(position);
            Glide.with(mContext).load(content.getPictureUrl()).into(holder.image);
            holder.title.setText(content.getTitle());
            holder.description.setText(content.getDescription());
            holder.fundProgressBar.setProgress(content.getCurrentPoint());
            holder.fundProgressBar.setMax(content.getGoal());
            holder.progressText.setText(PointUtils.getProgressPercent(content.getCurrentPoint(), content.getGoal()));
            holder.progressText2.setText(PointUtils.getProgressPercent2(content.getCurrentPoint(), content.getGoal()));
            holder.favoriteToggleButton.setChecked(content.isFavorite());

            mRealm = Realm.getDefaultInstance();
            User user = mRealm.where(User.class).findFirst();
            final String userId = user.getId();
            mRealm.close();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_DETAIL_CONTENT_ID, content.getId());
                    context.startActivity(intent);
                }
            });

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra(VideoActivity.EXTRA_VIDEO_CONTENT_ID, content.getId());
                    ((Activity)context).startActivityForResult(intent, MainActivity.REQUEST_DONATE);
                }
            });

            holder.favoriteToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        Call<Favorite> insertFavorite = NetworkManager.service.insertFavorite(userId, content.getId());
                        insertFavorite.enqueue(new Callback<Favorite>() {
                            @Override
                            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                                if (response.isSuccessful()) {
                                    Snackbar.make(getView(), R.string.message_add_favorite, Snackbar.LENGTH_SHORT).show();
                                    Log.e(TAG, "onResponse: ");
                                }
                            }

                            @Override
                            public void onFailure(Call<Favorite> call, Throwable t) {
                                Log.e(TAG, "onFailure: ");
                            }
                        });
                    } else {
                        Call<Favorite> deleteFavorite = NetworkManager.service.deleteFavorite(userId, content.getId());
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

            holder.linkImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.openWebPage(v.getContext(), content.getLinkUrl());
                }
            });
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
        @BindView(R.id.card_image)
        protected ImageView image;
        @BindView(R.id.card_title)
        protected TextView title;
        @BindView(R.id.card_text)
        protected TextView description;
        @BindView(R.id.action_button)
        protected Button button;
        @BindView(R.id.favorite_button)
        protected ToggleButton favoriteToggleButton;
        @BindView(R.id.link_button)
        protected ImageButton linkImageButton;
        @BindView(R.id.fund_progress_bar)
        protected ProgressBar fundProgressBar;
        @BindView(R.id.progress_text_view)
        protected TextView progressText;
        @BindView(R.id.progress_text_view2)
        protected TextView progressText2;

        public Holder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
