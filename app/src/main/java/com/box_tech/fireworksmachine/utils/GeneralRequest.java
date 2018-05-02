package com.box_tech.fireworksmachine.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;

/**
 * Created by scc on 2018/3/15.
 * 请求模板 GeneralRequest<ResultType>
 *
 */

public abstract class GeneralRequest<ResultType> {
    private final WeakReference<Activity> activity;
    private final StringBuilder queryString = new StringBuilder();
    private final String iv;
    private String url;
    private final OnFinished<ResultType> callback;

    protected GeneralRequest(Activity activity, OnFinished<ResultType> callback){
        this.activity = new WeakReference<>(activity);
        this.iv = Encryption.newIv();
        queryString.append("iv=");
        queryString.append(iv);
        this.callback = callback;
    }

    public interface OnFinished<ResultType>{
        void onOK(@Nullable Activity activity, @NonNull ResultType result);
        void onFailed(@Nullable Activity activity, @NonNull String message);
    }

    private String url_encode(String name){
        try {
            return URLEncoder.encode(name, "UTF-8");
        }catch (UnsupportedEncodingException e){
            return "";
        }
    }

    protected void addField(@NonNull String key, @NonNull String value){
        queryString.append("&");
        queryString.append(url_encode(key));
        queryString.append("=");
        queryString.append(url_encode(value));
    }

    protected void addField(@NonNull String key, long value){
        addField(key, String.valueOf(value));
    }

    protected void addFieldEncrypt(@NonNull String key,@NonNull  String value){
        try{
            String x = Encryption.encrypt(value, iv);
            queryString.append("&");
            queryString.append(url_encode(key));
            queryString.append("=");
            queryString.append(url_encode(x));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void setUrl(@NonNull String url){
        this.url = url;
    }

    private String getQueryString(){
        return queryString.toString();
    }
    private String getURL(){
        return url;
    }

    @Nullable
    private Class<ResultType> getResultTypeClass() {
        Class clazz = getClass();

        while (clazz != Object.class) {
            Type t = clazz.getGenericSuperclass();
            if (t instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) t).getActualTypeArguments();
                if (args[0] instanceof Class) {
                    //noinspection unchecked
                    return (Class<ResultType>) args[0];
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public void run(){
        try {
            String queryString = getQueryString();
            HttpPost.run(activity, getURL(), queryString, new HttpPost.Callback(){
                @Override
                public void onOK(@Nullable Activity activity, @NonNull String response) {
                    Class<ResultType> resultClass = getResultTypeClass();

                    try {
                        ResultType result = GsonUtils.fromJson(response, resultClass);
                        if(callback!=null){
                            if (result != null && resultClass != null) {
                                callback.onOK(activity, result);
                            } else {
                                callback.onFailed(activity, "服务器返回格式错误");
                            }
                        }
                    }catch (IllegalStateException e){
                        if(callback!=null)
                            callback.onFailed(activity, "服务器返回格式错误");
                    }
                }

                @Override
                public void onFailed(@Nullable Activity activity, @NonNull String message) {
                    if(callback!=null){
                        callback.onFailed(activity, message);
                    }
                }
            } );
        }catch (Exception e){
            Activity a = activity.get();
            if(a!=null && callback != null){
                callback.onFailed(a, e.getMessage());
            }
        }
    }
}
