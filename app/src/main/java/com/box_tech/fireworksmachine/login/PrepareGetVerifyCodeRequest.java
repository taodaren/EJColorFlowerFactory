package com.box_tech.fireworksmachine.login;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.login.Server.GetVerifyCode;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

import java.util.Locale;

/**
 * Created by scc on 2018/3/21.
 * 获取验证码
 */

class PrepareGetVerifyCodeRequest extends PrepareRequest<GeneralResult> {
    private final Handler mHandler;
    private int mRestTime = 0;

    @SuppressWarnings("SameParameterValue")
    PrepareGetVerifyCodeRequest(@NonNull Activity activity, @IdRes int button_go, @IdRes int phoneNumber, Handler handler){
        super(activity, button_go, 0, phoneNumber);
        mHandler = handler;
    }

    @Override
    protected void onCreateRequest(GeneralRequest.OnFinished<GeneralResult> callback, String... values) {
        super.onCreateRequest(callback, values);
        if(mRestTime<=0){
            new GetVerifyCode.Request(values[0], mActivity, callback).run();
        }
        else{
            onFinal();
        }
    }

    @Override
    protected void onOK(@NonNull Activity activity, @NonNull GeneralResult result) {
        super.onOK(activity, result);
        switch (result.getCode()){
            case GetVerifyCode.ResultCode.OK:
                mButtonGo.setEnabled(false);
                mRestTime = 300;
                mRefreshTime.run();
                Toast.makeText(mActivity, R.string.send_verify_code_success, Toast.LENGTH_SHORT).show();
                break;
            case GetVerifyCode.ResultCode.LIMIT_REACH:
                mButtonGo.setText(result.getMessage());
                break;
            case GetVerifyCode.ResultCode.REQUEST_LATER:
                mRestTime = 300;
                mRefreshTime.run();
                break;
        }
    }

    @Override
    protected void onFailed(@NonNull Activity activity, @NonNull String message) {
        super.onFailed(activity, message);
        Toast.makeText(activity,
                String.format(Locale.CHINA, activity.getString(R.string.get_verify_code_failed), message),
                Toast.LENGTH_SHORT).show();
    }

    private final Runnable mRefreshTime = new Runnable() {
        @Override
        public void run() {
            if(mRestTime>0){
                mRestTime--;
                mButtonGo.setText(String.format(Locale.CHINA, mActivity.getString(R.string.get_verify_code_later), mRestTime));
                mHandler.postDelayed(mRefreshTime, 1000);
            }
            else{
                mButtonGo.setText(R.string.get_verify_code);
                mButtonGo.setEnabled(true);
            }
        }
    };

    void clear(){
        mHandler.removeCallbacks(mRefreshTime);
    }
}
