package com.box_tech.fireworksmachine.device;

import android.support.annotation.NonNull;

/**
 * Created by scc on 2018/3/13.
 * 发送命令接口
 */

public interface ISendCommand {
    void sendCommand(@NonNull Device device, @NonNull byte[] pkg);
    void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback);
}
