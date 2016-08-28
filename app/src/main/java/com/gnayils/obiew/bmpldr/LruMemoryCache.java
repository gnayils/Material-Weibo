package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Gnayils on 8/21/16.
 */
public class LruMemoryCache {

    private final LinkedHashMap<String, Bitmap> map;
    private final int maxSize;
    private int size;

    public LruMemoryCache(int maxSize) {
        if(maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
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
            size += sizeOf(key, value);
            Bitmap previous = map.put(key, value);
            if(previous != null) {
                size -= sizeOf(key, previous);
            }
        }
        trimToSize(maxSize);
    }

    public void remove(String key) {
        if(key == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            Bitmap previous = map.remove(key);
            if(previous != null) {
                size -= sizeOf(key, previous);
                previous.recycle();
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
                if(this.size < 0 || (map.isEmpty() && this.size != 0)) {
                    throw new IllegalStateException("trimToSize() reporting inconsistent results");
                }
                if(this.size <= sizeTrimTo || map.isEmpty()) {
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

    private int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

}
