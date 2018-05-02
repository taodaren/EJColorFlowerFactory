package com.box_tech.fireworksmachine.login.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

/**
 * Created by scc on 2018/3/15.
 * 注册请求
 */

public class ResetPassword {
    public static class Request extends GeneralRequest<GeneralResult>{
        public Request(String phoneNumber, String password, String  passwordConfirm,
                                    String verifyCode, Activity activity,
                                    GeneralRequest.OnFinished<GeneralResult> callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "pwdFind");
            addField("mobile", phoneNumber);
            addFieldEncrypt("password", password);
            addFieldEncrypt("re_password", passwordConfirm);
            addField("code", verifyCode);
        }
    }

    public static class ResultCode {
        public final static int OK = 1;
        public final static int INCORRECT_PHONE_NUMBER = 5;
        public final static int INCORRECT_VERIFY_CODE = 6;
        public final static int PASSWORD_MISMATCH = 8;
    }
}
