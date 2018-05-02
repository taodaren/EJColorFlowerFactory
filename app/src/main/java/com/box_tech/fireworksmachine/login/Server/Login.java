package com.box_tech.fireworksmachine.login.Server;

import android.app.Activity;
import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;

/**
 * Created by scc on 2018/3/15.
 * 登录请求
 */

public class Login {
    public static class Request extends GeneralRequest<Result> {
        public Request(String phoneNumber, String password, Activity activity, GeneralRequest.OnFinished<Result> callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "login");
            addField("mobile", phoneNumber);
            addFieldEncrypt("password", password);
        }
    }

    @SuppressWarnings("unused")
    public static class Result {

        private int code;
        private String message;
        private LoginData data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LoginData getData() {
            return data;
        }

        public void setData(LoginData data) {
            this.data = data;
        }
    }

    public static class ResultCode{
        public final static int OK = 1;
        public final static int INCORRECT = 4;
    }
}
