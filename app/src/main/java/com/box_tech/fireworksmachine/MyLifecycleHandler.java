package com.box_tech.fireworksmachine;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    // I use four separate variables here. You can, of course, just use two and
    // increment/decrement them instead of using four and incrementing them all.
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;
    private static boolean foreground = false;
    private static final List<OnForegroundStateChangeListener> foregroundStateChangesList = new LinkedList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        checkForegroundChange();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        checkForegroundChange();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }


    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.w("test", "application is visible: " + (started > stopped));
    }


    private static void checkForegroundChange(){
        boolean old = foreground;
        if(old != isApplicationInForeground()){
            foreground = ! old;
            synchronized (foregroundStateChangesList){
                for(OnForegroundStateChangeListener listener : foregroundStateChangesList){
                    listener.onStateChanged(foreground);
                }
            }
        }
    }

    public interface OnForegroundStateChangeListener {
        /**
         *  APP切换到了后台或切换到前台
         * @param foreground 新的状态
         */
        void onStateChanged(boolean foreground);
    }

    public static void addListener(@NonNull OnForegroundStateChangeListener listener){
        synchronized (foregroundStateChangesList){
            foregroundStateChangesList.add(listener);
        }
    }

    public static void removeListener(@NonNull OnForegroundStateChangeListener listener){
        synchronized (foregroundStateChangesList){
            foregroundStateChangesList.remove(listener);
        }
    }

    // And these two public static functions
    //public static boolean isApplicationVisible() {
    //    return started > stopped;
    //}

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }
}
