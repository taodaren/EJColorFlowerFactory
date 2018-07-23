package com.box_tech.fireworksmachine.utils;

import java.util.Locale;

/**
 * Created by scc on 2018/3/8.
 * 工具类
 */

public class Util {
    public static String hex(byte[] data, int len){
        return hex(data, 0, Math.min(len, data.length));
    }

    public static String hex(byte[] data, int start, int end){
        StringBuilder sb = new StringBuilder();
        for(int i=start;i<end;i++){
            sb.append(String.format(Locale.US, "%02X ", (int)data[i] & 0xff));
        }
        return sb.toString();
    }

    public static byte[] int2byte(int[] d){
        byte[] r = new byte[d.length];
        for(int i=0;i<d.length;i++){
            r[i] = (byte)d[i];
        }
        return r;
    }

    //private final static Pattern phoneNumberPattern = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$");
    //private final static Pattern passwordPattern = Pattern.compile("^[0-9%w]{4,10}$");

    /*
    public static boolean isPhoneNumberValid(String phoneNumber) {
        Pattern phoneNumberPattern = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$");
        return phoneNumberPattern.matcher(phoneNumber).matches();
    }
    */

    /*
    public static boolean isPasswordValid(String password) {
        return passwordPattern.matcher(password).matches();
    }
    */
}
