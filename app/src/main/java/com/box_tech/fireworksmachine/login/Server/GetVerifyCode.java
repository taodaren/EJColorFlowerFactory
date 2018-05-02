package com.box_tech.fireworksmachine.login.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

/**
 * Created by scc on 2018/3/15.
 * 注册请求
 */

public class GetVerifyCode {
    public static class Request extends GeneralRequest<GeneralResult>{
        public Request(String phoneNumber, Activity activity,
                                    GeneralRequest.OnFinished<GeneralResult> callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "sendMsg");
            System.out.println("验证码获取 "+phoneNumber+" len = "+phoneNumber.length());
            addFieldEncrypt("mobile", phoneNumber);
        }
    }

    public static class ResultCode{
        public final static int OK = 1;
        public final static int LIMIT_REACH = 3;
        public final static int REQUEST_LATER = 4;
    }
}
