package com.box_tech.fireworksmachine.device.Server;

import android.app.Activity;

import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.utils.GeneralRequest;

/**
 * Created by scc on 2018/3/15.
 * 获取设备列表
 */

public class GetDeviceList {
    public interface OnFinished extends GeneralRequest.OnFinished<Result> {}

    public static class Request extends GeneralRequest<Result>{
        public Request(long member_id, Activity activity, GetDeviceList.OnFinished callback){
            super(activity, callback);
            setUrl(Settings.SERVER_URL + "device_list");
            addField("member_id", member_id);
        }
    }

    @SuppressWarnings("unused")
    public static class Result {
        private int code;
        private String message;
        private List data;

        @SuppressWarnings("unused")
        public static class DeviceInformation{
            private long id;
            private String mac;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }
        }

        @SuppressWarnings("unused")
        public static class List {
            DeviceInformation[] list;

            public DeviceInformation[] getList() {
                return list;
            }

            public void setList(DeviceInformation[] list) {
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
