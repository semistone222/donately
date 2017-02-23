package com.semistone.donately.utility;

import com.semistone.donately.data.History;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by semistone on 2017-02-22.
 */

public class PointUtils {

    public static int calculate(int adLength, boolean isClicked) {
        int point = adLength;
        if (isClicked) {
            point *= 2;
        }
        return point;
    }

    public static String getProgressPercent(int current, int goal) {
        float minority = (float) current / (float) goal;
        int percent = (int) (minority * 100);
        return percent + "%";
    }
}
