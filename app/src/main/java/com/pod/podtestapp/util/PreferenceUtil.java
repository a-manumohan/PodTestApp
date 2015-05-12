package com.pod.podtestapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by manuMohan on 15/05/11.
 */
public abstract class PreferenceUtil {
    public static class Session {
        private static final String ARG_SESSION = "session";
        private static final String ARG_ACCESS_TOKEN = "access_token";
        private static final String ARG_REFRESH_TOKEN = "refresh_token";

        public static void setAccessToken(Context context, String accessToken) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(ARG_SESSION, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(ARG_ACCESS_TOKEN, accessToken).apply();
        }

        public static String getAccessToken(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(ARG_SESSION, Context.MODE_PRIVATE);
            return sharedPreferences.getString(ARG_ACCESS_TOKEN, "");
        }
        public static void setRefreshToken(Context context, String refreshToekn) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(ARG_SESSION, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(ARG_REFRESH_TOKEN, refreshToekn).apply();
        }

        public static String getRefreshToken(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(ARG_SESSION, Context.MODE_PRIVATE);
            return sharedPreferences.getString(ARG_REFRESH_TOKEN, "");
        }
    }
}
