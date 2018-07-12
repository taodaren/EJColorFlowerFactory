package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

public class BindDevice {
    public interface OnFinished extends GeneralRequest.OnFinished<GeneralResult> {}

    public static class Request  extends GeneralRequest<GeneralResult>{
        public Request(String mac, String token, Activity activity, AddDevice.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "bindDevice_App");
            addField("mac", mac);
            addField("token", token);
        }
    }
}
