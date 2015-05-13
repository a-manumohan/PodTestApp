package com.pod.podtestapp.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.pod.podtestapp.BuildConfig;
import com.pod.podtestapp.model.Auth;
import com.pod.podtestapp.model.Organization;
import com.pod.podtestapp.util.PreferenceUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPreference;
import org.robolectric.shadows.ShadowPreferenceManager;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Created by manuMohan on 15/05/13.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class PodServiceManagerTest {
    PodServiceManager podServiceManager;
    @Mock
    PodService podService;
    @Mock
    PodAuthService podAuthService;
    @Mock
    Context context;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        podServiceManager = new PodServiceManager(podAuthService, podService, context);
    }

    @Test
    public void testAuthenticate() {
        String correctUsername = "correctUser", correctPassword = "correctPass";
        String wrongUsername = "wrongUser", wrongPassword = "wrongPass";
        Auth auth = new Auth();
        RetrofitError badRequestError = RetrofitError.httpError("", new Response("", 400, "", new ArrayList<>(), null), null, null);

        when(podAuthService.authenticate("password", correctUsername, correctPassword, "", "")).thenReturn(Observable.just(auth));
        when(podAuthService.authenticate("password", correctUsername, wrongPassword, "", "")).thenReturn(Observable.error(badRequestError));
        when(podAuthService.authenticate("password", wrongUsername, correctPassword, "", "")).thenReturn(Observable.error(badRequestError));


        Auth responseAuth = podServiceManager.authenticate(correctUsername, correctPassword).toBlocking().single();
        assertNotNull(responseAuth);

        try {
            responseAuth = podServiceManager.authenticate(correctUsername, wrongPassword).toBlocking().singleOrDefault(null);
            assertNull(responseAuth);
        } catch (RetrofitError error) {
            assertNotNull(error);
            assertNotNull(error.getResponse());
            assertEquals("Unexpected status code", 400, error.getResponse().getStatus());
        }
        try {
            responseAuth = podServiceManager.authenticate(wrongUsername, correctPassword).toBlocking().singleOrDefault(null);
            assertNull(responseAuth);
        } catch (RetrofitError error) {
            assertNotNull(error);
            assertNotNull(error.getResponse());
            assertEquals("Unexpected status code", 400, error.getResponse().getStatus());
        }
    }

    @Test
    public void testRefreshToken() {
        String correctRefreshToken = "1234", wrongRefreshToken = "3456";
        Auth auth = new Auth();
        RetrofitError unAuthError = RetrofitError.httpError("", new Response("", 401, "", new ArrayList<>(), null), null, null);
        when(podAuthService.refreshToken("refresh_token", "", "", correctRefreshToken)).thenReturn(Observable.just(auth));
        when(podAuthService.refreshToken("refresh_token", "", "", wrongRefreshToken)).thenReturn(Observable.error(unAuthError));

        Auth responseAuth = podAuthService.refreshToken("refresh_token", "", "", correctRefreshToken).toBlocking().single();
        assertNotNull(responseAuth);

        try {
            responseAuth = podAuthService.refreshToken("refresh_token", "", "", wrongRefreshToken).toBlocking().single();
            assertNull(responseAuth);
        } catch (RetrofitError error) {
            assertNotNull(error);
            assertNotNull(error.getResponse());
            assertEquals("Unexpected status code", 401, error.getResponse().getStatus());
        }
    }

    @Test
    public void testGetOrganizations() {
        String correctRefreshToken = "3456";
        ArrayList<Organization> organizations = new ArrayList<>();
        RetrofitError unAuthError = RetrofitError.httpError("", new Response("", 401, "", new ArrayList<>(), null), null, null);
        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PreferenceUtil.Session.ARG_ACCESS_TOKEN,"1234").apply();
        sharedPreferences.edit().putString(PreferenceUtil.Session.ARG_REFRESH_TOKEN,correctRefreshToken).apply();

        Auth auth = new Auth();
        auth.setAccessToken("1234");
        auth.setRefreshToken(correctRefreshToken);

        when(context.getSharedPreferences(PreferenceUtil.Session.ARG_SESSION, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);

        when(podAuthService.refreshToken("refresh_token", "", "", correctRefreshToken)).thenReturn(Observable.just(auth));

        when(podService.organizations()).thenReturn(Observable.just(organizations));
        assertNotNull(podServiceManager.getOrganizations().toBlocking().single());

        when(podService.organizations()).thenReturn(Observable.error(unAuthError))
                .thenReturn(Observable.just(organizations));
        assertNotNull(podServiceManager.getOrganizations().toBlocking().single());

        when(podService.organizations()).thenReturn(Observable.error(unAuthError))
                .thenReturn(Observable.error(unAuthError));

        try {
            assertNull(podServiceManager.getOrganizations().toBlocking().single());
        } catch (RetrofitError error) {
            assertNotNull(error);
            assertNotNull(error.getResponse());
            assertEquals("Unexpected status code", 401, error.getResponse().getStatus());
        }
    }

}
