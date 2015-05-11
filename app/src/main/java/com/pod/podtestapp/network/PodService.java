package com.pod.podtestapp.network;

import com.pod.podtestapp.model.Auth;
import com.pod.podtestapp.model.Organization;

import java.util.ArrayList;

import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by manuMohan on 15/05/11.
 */
public interface PodService {

    @POST("/oauth/token")
    Observable<Auth> authenticate(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );


    @GET("/org/")
    Observable<ArrayList<Organization>>organizations();
}
