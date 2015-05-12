package com.pod.podtestapp.network;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.pod.podtestapp.Constants;
import com.pod.podtestapp.model.Auth;
import com.pod.podtestapp.model.Organization;
import com.pod.podtestapp.util.PreferenceUtil;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by manuMohan on 15/05/11.
 */
@Singleton
public class PodServiceManager {
    private static final String POD_API_KEY = "POD_API_KEY";
    private static final String POD_API_SECRET = "POD_API_SECRET";
    private PodService mPodService;
    private PodAuthService mPodAuthService;
    private String apiKey, apiSecret;
    private Context mContext;

    @Inject
    public PodServiceManager(Context context) {
        mContext = context;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            apiKey = applicationInfo.metaData.getString(POD_API_KEY);
            apiSecret = applicationInfo.metaData.getString(POD_API_SECRET);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        RestAdapter authRestAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        mPodAuthService = authRestAdapter.create(PodAuthService.class);

        RequestInterceptor requestInterceptor = request -> {
            String accessToken = PreferenceUtil.Session.getAccessToken(context);
            if (!TextUtils.isEmpty(accessToken)) {
                request.addHeader("Authorization", "OAuth2 " + accessToken);
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        mPodService = restAdapter.create(PodService.class);
    }

    public Observable<Auth> authenticate(String username, String password) {

        return mPodAuthService.authenticate("password", username, password, apiKey, apiSecret);
    }

    private Observable<Auth> refreshToken() {
        return mPodAuthService.refreshToken("refresh_token", apiKey, apiSecret, PreferenceUtil.Session.getRefreshToken(mContext));
    }

    public Observable<ArrayList<Organization>> getOrganizations() {
        return mPodService.organizations().onErrorResumeNext(refreshTokenAndRetry(mPodService.organizations()));
    }

    /**
     * Retries once if the request gets a 401
     *
     * @param observableToRetry the observable that will be retried after getting a new token
     * @param <T>type of the object emitted by observable
     * @return error if not 401 , otherwise observable to retry.
     */
    private <T> Func1<Throwable, ? extends Observable<? extends T>> refreshTokenAndRetry(final Observable<T> observableToRetry) {
        return throwable -> {
            RetrofitError error = (RetrofitError) throwable;
            if (error.getResponse().getStatus() == 401) {
                if (TextUtils.isEmpty(PreferenceUtil.Session.getAccessToken(mContext))) {
                    return Observable.error(throwable);
                }
                PreferenceUtil.Session.setAccessToken(mContext, "");
                return refreshToken().flatMap(auth -> {
                    PreferenceUtil.Session.setAccessToken(mContext, auth.getAccessToken());
                    PreferenceUtil.Session.setRefreshToken(mContext, auth.getRefreshToken());
                    return observableToRetry;
                });
            }
            return Observable.error(throwable);
        };
    }
}
