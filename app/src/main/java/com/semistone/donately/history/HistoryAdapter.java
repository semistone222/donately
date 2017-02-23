package com.semistone.donately.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semistone.donately.R;
import com.semistone.donately.data.History;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by semistone on 2017-02-09.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {

    private final Context mContext;
    private final List<History> mHistoryList;
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm";

    public HistoryAdapter(Context context, List<History> historyList) {
        mContext = context;
        mHistoryList = historyList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.row_history, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        History history = mHistoryList.get(position);
        holder.data = history;
        holder.tvCount.setText("#" + String.valueOf(getItemCount() - position));
        holder.tvDonateTarget.setText(String.valueOf(history.getTitle()));
        long donateDate = history.getDonateDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String donateDateStr = dateFormat.format((long) donateDate * 1000);
        holder.tvDonateDate.setText(donateDateStr);
        holder.tvPoint.setText(String.valueOf(history.getPoint()) + "P");
        holder.itemView.setTag(history.getId());
    }

    @Override
    public int getItemCount() {
        if (mHistoryList == null) {
            return 0;
        } else {
            return mHistoryList.size();
        }
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
