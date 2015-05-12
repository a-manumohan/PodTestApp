package com.pod.podtestapp.module;

import android.content.Context;

import com.pod.podtestapp.network.PodServiceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by manuMohan on 15/05/12.
 */
@Module
public class PodModule {
    private Context mContext;

    public PodModule(Context context) {
        mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    public PodServiceManager providePodServiceManager(Context context) {
        return new PodServiceManager(context);
    }
}
