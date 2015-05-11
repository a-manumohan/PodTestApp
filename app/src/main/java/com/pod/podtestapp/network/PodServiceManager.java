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

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by manuMohan on 15/05/11.
 */
public class PodServiceManager {
    private Context mContext;
    private PodService mPodService;
    private String apiKey, apiSecret;

    public PodServiceManager(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            apiKey = applicationInfo.metaData.getString("POD_API_KEY");
            apiSecret = applicationInfo.metaData.getString("POD_API_SECRET");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                String accessToken = PreferenceUtil.Session.getAccessToken(mContext);
                if (!TextUtils.isEmpty(accessToken)) {
                    request.addHeader("Authorization", "“OAuth2 " + accessToken);
                }
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        mPodService = restAdapter.create(PodService.class);
    }

    public Observable<Auth> authenticate(String username, String password) {
        return mPodService.authenticate("password", username, password, apiKey, apiSecret);
    }

    public Observable<ArrayList<Organization>> getOrganizations() {
        return mPodService.organizations();
    }
}
