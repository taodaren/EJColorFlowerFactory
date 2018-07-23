package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;
import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;

public class BindDevice {
    public interface OnFinished extends GeneralRequest.OnFinished<Result> {}

    public static class Request  extends GeneralRequest<Result>{
        public Request(String mac, String token, Activity activity, BindDevice.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "bindDevice_App");
            addField("mac", mac);
            addField("token", token);
        }
    }


    @SuppressWarnings("unused")
    public static class Result {
        private int code;
        private String message;
        private String data;


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

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
