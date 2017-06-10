package com.gnayils.obiew.bmpldr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.gnayils.obiew.Obiew;

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
    private int maxSize;


    private static BitmapLoader bitmapLoader;

    private BitmapLoader() throws IllegalAccessException {
        memoryCache = LRUMemoryCache.create(Obiew.getAppContext(), 0);
        diskCache = LRUDiskCache.create(Obiew.getAppContext(), 0);
        downloader = URLDownloader.create();
        DisplayMetrics displayMetrics = Obiew.getAppResources().getDisplayMetrics();
        maxSize = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels) / 2;
    }

    public void loadBitmap(String url, ImageView imageView) {
        loadBitmap(url, imageView, null);
    }

    public void loadBitmap(String url, ImageView imageView, BitmapLoadListener listener) {
        if(url == null || url.trim().isEmpty()) {
            return;
        }
        LoadImageTask loadImageTask = (LoadImageTask) imageView.getTag();
        if(loadImageTask != null) {
            if(loadImageTask.url.equals(url) && loadImageTask.getStatus() != AsyncTask.Status.FINISHED) {
                Log.d(TAG, "last task attached to the ImageView still running: " + loadImageTask.hashCode());
                return ;
            } else {
                if(loadImageTask.getStatus() != AsyncTask.Status.FINISHED) {
                    loadImageTask.cancel(true);
                    Log.d(TAG, "cancel task: " + loadImageTask.hashCode());
                }
            }
        }

        final String urlKey = generateMD5Key(url);
        Bitmap bitmap = memoryCache.get(urlKey);
        if(bitmap != null) {
            Log.d(BitmapLoader.TAG, "get bitmap from memory cache: " + url);
            if(listener != null) {
                listener.onPreLoad();
                listener.onProgressUpdate(0);
                listener.onProgressUpdate(100);
                listener.onPostLoad(bitmap);
            }
            imageView.setImageBitmap(bitmap);
        } else {
            LoadImageTask task = new LoadImageTask(url, urlKey, imageView, listener);
            imageView.setTag(task);
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

    public class LoadImageTask extends AsyncTask<String, Integer, Bitmap>{

        public final String TAG = LoadImageTask.class.getSimpleName();

        private String url;
        private String urlKey;
        private ImageView imageView;
        private BitmapLoadListener listener;

        private LoadImageTask(String url, String urlKey, ImageView imageView, BitmapLoadListener listener) {
            this.url = url;
            this.urlKey = urlKey;
            this.imageView = imageView;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            if(listener != null) {
                listener.onPreLoad();
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            File file = diskCache.get(urlKey);
            if(file == null) {
                try {
                    InputStream inputStream = downloader.getInputStream(url);
                    Log.d(TAG, "get bitmap from internet: " + url);
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
                    if(isCancelled()) {
                        return null;
                    }
                    file =  diskCache.get(urlKey);
                } catch (IOException e) {
                    Log.e(TAG, "task failed during load image from [" + url + "] failed", e);
                    return null;
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

        private Bitmap decodeImage(File file) throws IOException, InterruptedException {
            InputStream inputStream = downloader.getInputStream(file);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            inputStream.mark((int)file.length());
            BitmapFactory.decodeStream(inputStream, null, options);

            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize);
            Log.d(TAG, "load image from file, image size: " + options.outWidth + ", " + options.outHeight + "; inSampleSize: " + options.inSampleSize);

            if(isCancelled()) {
                inputStream.close();
                return null;
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

        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
                long totalPixels = width * height / inSampleSize;

                final long totalReqPixelsCap = reqWidth * reqHeight;

                while (totalPixels > totalReqPixelsCap) {
                    inSampleSize *= 2;
                    totalPixels /= 2;
                }
            }
            return inSampleSize;
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
                imageView.setImageBitmap(bitmap);
            }
            if(listener != null) {
                listener.onPostLoad(bitmap);
            }
        }

        @Override
        protected void onCancelled() {
            if(listener != null) {
                listener.onCancelled();
            }
        }
    }

}
