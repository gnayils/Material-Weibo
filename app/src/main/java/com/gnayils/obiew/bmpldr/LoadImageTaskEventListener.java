package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;

/**
 * Created by Gnayils on 20/11/2016.
 */

public interface LoadImageTaskEventListener {

    void onPreExecute();

    void onProgressUpdate(Integer... values);

    void onPostExecute(Bitmap bitmap);

    void onCancelled();
}
