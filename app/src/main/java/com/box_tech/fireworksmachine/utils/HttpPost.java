package com.box_tech.fireworksmachine.utils;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.box_tech.fireworksmachine.BuildConfig;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by scc on 2018/3/9.
 * HTTP POST 请求
 */

class HttpPost {
    public interface Callback{
        void onOK(@Nullable Activity activity, @NonNull String response);
        void onFailed(@Nullable Activity activity, @NonNull String message);
    }

    private final static MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

    private static String user_agent(){
        return "FireworksMachine/"+ BuildConfig.VERSION_NAME+"(Linux;Android "+ Build.VERSION.RELEASE+";"+Build.MODEL+")";
    }

    private static final Headers headers = new Headers.Builder()
            .add("User-Agent", user_agent())
            .build();

    public static void run(final WeakReference<Activity> activity, String url, String queryString, final Callback callback){
        OkHttpClient client = new OkHttpClient();

        System.out.println(queryString);

        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(RequestBody.create(mediaType, queryString))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call,@NonNull  final IOException e) {
                final Activity a = activity.get();
                System.out.println("onFailure "+e.getMessage());
                if(a!=null){
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(a, e.getMessage());
                        }
                    });
                }
                else{
                    callback.onFailed(null, e.getMessage());
                }
            }

            @Override
            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                ResponseBody body = response.body();
                final String r = (body!=null)?body.string():null;
                final Activity a = activity.get();
                if(r!=null){
                    System.out.println("response");
                    System.out.println(r);
                }
                if(a!=null){
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(r!=null){
                                callback.onOK(a, r);
                            }
                            else{
                                callback.onFailed(a, "no response");
                            }
                        }
                    });
                }
                else{
                    if(r!=null){
                        callback.onOK(null, r);
                    }
                    else{
                        callback.onFailed(null, "no response");
                    }
                }
            }
        });
    }
}
