package com.box_tech.fireworksmachine.utils;

/**
 * Created by scc on 2018/3/15.
 * 注册结果
 */

@SuppressWarnings("unused")
public class GeneralResult {
    public final static int RESULT_OK = 1;

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
