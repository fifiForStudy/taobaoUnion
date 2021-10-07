package com.example.taobao.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.taobao.base.BaseApplication;
import com.example.taobao.model.domain.CacheWithDuration;
import com.google.gson.Gson;

public class JsonCacheUtil {

    public static final String JSON_CACHE_SP_NAME = "json_cache_sp_name";
    private final SharedPreferences mSharedPreferences;
    private final Gson mGson;

    //构造方法私有化
    private JsonCacheUtil() {
        mSharedPreferences = BaseApplication.getAppContext().getSharedPreferences(JSON_CACHE_SP_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();

    }

    //单例
    public static JsonCacheUtil sJsonCacheUtil = null;

    //暴露接口
    public static JsonCacheUtil getInstance() {
        if (sJsonCacheUtil == null) {
            sJsonCacheUtil = new JsonCacheUtil();
        }
        //TODO:!!!!!!!!!!!!!!!
        return sJsonCacheUtil;
        /*注：多线程要使用DoubleCheck*/
    }

    //save
    public void saveCache(String key, Object value) {
        this.saveCache(key, value, -1L);
    }

    /**
     * duration:时长,时间间隔
     */
    public void saveCache(String key, Object value, long duration) {
        //进入edit模式
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        String valueStr = mGson.toJson(value);

        if (duration != -1L) {
            duration += System.currentTimeMillis();
        }

        //保存一个有数据有时间的内容
        CacheWithDuration cacheWithDuration = new CacheWithDuration(duration, valueStr);
        String cacheWithTime = mGson.toJson(cacheWithDuration);
        edit.putString(key, cacheWithTime);
        edit.apply();
    }

    //delete
    public void delCache(String key) {
        mSharedPreferences.edit().remove(key).apply();

    }

    //modify
    //search
    public <T> T getValue(String key, Class<T> clazz) {
        String valueWithDuration = mSharedPreferences.getString(key, null);
        if (valueWithDuration == null) {
            return null;
        }
        CacheWithDuration cacheWithDuration = mGson.fromJson(valueWithDuration, CacheWithDuration.class);
        //对时间进行判断
        long duration = cacheWithDuration.getDuration();
        if (duration != -1 && duration - System.currentTimeMillis() <= 0) {
            //过期了
            return null;
        } else {
            //没过期
            String cache = cacheWithDuration.getCache();
            LogUtils.d(this,"cache --> "+cache);
            T result = mGson.fromJson(cache, clazz);
            return result;

        }
    }

}



