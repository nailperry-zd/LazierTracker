package com.codeless.tracker.utils;

import android.content.Context;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.codeless.tracker.ConfigConstants;

/**
 * Created by zhangdan on 2018/3/5.
 */

public class PathUtil {
    public static String getViewPath(View view) {
        String idStr = getResIdName(view.getContext(), view);
        return "/" + idStr;
    }

    public static String getResIdName(Context context, View view) {
        final int viewId = view.getId();
        if (-1 == viewId) {
            return null;
        } else {
            String name = ResourceReader.Ids.getInstance(context).nameForId(viewId);
            return name;
        }
    }

    public static Object getDataObj(View view, JSONObject nodeObj) {
        // 按照路径dataPath搜集数据
        String dataPath = nodeObj.getString(ConfigConstants.DATAPATH);
        String[] paths = dataPath.split("\\.");
        Object refer = view;
        for (int j = 0; j < paths.length; j++) {
            String path = paths[j];
            switch (path) {
                case ConfigConstants.KEY_THIS:
                    refer = view;
                    break;
                case ConfigConstants.KEY_CONTEXT:
                    refer = ((View) refer).getContext();
                    break;
                default:
                    // 反射获取变量值
                    refer = ReflectUtil.getObjAttr(path, refer);
                    break;
            }
        }

        return refer;
    }
}
