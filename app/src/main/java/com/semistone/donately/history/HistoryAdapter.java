package com.semistone.donately.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semistone.donately.R;
import com.semistone.donately.data.Content;
import com.semistone.donately.data.History;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by semistone on 2017-02-09.
 */

public class HistoryAdapter extends RealmRecyclerViewAdapter<History, HistoryAdapter.Holder> {

    private final static String DATE_FORMAT = "yy/MM/dd HH:mm";

    public HistoryAdapter(Context context, OrderedRealmCollection<History> data) {
        super(context, data, true);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        History history = getData().get(position);
        holder.data = history;
        holder.tvCount.setText(String.valueOf(getItemCount() - position));
        Realm realm = Realm.getDefaultInstance();
        Content content = realm.where(Content.class).equalTo(Content.ID, history.getContentId()).findFirst();
        holder.tvDonateTarget.setText(content.getTitle());
        realm.close();
        long donateDate = history.getDonateDate();
        String donateDateStr = (new SimpleDateFormat(DATE_FORMAT, Locale.US)).format(donateDate);
        holder.tvDonateDate.setText(donateDateStr);
        holder.tvPoint.setText(String.valueOf(history.getPoint()));
        holder.itemView.setTag(history.getId());
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_count)
        protected TextView tvCount;

        @BindView(R.id.tv_donate_target)
        protected TextView tvDonateTarget;

        @BindView(R.id.tv_donate_date)
        protected TextView tvDonateDate;

        @BindView(R.id.tv_point)
        protected TextView tvPoint;

        public History data;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
