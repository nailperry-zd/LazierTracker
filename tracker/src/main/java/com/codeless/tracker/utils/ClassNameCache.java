package com.codeless.tracker.utils;

import android.util.LruCache;

/**
 * Created by zhangdan on 17/2/25.
 */

public class ClassNameCache extends LruCache<Class<?>, String> {
    public ClassNameCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected String create(Class<?> klass) {
        return klass.getCanonicalName();
    }
}
