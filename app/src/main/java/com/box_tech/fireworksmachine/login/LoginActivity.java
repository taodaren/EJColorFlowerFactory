package com.box_tech.fireworksmachine.login;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.box_tech.fireworksmachine.MainActivity;
import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.login.Server.Login;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.SuccessPageActivity;
import java.util.Locale;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView maPhoneNumberView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String username = Settings.get_username(this);
        String password = Settings.get_password(this);

        // Set up the login form.
        maPhoneNumberView = findViewById(R.id.et_phone_number);
        if(username!=null){
            maPhoneNumberView.setText(username);
        }

        mPasswordView = findViewById(R.id.password);
        if(password!=null){
            mPasswordView.setText(password);
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        new PrepareRequest<Login.Result>(this, R.id.sign_in_button,
                R.id.password, R.id.et_phone_number){
            @Override
            protected void onCreateRequest(GeneralRequest.OnFinished<Login.Result> callback, String... values) {
                super.onCreateRequest(callback, values);
                showProgress(true);
                new Login.Request(values[0], values[1], LoginActivity.this, callback).run();
            }

            @Override
            protected void onOK(@NonNull Activity activity, @NonNull Login.Result result) {
                super.onOK(activity, result);
                ((LoginActivity)activity).onOK(result);
            }

            @Override
            protected void onFailed(@NonNull Activity activity, @NonNull String result) {
                super.onFailed(activity, result);
                ((LoginActivity)activity).onFailed(result);
            }
        };
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void onOK(@NonNull Login.Result result){
        showProgress(false);
        switch (result.getCode()){
            case Login.ResultCode.OK:
                Log.i("LoginActivity", "login success "+result.getData().getToken()+" "+result.getData().getMember_id());
                Settings.storeLoginInfo(this, maPhoneNumberView.getText().toString(), mPasswordView.getText().toString());
                Settings.storeMemberID(this, result.getData().getMember_id());
                SuccessPageActivity.start_me(this, R.string.login_success, MainActivity.class);
                finish();
                break;
            case Login.ResultCode.INCORRECT:
                mPasswordView.setError(result.getMessage());
                mPasswordView.requestFocus();
                break;
            default:
                onFailed(result.getMessage());
                break;
        }
    }

    private void onFailed(@NonNull String message){
        showProgress(false);
        mPasswordView.setError(String.format(Locale.CHINA, getString(R.string.error_login_failed), message));
        mPasswordView.requestFocus();
    }

    public void onClickRegister(@SuppressWarnings("unused") View view){
        RegisterActivity.start_me(this, maPhoneNumberView.getText().toString());
        finish();
    }

    public void onClickResetPassword(@SuppressWarnings("unused") View view){
        ResetPasswordActivity.start_me(this, maPhoneNumberView.getText().toString());
        finish();
    }
}

