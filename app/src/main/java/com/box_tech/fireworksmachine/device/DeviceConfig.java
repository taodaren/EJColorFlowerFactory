package com.box_tech.fireworksmachine.device;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by scc on 2018/3/2.
 * Device configuration
 */

public class DeviceConfig implements Parcelable {
    public long mID = 0;
    public int mTemperatureThresholdLow = 500;
    public int mTemperatureThresholdHigh = 510;
    public int[] mMotorDefaultSpeed = new int[]{20,10,15,200};
    public int mDMXAddress = 8;
    public int mGualiaoTime = 0;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mID);
        dest.writeInt(this.mTemperatureThresholdLow);
        dest.writeInt(this.mTemperatureThresholdHigh);
        dest.writeIntArray(this.mMotorDefaultSpeed);
        dest.writeInt(this.mDMXAddress);
        dest.writeInt(this.mGualiaoTime);
    }

    public DeviceConfig() {
    }

    private DeviceConfig(Parcel in) {
        this.mID = in.readLong();
        this.mTemperatureThresholdLow = in.readInt();
        this.mTemperatureThresholdHigh = in.readInt();
        this.mMotorDefaultSpeed = in.createIntArray();
        this.mDMXAddress = in.readInt();
        this.mGualiaoTime = in.readInt();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DeviceConfig> CREATOR = new Parcelable.Creator<DeviceConfig>() {
        @Override
        public DeviceConfig createFromParcel(Parcel source) {
            return new DeviceConfig(source);
        }

        @Override
        public DeviceConfig[] newArray(int size) {
            return new DeviceConfig[size];
        }
    };
}
