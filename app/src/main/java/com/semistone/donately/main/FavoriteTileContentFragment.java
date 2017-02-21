package com.semistone.donately.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;
import com.semistone.donately.data.Content;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by semistone on 2017-02-20.
 */

public class FavoriteTileContentFragment extends Fragment {

    private Realm mRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ButterKnife.bind(this, recyclerView);
        mRealm = Realm.getDefaultInstance();
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext(), mRealm.where(Content.class).equalTo(Content.FAVORITE, true).findAll());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        recyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        return recyclerView;
    }

    public static class ContentAdapter extends RealmRecyclerViewAdapter<Content, ViewHolder> {

        private Context mContext;

        public ContentAdapter(Context context, OrderedRealmCollection<Content> data)  {
            super(context, data, true);
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Content content = getData().get(position);
            holder.data = content;
            Glide.with(mContext).load(content.getPictureUrl()).into(holder.picture);
            holder.title.setText(content.getTitle());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tile_picture)
        protected ImageView picture;
        @BindView(R.id.tile_title)
        protected TextView title;

        private Content data;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_tile, parent, false));
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.CONTENT_ID, data.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}