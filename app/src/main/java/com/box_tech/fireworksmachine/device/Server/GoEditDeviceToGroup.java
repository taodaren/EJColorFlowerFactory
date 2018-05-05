package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;

/**
 * Created by scc on 2018/3/15.
 * 获取添加设备到用户组的可用设备列表和组内设备列表
 */

public class GoEditDeviceToGroup {
    public interface OnFinished extends GeneralRequest.OnFinished<Result>{}

    public static class Request extends GeneralRequest<Result> {
        public Request(long member_id, String token, long group_id, Activity activity, GoEditDeviceToGroup.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "goEditDeviceToGroup");
            addField("member_id", member_id);
            addField("token", token);
            addField("group_id", group_id);
        }
    }

    @SuppressWarnings("unused")
    public static class Data{
        private long[] list;
        private long[] possess;

        public long[] getList() {
            return list;
        }

        public void setList(long[] list) {
            this.list = list;
        }

        public long[] getPossess() {
            return possess;
        }

        public void setPossess(long[] possess) {
            this.possess = possess;
        }
    }

    @SuppressWarnings("unused")
    public static class Result {
        private int code;
        private String message;
        private Data data;

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

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }
}
