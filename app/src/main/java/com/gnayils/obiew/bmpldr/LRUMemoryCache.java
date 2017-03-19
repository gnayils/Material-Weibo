package com.gnayils.obiew.bmpldr;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Gnayils on 8/21/16.
 */
public class LRUMemoryCache {

    public static final String TAG = LRUMemoryCache.class.getSimpleName();

    private final LinkedHashMap<String, Bitmap> map;
    private final int cacheSize;
    private int currentSize;

    private LRUMemoryCache(int cacheSize) {
        if(cacheSize <= 0) {
            throw new IllegalArgumentException("cacheSize <= 0");
        }
        this.cacheSize = cacheSize;
        this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
    }

    public Bitmap get(String key) {
        if(key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            return map.get(key);
        }
    }

    public void put(String key, Bitmap value) {
        if(key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            currentSize += sizeOf(key, value);
            Bitmap previous = map.put(key, value);
            if(previous != null) {
                currentSize -= sizeOf(key, previous);
            }
        }
        trimToSize(cacheSize);
    }

    public void remove(String key) {
        if(key == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            Bitmap previous = map.remove(key);
            if(previous != null) {
                currentSize -= sizeOf(key, previous);
            }
        }
    }

    public Collection<String> keys() {
        synchronized (this) {
            return new HashSet<String>(map.keySet());
        }
    }

    public void clear() {
        trimToSize(-1);
    }

    private void trimToSize(int sizeTrimTo) {
        while(true) {
            synchronized (this) {
                if(this.currentSize < 0 || (map.isEmpty() && this.currentSize != 0)) {
                    throw new IllegalStateException("trimToSize() reporting inconsistent results");
                }
                if(this.currentSize <= sizeTrimTo || map.isEmpty()) {
                    break;
                }
                Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
                if(toEvict == null) {
                    break;
                }
                remove(toEvict.getKey());
            }
        }
    }

    private int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static LRUMemoryCache create(Context context, int cacheSize) {
        if(cacheSize == 0) {
            ActivityManager activitymanager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int memoryClass = activitymanager.getMemoryClass();
            cacheSize = memoryClass * 1024 * 1024 / 4;
        }
        return new LRUMemoryCache(cacheSize);
    }

}
