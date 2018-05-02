package com.box_tech.fireworksmachine.device;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by scc on 2018/3/2.
 * DeviceState information
 */

public class DeviceState implements Parcelable {
    public int mTemperature = 20;
    public float mSupplyVoltage = 12;
    public int[] mMotorSpeed = new int[]{20,10,15,200};
    public int mPitch = 0;
    public int mUltrasonicDistance = 60;
    public int mInfraredDistance = 60;
    public int mRestTime = 0;

    public String strOfRestTime(){
        int sec = mRestTime%60;
        int min = (mRestTime/60)%60;
        int hour = (mRestTime/3600)%60;
        StringBuilder sb = new StringBuilder();
        if(hour>0){
            sb.append(String.valueOf(hour));
            sb.append(" 小时 ");
        }
        if(min>0){
            sb.append(String.valueOf(min));
            sb.append(" 分 ");
        }
        sb.append(String.valueOf(sec));
        sb.append(" 秒");
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTemperature);
        dest.writeFloat(this.mSupplyVoltage);
        dest.writeIntArray(this.mMotorSpeed);
        dest.writeInt(this.mPitch);
        dest.writeInt(this.mUltrasonicDistance);
        dest.writeInt(this.mInfraredDistance);
        dest.writeInt(this.mRestTime);
    }

    public DeviceState() {
    }

    private DeviceState(Parcel in) {
        this.mTemperature = in.readInt();
        this.mSupplyVoltage = in.readFloat();
        this.mMotorSpeed = in.createIntArray();
        this.mPitch = in.readInt();
        this.mUltrasonicDistance = in.readInt();
        this.mInfraredDistance = in.readInt();
        this.mRestTime = in.readInt();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DeviceState> CREATOR = new Parcelable.Creator<DeviceState>() {
        @Override
        public DeviceState createFromParcel(Parcel source) {
            return new DeviceState(source);
        }

        @Override
        public DeviceState[] newArray(int size) {
            return new DeviceState[size];
        }
    };
}
