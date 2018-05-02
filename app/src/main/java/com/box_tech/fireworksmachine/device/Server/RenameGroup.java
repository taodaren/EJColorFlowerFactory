package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

/**
 * Created by scc on 2018/3/15.
 * 用户添加设备组
 */

public class RenameGroup {
    public interface OnFinished extends GeneralRequest.OnFinished<GeneralResult> {}

    public static class Request extends GeneralRequest<GeneralResult> {
        public Request(long group_id, String group_name, Activity activity, RenameGroup.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "renameGroup");
            addField("group_id", group_id);
            addField("group_name", group_name);
        }
    }
}
