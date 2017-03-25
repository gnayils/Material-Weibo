package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;

/**
 * Created by Gnayils on 20/11/2016.
 */

public interface BitmapLoadListener {

    void onPreLoad();

    void onProgressUpdate(Integer... values);

    void onPostLoad(Bitmap bitmap);

    void onCancelled();
}
