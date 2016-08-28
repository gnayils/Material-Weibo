package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.gnayils.obiew.App;

/**
 * Created by Gnayils on 8/21/16.
 */
public class BitmapLoader {

    private LruMemoryCache memoryCache;
    private LruDiskCache diskCache;

    private static BitmapLoader BITMAP_LOADER = null;


    private BitmapLoader() throws IllegalAccessException {
        memoryCache = LruMemoryCache.create(App.context(), 0);
        diskCache = LruDiskCache.create(App.context(), 0);
    }

    public static void initialize() throws IllegalAccessException {
        BITMAP_LOADER = new BitmapLoader();
    }
}
