package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.graphics.BitmapFactory.*;

/**
 * Created by Gnayils on 8/21/16.
 */
public class BitmapLoader {

    public static final String TAG = BitmapLoader.class.getSimpleName();

    private URLDownloader downloader;
    private LRUMemoryCache memoryCache;
    private LRUDiskCache diskCache;

    private static BitmapLoader bitmapLoader;

    private BitmapLoader() throws IllegalAccessException {
        memoryCache = LRUMemoryCache.create(App.context(), 0);
        diskCache = LRUDiskCache.create(App.context(), 0);
        downloader = URLDownloader.create();
    }

    public void loadBitmap(String url, ImageView imageView) {
        new LoadImageTask(url, imageView).execute();
    }

    public static BitmapLoader getInstance() {
        if(bitmapLoader == null) {
            throw new IllegalStateException("BitmapLoader is not initialized");
        }
        return bitmapLoader;
    }

    private String generateMD5Key(String text) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes());
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BigInteger bi = new BigInteger(hash).abs();
        return bi.toString(10 + 26);
    }

    public static void initialize() {
        try {
            bitmapLoader = new BitmapLoader();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "BitmapLoader initialize failed", e);
        }
    }

    public class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {

        public final String TAG = LoadImageTask.class.getSimpleName();

        private String url;
        private String urlKey;
        private ImageView imageView;
        private LoadImageTaskEventListener listener;

        private LoadImageTask(String url, ImageView imageView) {
            this.url = url;
            this.urlKey = generateMD5Key(url);
            this.imageView = imageView;
            this.listener = new LoadImageTaskEventListener() {
                @Override
                public void onPostExecute(Bitmap bitmap) {
                    LoadImageTask.this.imageView.setImageBitmap(bitmap);
                }
            };
        }

        @Override
        protected void onPreExecute() {
            if(listener != null) {
                listener.onPreExecute();
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = memoryCache.get(urlKey);
            if(bitmap != null) {
                Log.d(BitmapLoader.TAG, "get bitmap from memory cache: " + url);
                return bitmap;
            }

            File file = diskCache.get(urlKey);
            if(file == null) {
                try {
                    InputStream inputStream = downloader.getInputStream(url);
                    diskCache.put(urlKey, inputStream, new LRUDiskCache.StreamCopyListener() {

                        @Override
                        public boolean bytesCopied(int totalBytesCount, int copiedBytesCount) {
                            if (isCancelled()) {
                                return true;
                            }
                            publishProgress(copiedBytesCount * 100 / totalBytesCount);
                            return false;
                        }
                    });
                    Log.d(TAG, "get bitmap from internet: " + url);
                    file =  diskCache.get(urlKey);
                } catch (IOException e) {
                    Log.e(TAG, "task failed during load image from [" + url + "] failed", e);
                }
            } else {
                Log.d(TAG, "get bitmap from disk cache: " + url);
            }
            try {
                return decodeImage(file);
            } catch (IOException e) {
                Log.e(TAG, "task failed during decode image from [" + file.getAbsolutePath() + "]", e);
                return null;
            } catch (InterruptedException e) {
                Log.e(TAG, "task failed during decode image from [" + file.getAbsolutePath() + "]", e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(listener != null) {
                listener.onProgressUpdate(values);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                memoryCache.put(urlKey, bitmap);
            }
            if(listener != null) {
                listener.onPostExecute(bitmap);
            }
        }

        @Override
        protected void onCancelled() {
            if(listener != null) {
                listener.onCancelled();
            }
        }

        private Bitmap decodeImage(File file) throws IOException, InterruptedException {
            InputStream inputStream = downloader.getInputStream(file);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            inputStream.mark((int)file.length());
            BitmapFactory.decodeStream(inputStream, null, options);

            options = new Options();
            if(imageView.isLaidOut()) {
                int scale = Math.round(Math.min((float) options.outWidth / imageView.getWidth(), (float) options.outHeight / imageView.getHeight()));
                options.inSampleSize = Math.max(scale, 1);
            }

            try {
                if(inputStream.markSupported()) {
                    inputStream.reset();
                } else {
                    inputStream.close();
                    inputStream = downloader.getInputStream(file);
                }
            } catch(Exception e) {
                Log.e(TAG, "reset input stream failed", e) ;
                inputStream.close();
                inputStream = downloader.getInputStream(file);
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            return bitmap;
        }
    }

}
