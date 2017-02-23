package com.semistone.donately.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by semistone on 2017-02-23.
 */

public class NetworkManager {

    private static final String baseUrl = "http://192.168.1.26:52273";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static NetworkService service = retrofit.create(NetworkService.class);
}
