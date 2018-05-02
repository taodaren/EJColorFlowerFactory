package com.box_tech.fireworksmachine.device.Server;

import com.google.gson.annotations.SerializedName;

/**
 * Created by scc on 2018/3/21.
 *  组信息
 */

@SuppressWarnings("unused")
public class GroupInfo {
    @SerializedName("group_list")
    private long[] device_list;// 设备ID列表
    private long group_id;
    private String update_time;
    private String group_name;

    public long[] getDevice_list() {
        return device_list;
    }

    public void setDevice_list(long[] device_list) {
        this.device_list = device_list;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
}
