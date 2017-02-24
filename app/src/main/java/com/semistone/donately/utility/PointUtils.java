package com.semistone.donately.utility;

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

    public static String getProgressPercent2(int current, int goal) {
        return current + " / " + goal;
    }
}
