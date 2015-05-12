package com.pod.podtestapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pod.podtestapp.PodApplication;
import com.pod.podtestapp.R;
import com.pod.podtestapp.network.PodServiceManager;
import com.pod.podtestapp.util.PreferenceUtil;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String ARG_LOADING = "arg_loading";
    private static final String ARG_USERNAME = "arg_username";
    private static final String ARG_PASSWORD = "arg_password";

    @Inject
    protected PodServiceManager mPodServiceManager;
    private ProgressDialog mProgressDialog;
    private Subscription mLoginSubscription;

    private boolean isLoading = false;
    private String mUsername, mPassword;

    private EditText mUserNameEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((PodApplication)getApplication()).getPodComponent().inject(this);
        if (savedInstanceState != null) {
            isLoading = savedInstanceState.getBoolean(ARG_LOADING, false);
            mUsername = savedInstanceState.getString(ARG_USERNAME, "");
            mPassword = savedInstanceState.getString(ARG_PASSWORD, "");
        }

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginSubscription != null)
            mLoginSubscription.unsubscribe();
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
    }

    private void initViews() {
        mUserNameEditText = (EditText) findViewById(R.id.username);
        mPasswordEditText = (EditText) findViewById(R.id.password);
        mUserNameEditText.setText(mUsername);
        mPasswordEditText.setText(mPassword);

        mUserNameEditText.setOnEditorActionListener((textView, action, keyEvent) -> {
            if (action == EditorInfo.IME_ACTION_NEXT) {
                mPasswordEditText.requestFocus();
                return true;
            }
            return false;
        });
        mPasswordEditText.setOnEditorActionListener((textView, action, keyEvent) -> {
            if (action == 111) {
                validateAndLogin();
                return true;
            }
            return false;
        });

        Button loginButton = (Button) findViewById(R.id.sign_in);
        loginButton.setOnClickListener(view -> {
            validateAndLogin();
        });
        if (isLoading) {
            login(mUsername, mPassword);
        }
    }

    private void validateAndLogin() {
        String username = mUserNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            mUserNameEditText.setError(getString(R.string.message_direction_enter_username));
            return;
        }
        String password = mPasswordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.message_direction_enter_password));
            return;
        }
        mUserNameEditText.setError(null);
        mPasswordEditText.setError(null);
        login(username, password);
    }

    private void login(String username, String password) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setTitle(getString(R.string.message_title_signing_in));
        mProgressDialog.setMessage(getString(R.string.message_message_please_wait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        isLoading = true;

        mLoginSubscription = mPodServiceManager.authenticate(username, password).
                subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(
                        auth -> {
                            PreferenceUtil.Session.setAccessToken(this, auth.getAccessToken());
                            PreferenceUtil.Session.setRefreshToken(this, auth.getRefreshToken());
                            proceedToHome();
                        },
                        throwable -> {
                            isLoading = false;
                            mProgressDialog.hide();
                            RetrofitError retrofitError = (RetrofitError) throwable;
                            switch (retrofitError.getResponse().getStatus()) {
                                case 400:
                                    showUsernamePasswordWrongMessage();
                                    break;
                                default:
                                    showGenericErrorMessage();
                            }
                        },
                        () -> {
                            mProgressDialog.hide();
                            isLoading = false;
                        }
                );
    }

    private void showGenericErrorMessage() {
        Toast.makeText(this, getString(R.string.message_error_generic), Toast.LENGTH_SHORT).show();
    }

    private void showUsernamePasswordWrongMessage() {
        Toast.makeText(this, getString(R.string.message_error_wrong_username_password), Toast.LENGTH_SHORT).show();
    }

    private void proceedToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(ARG_LOADING, isLoading);
        outState.putString(ARG_USERNAME, mUsername);
        outState.putString(ARG_PASSWORD, mPassword);
    }
}
