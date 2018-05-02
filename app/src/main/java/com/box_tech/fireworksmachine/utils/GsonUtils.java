/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.box_tech.fireworksmachine.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Json工具类.
 */
@SuppressWarnings("unused")
class GsonUtils {
    private final static Gson gson = new GsonBuilder().create();

    public static String toJson(Object value) {
        return gson.toJson(value);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonParseException {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonParseException {
        //noinspection unchecked
        return (T) gson.fromJson(json, typeOfT);
    }
}
