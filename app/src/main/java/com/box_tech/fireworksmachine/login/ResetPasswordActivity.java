package com.box_tech.fireworksmachine.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.login.Server.ResetPassword;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;
import com.box_tech.fireworksmachine.utils.SuccessPageActivity;

import java.util.Locale;

public class ResetPasswordActivity extends AppCompatActivity {
    private AutoCompleteTextView maPhoneNumberView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private EditText mVerifyCodeView;
    private View mProgressView;
    private View mRegisterFormView;
    private PrepareGetVerifyCodeRequest mPrepareGetVerifyCodeRequest;

    public static void start_me(Context context, String phoneNumber){
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        // Set up the login form.
        maPhoneNumberView = findViewById(R.id.et_phone_number);
        if(phoneNumber!=null){
            maPhoneNumberView.setText(phoneNumber);
        }

        mPasswordView = findViewById(R.id.password);
        mRePasswordView = findViewById(R.id.re_password);
        mVerifyCodeView = findViewById(R.id.verify_code);
        mRegisterFormView = findViewById(R.id.form);
        mProgressView = findViewById(R.id.progress);

        new PrepareRequest<GeneralResult>(this,
                R.id.bt_go, R.id.verify_code,
                R.id.et_phone_number,
                R.id.password,
                R.id.re_password){
            @Override
            protected void onCreateRequest(GeneralRequest.OnFinished<GeneralResult> callback, String... values) {
                super.onCreateRequest(callback, values);
                showProgress(true);
                new ResetPassword.Request(values[0], values[1], values[2], values[3],
                        ResetPasswordActivity.this, callback).run();
            }

            @Override
            protected void onOK(@NonNull Activity activity, @NonNull GeneralResult result) {
                super.onOK(activity, result);
                ((ResetPasswordActivity)activity).onOK(result);
            }

            @Override
            protected void onFailed(@NonNull Activity activity, @NonNull String result) {
                super.onFailed(activity, result);
                ((ResetPasswordActivity)activity).onFailed(result);
            }
        };

        mPrepareGetVerifyCodeRequest = new PrepareGetVerifyCodeRequest(this,
                R.id.bt_get_verify_code, R.id.et_phone_number, new Handler());
    }

    @Override
    protected void onDestroy() {
        mPrepareGetVerifyCodeRequest.clear();
        super.onDestroy();
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void onOK(@NonNull GeneralResult result){
        showProgress(false);
        switch (result.getCode()){
            case ResetPassword.ResultCode.OK:
                Settings.storeSessionInfo(this, new LoginSession(
                        maPhoneNumberView.getText().toString(),
                        mPasswordView.getText().toString()));
                SuccessPageActivity.start_me(this, R.string.reset_password_success, LoginActivity.class);
                finish();
                break;
            case ResetPassword.ResultCode.INCORRECT_VERIFY_CODE:
                mVerifyCodeView.setError(result.getMessage());
                mVerifyCodeView.requestFocus();
                break;
            case ResetPassword.ResultCode.INCORRECT_PHONE_NUMBER:
                maPhoneNumberView.setError(result.getMessage());
                maPhoneNumberView.requestFocus();
                break;
            case ResetPassword.ResultCode.PASSWORD_MISMATCH:
                mRePasswordView.setError(result.getMessage());
                mRePasswordView.requestFocus();
                break;
            default:
                onFailed(result.getMessage());
                break;
        }
    }

    private void onFailed(@NonNull String message){
        showProgress(false);
        Toast.makeText(this, String.format(Locale.CHINA, getString(R.string.error_reset_password_failed), message), Toast.LENGTH_SHORT).show();
    }
}
