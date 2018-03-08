package com.codeless.tracker;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.codeless.tracker.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhangdan on 2018/3/5.
 */

public class Tracker {
    final static public String TAG = "LazierTracker";
    private volatile static Tracker mTracker;
    private final Map<String, Object> mConfigureMap;

    private Tracker(Context context) {
        // TODO: 2018/3/5 该业务埋点配置本应由服务器下发，这里暂时写在本地
        // 解析业务埋点配置
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("configure.json");
            json = IOUtil.readInputStream(inputStream);
        } catch (IOException e) {

        }
        mConfigureMap = JSON.parseObject(json, Map.class);
    }

    public static void init(Context context) {
        if (mTracker == null) {
            synchronized (Tracker.class) {
                if (mTracker == null) {
                    mTracker = new Tracker(context);
                }
            }
        }
    }

    public static synchronized Tracker instance() {
        if (mTracker == null) {
            Log.d(TAG,
                    "Tracker is not enabled, please call init first");
        }
        return mTracker;
    }

    public static synchronized Tracker instance(Context context) {
        if (mTracker == null) {
            mTracker = new Tracker(context.getApplicationContext());
        }
        return mTracker;
    }

    public Map<String, Object> getConfigureMap() {
        return mConfigureMap;
    }

    public void trackEvent(String eventId, Map<String, Object> attributes) {
        // TODO: 2018/3/5 在此组装打点数据，然后上报服务器；这里为了演示，仅以日志形式打印出来
        Log.d(TAG, "成功打点事件->@eventId = " + eventId);
        if (null != attributes) {
            Iterator<Map.Entry<String, Object>> iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                Log.d(TAG, "attributes@" + entry.getKey() + " = " + entry.getValue());
            }
        }
    }
}
