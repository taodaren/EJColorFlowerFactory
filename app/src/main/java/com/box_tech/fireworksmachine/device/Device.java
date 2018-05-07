package com.box_tech.fireworksmachine.device;

import android.support.annotation.NonNull;

/**
 * Created by scc on 2018/3/2.
 *
 */

@SuppressWarnings("unused")
public class Device {
    DeviceConfig config = null;
    DeviceState state = null;
    private final String address;
    private long id = 0; // from server
    private boolean connected = false;

    public Device(@NonNull String address){
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public DeviceConfig getConfig() {
        return config;
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState mState) {
        this.state = mState;
    }

    public void setConfig(DeviceConfig config) {
        this.config = config;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public long getId() {
        return (config==null)?id : config.mID;
    }

    public void setId(long id) {
        this.id = id;
    }
}
