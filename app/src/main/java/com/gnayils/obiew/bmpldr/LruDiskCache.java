package com.gnayils.obiew.bmpldr;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Gnayils on 8/28/16.
 */
public class LRUDiskCache {

    public static final String TAG = LRUDiskCache.class.getSimpleName();

    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    public static final int DEFAULT_COMPRESS_QUALITY = 100;
    public static final int COPY_BUFFER_SIZE = 8192;
    public static final int CONTINUE_COPY_PERCENTAGE = 75;

    private final LinkedHashMap<String, File> map;
    private final File cacheDirectory;
    private final long cacheSize;
    private long currentSize;

    private LRUDiskCache(File cacheDirectory, long cacheSize) throws IllegalAccessException {
        if(cacheSize <= 0) {
            throw new IllegalStateException("max <= 0");
        }
        this.cacheDirectory = cacheDirectory;
        this.cacheSize = cacheSize;
        this.map = new LinkedHashMap<String, File>(0, 0.75f, true);
        File[] files = cacheDirectory.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    this.map.put(file.getName(), file);
                }
            }
        }
        Log.d(TAG, "use cache directory: " + cacheDirectory.getAbsolutePath());
    }

    public File get(String key) {
        if(key == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            return map.get(key);
        }
    }

    public void put(String key, InputStream inputStream, StreamCopyListener listener) throws IOException {
        if(key == null || inputStream == null) {
            throw new NullPointerException("key == null || inputStream == null");
        }
        synchronized (this) {
            OutputStream outputStream = null;
            try {
                boolean successful = true;
                int totalLength = inputStream.available();
                int totalReadLength = 0;
                int readLength;
                byte[] buffer = new byte[COPY_BUFFER_SIZE];
                File file = new File(cacheDirectory, key);
                outputStream = new BufferedOutputStream(new FileOutputStream(file));
                while ((readLength = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readLength);
                    totalReadLength += readLength;
                    if(shouldStopCopy(listener, totalLength, totalReadLength)) {
                        successful = false;
                        break;
                    }
                }
                if(successful) {
                    outputStream.flush();
                    map.put(key, file);
                    currentSize += file.length();
                }
            } catch (Exception e) {
                Log.e(TAG, "read data from input stream failed", e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
        trimToSize(cacheSize);
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
                    currentSize += file.length();
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
        trimToSize(cacheSize);
    }

    public void remove(String key) {
        if(key == null) {
            throw new IllegalArgumentException("key == null");
        }
        synchronized (this) {
            File file = map.remove(key);
            if(file != null) {
                currentSize -= file.length();
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

    private void trimToSize(long sizeTrimTo) {
        while(true) {
            synchronized (this) {
                if(currentSize < 0 || (map.isEmpty() && currentSize != 0)) {
                    throw new IllegalStateException("trimToSize() reporting inconsistent results");
                }
                if(currentSize <= sizeTrimTo || map.isEmpty()) {
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

    private boolean shouldStopCopy(StreamCopyListener listener, int total, int current) {
        boolean cancelled = listener.bytesCopied(total, current);
        if(cancelled) {
            if(100 * current / total >= CONTINUE_COPY_PERCENTAGE) {
                return false;
            }
        }
        return cancelled;
    }

    public static LRUDiskCache create(Context context, int cacheSize) throws IllegalAccessException {
        File cacheDirectory = null;
        if("mounted".equals(Environment.getExternalStorageState())) {
            if(context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                File dataDirectory = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
                cacheDirectory = new File(new File(dataDirectory, context.getPackageName()), "cache");
                if(!cacheDirectory.exists() && !cacheDirectory.mkdirs()) {
                    Log.w(TAG, "can't create external cache directory: " + cacheDirectory.getAbsolutePath());
                    cacheDirectory = null;
                }
            }
        }
        if(cacheDirectory == null) {
            cacheDirectory = context.getCacheDir();
            if (cacheDirectory == null) {
                String cacheDirectoryPath = "/data/data/" + context.getPackageName() + "/cache/";
                cacheDirectory = new File(cacheDirectoryPath);
                if(!cacheDirectory.exists() && !cacheDirectory.mkdirs()) {
                    throw new IllegalAccessException("can't create internal cache directory: " + cacheDirectory.getAbsolutePath());
                }
            }
        }
        if(cacheSize == 0) {
          cacheSize = 1024 * 1024 * 50;
        }
        return new LRUDiskCache(cacheDirectory, cacheSize);
    }

    public interface StreamCopyListener {

        boolean bytesCopied(int totalBytesCount, int copiedBytesCount);
    }
}
