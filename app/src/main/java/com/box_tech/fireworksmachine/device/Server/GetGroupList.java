package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;

/**
 * Created by scc on 2018/3/15.
 * 获取用户组列表以及每个组中的设备列表
 */

public class GetGroupList {
    public interface OnFinished extends GeneralRequest.OnFinished<Result>{}

    public static class Request extends GeneralRequest<Result> {
        public Request(long member_id, String token, Activity activity, GetGroupList.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "getDeviceGroupList");
            addField("member_id", member_id);
            addField("token", token);
        }
    }

    @SuppressWarnings("unused")
    public static class Result {
        private int code;
        private String message;
        private GroupInfo[] data;

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

        public GroupInfo[] getData() {
            return data;
        }

        public void setData(GroupInfo[] data) {
            this.data = data;
        }
    }

    public static class ResultCode{
        public final static int OK = 1;
    }
}
