package com.box_tech.fireworksmachine;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by scc on 2018/3/9.
 * 配置
 */

public class Settings {
    private static String get_value(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return settings.getString(key, null );
    }
    public static String get_username(Context context){ return get_value(context, "username");}
    public static String get_password(Context context){ return get_value(context, "password");}
    public static long get_member_id(Context context){
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return settings.getLong("member_id", 0 );
    }
    public static void storeLoginInfo(Context context, String username, String password){
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }
    public static void storeMemberID(Context context, long member_id){
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("member_id", member_id);
        editor.apply();
    }


    public final static String SERVER_URL = "http:///60.205.226.109/index.php/index/api/";
}
