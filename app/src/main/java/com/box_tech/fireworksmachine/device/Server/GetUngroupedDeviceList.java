package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;

/**
 * Created by scc on 2018/3/15.
 * 获取没有分到用户组的设备列表
 */

public class GetUngroupedDeviceList {
    public static class Request extends GeneralRequest<Result>{
        public Request(long member_id, Activity activity, GetUngroupedDeviceList.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "goAddDeviceToGroup");
            addField("member_id", member_id);
        }
    }

    public interface OnFinished extends GeneralRequest.OnFinished<Result>{}

    @SuppressWarnings("unused")
    public static class Result{
        private int code;
        private String message;
        private List data;

        @SuppressWarnings("unused")
        public static class List {
            private long[] list;

            public long[] getList() {
                return list;
            }

            public void setList(long[] list) {
                this.list = list;
            }
        }

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

        public List getData() {
            return data;
        }

        public void setData(List data) {
            this.data = data;
        }
    }
}
