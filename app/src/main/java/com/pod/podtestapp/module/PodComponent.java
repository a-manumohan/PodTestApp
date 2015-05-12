package com.pod.podtestapp.module;

import com.pod.podtestapp.activity.LoginActivity;
import com.pod.podtestapp.fragment.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by manuMohan on 15/05/12.
 */
@Singleton
@Component(modules = PodModule.class)
public interface PodComponent {
    void inject(LoginActivity activity);
    void inject(HomeFragment fragment);
}
