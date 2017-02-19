package com.semistone.donately.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.semistone.donately.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by semistone on 2017-02-16.
 */

public class ContentFragment extends Fragment {

    private static final String numKey = "num";
    private int mNum;

    @BindView(R.id.image_view)
    protected ImageView mImageView;

    @BindView(R.id.title_text_view)
    protected TextView mTitleTextView;

    @BindView(R.id.description_text_view)
    protected TextView mDescriptionTextView;

    @BindView(R.id.beneficiary_text_view)
    protected TextView mBeneficiaryTextView;

    public static ContentFragment newInstance(int num) {

        ContentFragment fragment = new ContentFragment();

        Bundle args = new Bundle();
        args.putInt(numKey, num);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt(numKey) : 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        ButterKnife.bind(this, view);

        FragmentHelper.Content content = FragmentHelper.getContent(getActivity(), mNum);
        Glide.with(this).load(content.getImageResourceID()).into(mImageView);
        mTitleTextView.setText(content.getTitle());
        mDescriptionTextView.setText(content.getDescription());
        mBeneficiaryTextView.setText(content.getBeneficiary());
        return view;
    }

    @OnClick(R.id.card_view)
    protected void onClickCardView(View v) {
        Snackbar.make(v, R.string.card_view_encourage, Snackbar.LENGTH_SHORT).show();
    }
}
