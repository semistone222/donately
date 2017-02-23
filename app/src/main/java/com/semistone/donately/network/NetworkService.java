package com.semistone.donately.network;

import com.semistone.donately.data.Advertisement;
import com.semistone.donately.data.Content;
import com.semistone.donately.data.Favorite;
import com.semistone.donately.data.History;
import com.semistone.donately.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by semistone on 2017-02-23.
 */

public interface NetworkService {
    @GET("/users/{user_id}/contents")
    Call<List<Content>> getContents(@Path("user_id") String user_id);

    @GET("/users/{user_id}/type/{type}/contents")
    Call<List<Content>> getContentsByType(@Path("user_id") String user_id,
                                          @Path("type") String type);

    @GET("/users/{user_id}/favorite/contents")
    Call<List<Content>> getFavoriteContents(@Path("user_id") String user_id);

    @GET("/users/{user_id}/contents/{content_id}")
    Call<Content> getContent(@Path("user_id") String user_id,
                             @Path("content_id") int content_id);

    @GET("/users/{user_id}/histories")
    Call<List<History>> getHistories(@Path("user_id") String user_id);

    @FormUrlEncoded
    @POST("/histories")
    Call<History> insertHistory(@Field("user_id") String user_id,
                                @Field("content_id") int content_id,
                                @Field("advertisement_id") int advertisement_id,
                                @Field("isclicked") int isClicked,
                                @Field("point") int point);

    @FormUrlEncoded
    @POST("/favorites")
    Call<Favorite> insertFavorite(@Field("user_id") String user_id,
                                  @Field("content_id") int content_id);

    @DELETE("/users/{user_id}/contents/{content_id}/favorites")
    Call<Favorite> deleteFavorite(@Path("user_id") String user_id,
                                  @Path("content_id") int content_id);

    @FormUrlEncoded
    @POST("/users")
    Call<User> insertUser(@Field("user_id") String user_id,
                          @Field("name") String name,
                          @Field("email") String email,
                          @Field("access_token") String access_token,
                          @Field("type") String type,
                          @Field("photo_url") String photo_url);

    @GET("/users/{user_id}")
    Call<User> getUser(@Path("user_id") String user_id);

    @GET("advertisement/length/{ad_length}")
    Call<Advertisement> getAdvertisement(@Path("ad_length") int ad_length);
}
