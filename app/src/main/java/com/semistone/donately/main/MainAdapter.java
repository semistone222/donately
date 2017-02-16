package com.semistone.donately.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.semistone.donately.Cheeses;
import com.semistone.donately.R;

/**
 * Created by semistone on 2017-02-15.
 */

public class MainAdapter extends FragmentStatePagerAdapter {

    String TAG = this.getClass().getName();

    private static String[] beneficiaries = {"Poverty", "Refugee", "Animal", "Environment"};
    private static final int NUM_ITEMS = beneficiaries.length;

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount: " + NUM_ITEMS);
        return NUM_ITEMS;
    }

    // TODO: 2017-02-15 왜 안불러와 지지 ㅠ 페이지가 안보여
    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem: ");
        return ArrayListFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i(TAG, "getPageTitle: ");
        return beneficiaries[position];
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;

        static String TAG = "ArrayListFragment";

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            Log.i(TAG, "newInstance: ");
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView) tv).setText("Fragment #" + mNum);
            Log.i("FragmentList abcdef", "Item clicked: ");
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
}
