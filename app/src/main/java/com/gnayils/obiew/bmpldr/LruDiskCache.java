package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Gnayils on 8/28/16.
 */
public class LruDiskCache {

    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    public static final int DEFAULT_COMPRESS_QUALITY = 100;

    private final LinkedHashMap<String, File> map;
    private final File cacheDirectory;
    private final int maxSize;
    private int size;

    public LruDiskCache(File cacheDirectory, int maxSize) throws IllegalAccessException {
        if(maxSize <= 0) {
            throw new IllegalStateException("max <= 0");
        }
        if(!cacheDirectory.exists() && !cacheDirectory.mkdirs()) {
            throw new IllegalAccessException("cache directory not exists");
        }
        this.cacheDirectory = cacheDirectory;
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<String, File>(0, 0.75f, true);
    }

    public Bitmap get(String key) {
        if(key == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            File file = map.get(key);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            return bitmap;
        }
    }

    public void put(String key, Bitmap bitmap) throws FileNotFoundException {
        if(key == null || bitmap == null) {
            throw new NullPointerException("key == null || bitmap == null");
        }
        synchronized (this) {
            File file = new File(cacheDirectory, key);
            OutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(file));
                boolean successful = bitmap.compress(DEFAULT_COMPRESS_FORMAT, DEFAULT_COMPRESS_QUALITY, outputStream);
                if (successful) {
                    map.put(key, file);
                    size += file.length();
                }
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
        trimToSize(maxSize);
    }

    public void remove(String key) {
        if(key == null) {
            throw new IllegalArgumentException("key == null");
        }
        synchronized (this) {
            File file = map.remove(key);
            if(file != null) {
                size -= file.length();
                if(file.exists()) {
                    throw new IllegalStateException("file that is going to remove cannot be non－exists");
                }
                file.delete();
            }
        }
    }

    public void clear() {
        trimToSize(-1);
    }

    private void trimToSize(int sizeTrimTo) {
        while(true) {
            synchronized (this) {
                if(size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException("trimToSize() reporting inconsistent results");
                }
                if(size <= sizeTrimTo || map.isEmpty()) {
                    break;
                }
                Map.Entry<String, File> toEvict = map.entrySet().iterator().next();
                if(toEvict == null) {
                    break;
                }
                remove(toEvict.getKey());
            }
        }
    }

}
