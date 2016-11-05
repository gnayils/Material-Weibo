package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.gnayils.obiew.App;

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

    public void display(String url, final ImageView imageView) {
        String urlKey = generateMD5Key(url);
        Bitmap bitmap = memoryCache.get(urlKey);
        if(bitmap != null) {
            Log.d(TAG, "get bitmap from memory cache: " + url);
            imageView.setImageBitmap(bitmap);
        } else {
            LoadImageTask task = new LoadImageTask(url, imageView.getWidth(), imageView.getHeight(),
                    new LoadImageTaskEventListener(){

                @Override
                public void onPostExecute(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }

            });
            task.execute();
        }
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

    private class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {

        public final String tag = LoadImageTask.class.getSimpleName();

        private String url;
        private String urlKey;
        private int targetWidth;
        private int targetHeight;
        private LoadImageTaskEventListener listener;

        private LoadImageTask(String url, int targetWidth, int targetHeight, LoadImageTaskEventListener listener) {
            this.url = url;
            this.urlKey = generateMD5Key(url);
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            listener.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
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
                    Log.d(tag, "get bitmap from internet: " + url);
                    file =  diskCache.get(urlKey);
                } catch (IOException e) {
                    Log.e(tag, "task failed during load image from [" + url + "] failed", e);
                }
            } else {
                Log.d(tag, "get bitmap from disk cache: " + url);
            }
            try {
                return decodeImage(file);
            } catch (IOException e) {
                Log.e(tag, "task failed during decode image from [" + file.getAbsolutePath() + "]", e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            listener.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                memoryCache.put(urlKey, bitmap);
            }
            listener.onPostExecute(bitmap);
        }

        @Override
        protected void onCancelled() {
            listener.onCancelled();
        }

        private Bitmap decodeImage(File file) throws IOException {
            InputStream inputStream = downloader.getInputStream(file);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            inputStream.mark((int)file.length());
            BitmapFactory.decodeStream(inputStream, null, options);

            int scale = Math.round(Math.min(options.outWidth / 1.0f / targetWidth, options.outHeight / 1.0f / targetHeight));
            options = new Options();
            options.inSampleSize = Math.max(scale, 1);

            try {
                if(inputStream.markSupported()) {
                    inputStream.reset();
                }
            } catch(Exception e) {
                Log.e(tag, "reset input stream failed", e);
                inputStream.close();
                inputStream = downloader.getInputStream(file);
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            return bitmap;
        }
    }

    private class LoadImageTaskEventListener {

        public void onPreExecute() {

        }

        public void onProgressUpdate(Integer... values) {

        }

        public void onPostExecute(Bitmap bitmap) {


        }

        public void onCancelled() {

        }
    }

}
