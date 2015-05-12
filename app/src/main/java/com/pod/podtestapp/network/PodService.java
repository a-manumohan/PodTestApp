package com.pod.podtestapp.network;

import com.pod.podtestapp.model.Auth;
import com.pod.podtestapp.model.Organization;

import java.util.ArrayList;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by manuMohan on 15/05/11.
 */
public interface PodService {
    @GET("/org/")
    Observable<ArrayList<Organization>>organizations();
}
