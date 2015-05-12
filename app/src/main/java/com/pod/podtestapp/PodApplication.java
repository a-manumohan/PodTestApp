package com.pod.podtestapp;

import android.app.Application;

import com.pod.podtestapp.module.DaggerPodComponent;
import com.pod.podtestapp.module.PodComponent;
import com.pod.podtestapp.module.PodModule;

/**
 * Created by manuMohan on 15/05/11.
 */
public class PodApplication extends Application{
    private PodComponent mPodComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        mPodComponent = DaggerPodComponent.builder().podModule(new PodModule(this)).build();
    }

    public PodComponent getPodComponent() {
        return mPodComponent;
    }
}
