package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

/**
 * Created by scc on 2018/3/15.
 * 用户移除设备
 */

public class RemoveDevice {
    public interface OnFinished extends GeneralRequest.OnFinished<GeneralResult> {}

    public static class Request extends GeneralRequest<GeneralResult>{
        public Request(long member_id, String token, long device_id, Activity activity, RemoveDevice.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "rm_device");
            addField("member_id", member_id);
            addField("device_id", device_id);
            addField("token", token);
        }
    }
}
