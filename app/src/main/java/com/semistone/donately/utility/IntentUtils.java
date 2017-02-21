package com.semistone.donately.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by semistone on 2017-02-21.
 */

public class IntentUtils {

    public static void openWebpage(Context context, String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
