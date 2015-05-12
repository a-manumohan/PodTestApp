package com.pod.podtestapp.network;

import com.pod.podtestapp.model.Auth;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by manuMohan on 15/05/12.
 */
public interface PodAuthService {
    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<Auth> authenticate(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );

    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<Auth> refreshToken(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("refresh_token") String refreshToken
    );
}
