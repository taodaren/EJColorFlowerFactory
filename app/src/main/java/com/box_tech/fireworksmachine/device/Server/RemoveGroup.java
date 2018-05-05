package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

/**
 * Created by scc on 2018/3/15.
 * 用户移除设备
 */

public class RemoveGroup {
    public interface OnFinished extends GeneralRequest.OnFinished<GeneralResult> {}

    public static class Request extends GeneralRequest<GeneralResult>{
        public Request(long member_id, String token, long group_id, Activity activity, RemoveGroup.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "rm_group");
            addField("member_id", member_id);
            addField("group_id", group_id);
            addField("token", token);
        }
    }
}
