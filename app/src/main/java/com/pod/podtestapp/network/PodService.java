package com.pod.podtestapp.network;

import com.pod.podtestapp.model.Organization;

import java.util.ArrayList;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by manuMohan on 15/05/11.
 */
interface PodService {
    @GET("/org/")
    Observable<ArrayList<Organization>>organizations();
}
