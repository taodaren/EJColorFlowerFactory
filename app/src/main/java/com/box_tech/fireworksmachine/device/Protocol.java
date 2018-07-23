package com.box_tech.fireworksmachine.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.box_tech.fireworksmachine.utils.BinaryReader;
import com.box_tech.fireworksmachine.utils.CRC16;
import com.box_tech.fireworksmachine.utils.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by scc on 2018/3/8
 * 协议处理.
 */

public class Protocol {
    private final static String TAG = "Protocol";
    private final static int MAX_PKG_LEN = 0x80;
    private final static int CMD_STATUS = 1;
    private final static int CMD_CONFIG = 2;
    private final static int CMD_SET_TEMP_THRESHOLD = 3;
    private final static int CMD_SET_MOTOR_SPEED = 4;
    private final static int CMD_SET_ID = 5;
    private final static int CMD_SET_DMX_ADDRESS = 6;
    private final static int CMD_JET = 7;
    private final static int CMD_ADD_MATERIAL = 8;
    private final static int CMD_GET_TIMESTAMP = 9;
    private final static int CMD_STOP_JET = 10;
    private final static int CMD_SET_GUALIAO_TIME = 11;
    private final static int CMD_SET_BARCODE_DATA = 15;

    private final static int HEADER_LEN = 7;
    private final byte[] pkg = new byte[MAX_PKG_LEN];
    private int pkg_len = 0;
    private boolean translate = false;

    public void onReceive(byte[] data){
        for(byte b : data){
            onByte(b);
            //onByteLevel2(b,false);
        }
    }

    // 反转义
    private void onByte(byte b){
        if(translate){
            translate = false;
            onByteLevel2( (byte)(((int)b & 0xff) ^ 0xFF), true );
        }
        else if( b == (byte)0xFE){
            translate = true;
        }
        else{
            onByteLevel2(b, false);
        }
    }

    private int s = 0;
    private int data_len = 0;
    private final byte[] S = new byte[6];
    private int Sn = 0;
    private final static int[] K = new int[]{
            30, 19, 3, 37, 17, 16, 35, 36, 16, 37, 22, 4, 42, 15, 21, 14, 29, 45, 20, 41, 38, 13,
            12, 5, 5, 40, 8, 1, 34, 7, 8, 2, 19, 13, 31, 27, 0, 20, 28, 24, 38, 36, 23, 10, 1, 14,
            43, 33, 13, 16, 15, 3, 37, 4, 25, 6, 40, 12, 5, 42, 25, 3, 31, 29, 18, 25, 39, 30, 24,
            47, 11, 47, 34, 22, 46, 8, 44, 39, 44, 21, 27, 9, 15, 2, 11, 28, 6, 19, 41, 21, 46, 45,
            39, 44, 31, 23, 18, 34, 33, 9, 24, 36, 23, 30, 17, 41, 22, 26, 9, 32, 6, 4, 26, 14, 18,
            40, 38, 11, 17, 12, 7, 35, 32, 27, 32, 2, 10, 0, 20, 0, 35, 45, 26, 1, 47, 33, 28, 46,
            42, 10, 43, 43, 29, 7
    };
    private static byte map(byte[] S, int i, byte d){
        int m = 0;
        for(int j=0;j<8;j++){
            int k = K[(i*8+j)%K.length];
            m |= ((((int)S[k>>3] & (1<<(k&7)))!=0)?1:0)<<j;
        }
        return (byte)(((int)d & 0xff)^m);
    }
    private byte map(int i, byte d){
        return map(S, i, d);
    }
    //解密
    private void onByteLevel2(byte b, boolean is_translated){
        if(b == (byte)0xDC && !is_translated){
            s = 1;
        }
        else if(s==1){
            data_len = (int)b & 0xff;
            if(data_len>MAX_PKG_LEN){
                s = 0;
            }
            else{
                Sn = 0;
                s = 2;
            }
        }
        else if(s==2){
            S[Sn] = b;
            Sn ++;
            if(Sn>=S.length){
                s = 3;
                pkg_len = 0;
            }
        }
        else if(s==3){
            pkg[pkg_len] = map(pkg_len, b);
            pkg_len++;
            if(pkg_len>=data_len){
                s = 0;
                onPackage();
            }
        }
    }

    private void onPackage(){
        if(pkg_len>=9 && pkg[0] == (byte)0xCD){
            int data_len = (int)pkg[1] & 0xff;
            if(data_len+9 == pkg_len){
                onPackageLengthValidated();
                return;
            }
        }
        Log.e(TAG, "malformed package "+ Util.hex(pkg, pkg_len));
    }

    public byte[] getPkg(){
        return Arrays.copyOfRange(pkg, 0, pkg_len);
    }

    private void onPackageLengthValidated(){
        if( CRC16.validate(pkg, pkg_len-2) ){
            if((pkg[2] & 0x80) != 0){
                Log.i(TAG, "ack package   "+Util.hex(pkg, pkg_len));
                onAckPackage();
            }
            else{
                Log.e(TAG, "drop command package "+Util.hex(pkg, pkg_len));
            }
        }
        else{
            Log.e(TAG, "CRC wrong package "+Util.hex(pkg, pkg_len));
        }
    }

    private void onAckPackage(){
        int cmd = pkg[2] & 0x7F;
        //long id = parseID(pkg, pkg_len);
        switch (cmd){
            case CMD_STATUS:
                DeviceState ds = parseStatus(pkg, pkg_len);
                if(ds != null){
                    onReceivePackage(ds);
                }
                break;
            case CMD_CONFIG:
                DeviceConfig config = parseConfig(pkg, pkg_len);
                if(config!=null){
                    onReceivePackage(config);
                }
                break;
            default:
                onReceivePackage(pkg, pkg_len);
                break;
        }
    }

    protected void onReceivePackage(@NonNull DeviceState state){
    }

    protected void onReceivePackage(@NonNull DeviceConfig config){
    }

    protected void onReceivePackage(@NonNull byte[] pkg, int pkg_len){
        Log.e(TAG, "receive package "+Util.hex(pkg, pkg_len));
    }

    public static boolean isMatch(@NonNull byte[] cmd_pkg, @NonNull byte[] ack_pkg){
        if(cmd_pkg[0] == (byte)0xCE && ack_pkg[0] == (byte)0xDE ){
            return (cmd_pkg[1] & 7) == ack_pkg[1];
        }
        return (cmd_pkg[2] | (byte)0x80) == ack_pkg[2] &&
                ((cmd_pkg[3]==0 && cmd_pkg[4]==0 &&cmd_pkg[5]==0 &&cmd_pkg[6]==0)||
                        (cmd_pkg[3]==ack_pkg[3] && cmd_pkg[4]==ack_pkg[4] &&
                                cmd_pkg[5]==ack_pkg[5] &&cmd_pkg[6]==ack_pkg[6]));
    }

    @NonNull
    public static byte[] command_package(int command, long id, @Nullable byte[] data){
        final int data_len = (data==null)?0:data.length;
        final byte[] pkg = new byte[9+data_len];
        pkg[0] = (byte)0xCD;
        pkg[1] = (byte)data_len;
        pkg[2] = (byte)command;
        pkg[3] = (byte)(id & 0xff);
        pkg[4] = (byte)((id >>8)& 0xff);
        pkg[5] = (byte)((id >>16)& 0xff);
        pkg[6] = (byte)((id >>24)& 0xff);
        if(data!=null){
            System.arraycopy(data, 0, pkg, 7, data_len);
        }
        int crc = CRC16.calculate(pkg, 7+data_len);
        pkg[7+data_len] = (byte)(crc & 0xff);
        pkg[8+data_len] = (byte)((crc>>8) & 0xff);

        //Log.i(TAG, "command_package "+Util.hex(pkg, pkg.length));
        return pkg;
    }

    public static boolean isLcdModeCommand(@NonNull byte[] pkg){
        return pkg[0] == (byte)0xCE;
    }


    private static final SecureRandom seedGen = new SecureRandom();

    @NonNull
    private static byte[] encrypt_package(@NonNull byte[] d){
        final byte[] pkg = new byte[8+d.length];
        final byte[] S = new byte[6];
        seedGen.nextBytes(S);
        pkg[0] = (byte)0xDC;
        pkg[1] = (byte)d.length;
        System.arraycopy(S, 0, pkg, 2, S.length);
        for(int i=0;i<d.length;i++){
            pkg[8+i] = map(S, i, d[i]);
        }
        return pkg;
    }

    private final static byte TRANSLATE_LEAD = (byte)0xFE;

    private static boolean should_translate(byte b){
        return b == (byte)0xCD ||
                b == (byte)0xDC ||
                b == TRANSLATE_LEAD;
    }

    @NonNull
    private static byte[] translate(@NonNull byte[] d){
        int c = 0;
        for(int i=1;i<d.length;i++){
            if(should_translate(d[i])){
                c++;
            }
        }

        if(c>0){
            final byte[] pkg = new byte[d.length+c];
            pkg[0] = d[0];
            int j = 1;
            for(int i=1;i<d.length;i++){
                if(should_translate(d[i])){
                    pkg[j++] = TRANSLATE_LEAD;
                    pkg[j++] = (byte)((int)d[i] ^ 0xff);
                }
                else{
                    pkg[j++] = d[i];
                }
            }
            return pkg;
        }
        else{
            return d;
        }
    }

    @NonNull
    public static byte[] wrapped_package(@NonNull byte[] data){
        return translate(encrypt_package(data));
    }

    @NonNull
    public static byte[] get_status_package(long id){
        return command_package(CMD_STATUS, id, null);
    }

    @NonNull
    public static byte[] get_config_package(long id){
        return command_package(CMD_CONFIG, id, null);
    }

    @NonNull
    public static byte[] set_temperature_threshold_package(long id, int low, int height){
        byte[] data = new byte[4];
        data[0] = (byte)(low & 0xff);
        data[1] = (byte)((low>>8) & 0xff);
        data[2] = (byte)(height & 0xff);
        data[3] = (byte)((height>>8) & 0xff);
        return command_package(CMD_SET_TEMP_THRESHOLD, id, data);
    }

    @NonNull
    public static byte[] set_motor_default_speed_package(long id, int[] speeds){
        byte[] data = new byte[4];
        data[0] = (byte)(speeds[0] & 0xff);
        data[1] = (byte)(speeds[1] & 0xff);
        data[2] = (byte)(speeds[2] & 0xff);
        data[3] = (byte)(speeds[3] & 0xff);
        return command_package(CMD_SET_MOTOR_SPEED, id, data);
    }

    @NonNull
    public static byte[] set_id_package(long id, long new_id){
        byte[] data = new byte[4];
        data[0] = (byte)(new_id & 0xff);
        data[1] = (byte)((new_id>>8) & 0xff);
        data[2] = (byte)((new_id>>16) & 0xff);
        data[3] = (byte)((new_id>>24) & 0xff);
        return command_package(CMD_SET_ID, id, data);
    }

    @NonNull
    public static byte[] set_dmx_address_package(long id, int dmx_address){
        byte[] data = new byte[2];
        data[0] = (byte)(dmx_address & 0xff);
        data[1] = (byte)((dmx_address>>8) & 0xff);
        return command_package(CMD_SET_DMX_ADDRESS, id, data);
    }

    @NonNull
    public static byte[] jet_package(long id, int delay, int duration, int high){
        byte[] data= new byte[5];
        data[0] = (byte)(delay & 0xff);
        data[1] = (byte)((delay>>8) & 0xff);
        data[2] = (byte)(duration & 0xff);
        data[3] = (byte)((duration>>8) & 0xff);
        data[4] = (byte)(high & 0xff);
        return command_package(CMD_JET, id, data);
    }

    @NonNull
    public static byte[] jet_stop_package(long id){
        return command_package(CMD_STOP_JET, id, null);
    }

    @NonNull
    public static byte[] set_gualiao_time_package(long id, int time){
        byte[] data= new byte[1];
        data[0] = (byte)(time & 0xff);
        return command_package(CMD_SET_GUALIAO_TIME, id, data);
    }

    @NonNull
    public static byte[] get_timestamp_package(long id){
        return command_package(CMD_GET_TIMESTAMP, id, null);
    }

    @NonNull
    public static byte[] add_material(long id, int time, long stamp){
        byte[] data= new byte[6];
        data[0] = (byte)(time & 0xff);
        data[1] = (byte)((time>>8) & 0xff);
        data[2] = (byte)(stamp & 0xff);
        data[3] = (byte)((stamp>>8) & 0xff);
        data[4] = (byte)((stamp>>16) & 0xff);
        data[5] = (byte)((stamp>>24) & 0xff);
        return command_package(CMD_ADD_MATERIAL, id, data);
    }

    @NonNull
    public static byte[] set_barcode_data(long id, byte[] data, int offset){
        int len = Math.min(32, data.length-offset);
        byte[] d = new byte[len+2];
        d[0] = (byte)(offset & 0xff);
        d[1] = (byte)(offset >> 8);
        System.arraycopy(data, offset, d, 2, len);
        return command_package(CMD_SET_BARCODE_DATA, id, d );
    }

    public static long parseTimestamp(@NonNull byte[] pkg, int pkg_len){
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try{
            reader.skip(HEADER_LEN);
            return reader.readUnsignedIntLSB();
        }catch (IOException e){
            return -1;
        }
    }

    public static boolean parse_set_barcode_data_ack(@NonNull byte[] pkg, int pkg_len){
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        int result;
        try{
            reader.skip(HEADER_LEN);
            result = reader.readUnsignedChar();
        }catch (IOException e){
            result = 1;
        }
        return result == 0;
    }


    @Nullable
    public static DeviceState parseStatus(@NonNull byte[] pkg, int pkg_len){
        DeviceState ds = new DeviceState();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try{
            reader.skip(HEADER_LEN);
            ds.mTemperature = reader.readSignedShortLSB();
            ds.mSupplyVoltage = (float)reader.readUnsignedChar()/10;
            ds.mMotorSpeed[0] = reader.readUnsignedChar();
            ds.mMotorSpeed[1] = reader.readUnsignedChar();
            ds.mMotorSpeed[2] = reader.readUnsignedChar();
            ds.mMotorSpeed[3] = reader.readUnsignedChar();
            ds.mPitch = reader.readUnsignedChar();
            ds.mUltrasonicDistance = reader.readUnsignedChar();
            ds.mInfraredDistance = reader.readUnsignedChar();
            ds.mRestTime = reader.readUnsignedShortLSB();
            return ds;
        }catch (IOException e){
            return null;
        }
    }

    @Nullable
    public static DeviceConfig parseConfig(@NonNull byte[] pkg, int pkg_len){
        DeviceConfig config = new DeviceConfig();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try{
            reader.skip(HEADER_LEN);
            config.mID = reader.readUnsignedIntLSB();
            config.mMotorDefaultSpeed[0] = reader.readUnsignedChar();
            config.mMotorDefaultSpeed[1] = reader.readUnsignedChar();
            config.mMotorDefaultSpeed[2] = reader.readUnsignedChar();
            config.mMotorDefaultSpeed[3] = reader.readUnsignedChar();
            config.mTemperatureThresholdLow = reader.readSignedShortLSB();
            config.mTemperatureThresholdHigh = reader.readSignedShortLSB();
            config.mDMXAddress = reader.readUnsignedShortLSB();
            config.mGualiaoTime = reader.readUnsignedChar();
            return config;
        }catch (IOException e){
            return null;
        }
    }

    private static long parseID(byte[] pkg, int pkg_len){
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try{
            reader.skip(3);
            return reader.readUnsignedIntLSB();
        }catch (IOException e){
            return 0;
        }
    }
}
