package com.box_tech.fireworksmachine.device;

import android.support.annotation.NonNull;

/**
 * Created by scc on 2018/3/20.
 * 接收数据包回调
 */

public interface OnReceivePackage {
    void ack(@NonNull byte[] pkg);
    void timeout();
}
