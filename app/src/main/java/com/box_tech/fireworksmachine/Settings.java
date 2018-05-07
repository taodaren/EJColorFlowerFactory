package com.box_tech.fireworksmachine;

import android.content.Context;
import android.content.SharedPreferences;

import com.box_tech.fireworksmachine.login.LoginSession;

/**
 * Created by scc on 2018/3/9.
 * 配置
 */

public class Settings {
    /*private static String get_value(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return settings.getString(key, null );
    }*/
    public static LoginSession getLoginSessionInfo(Context context){
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return new LoginSession(
                settings.getString("username", null),
                settings.getString("password", null),
                settings.getLong("member_id", 0 ),
                settings.getString("token", null));
    }
    public static void storeSessionInfo(Context context, LoginSession session){
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", session.getUsername());
        editor.putString("password", session.getPassword());
        editor.putLong("member_id", session.getMember_id());
        editor.putString("token", session.getToken());
        editor.apply();
    }


    public final static String SERVER_URL = "http:///60.205.226.109/index.php/index/api/";
}
