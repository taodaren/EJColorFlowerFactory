package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

/**
 * Created by scc on 2018/3/15.
 * 用户添加设备组
 */

public class AddGroup {
    public interface OnFinished extends GeneralRequest.OnFinished<GeneralResult> {}

    public static class Request extends GeneralRequest<GeneralResult> {
        public Request(long member_id, String token, @Nullable String group_name, Activity activity, AddGroup.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "add_group");
            addField("member_id", member_id);
            addField("token", token);
            if( group_name != null && !TextUtils.isEmpty(group_name)){
                addField("group_name", group_name);
            }
        }
    }
}
