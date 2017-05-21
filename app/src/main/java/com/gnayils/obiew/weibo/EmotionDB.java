package com.gnayils.obiew.weibo;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.gnayils.obiew.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by iUser on 4/5/17.
 */

public class EmotionDB extends SQLiteOpenHelper {

    public static final String TAG = EmotionDB.class.getSimpleName();

    public static final String DATABASE_NAME = "emotion.db";
    public static final int VERSION = 1;

    private static EmotionDB instance;
    private static BitmapFactory.Options options;
    private static LruCache<String, Bitmap> cache;

    private Context context;
    private float emotionSize;

    public EmotionDB(Context context) {
        this(context, DATABASE_NAME, null, VERSION, null);
    }

    public EmotionDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.context = context;
        File destDatabaseFile = context.getDatabasePath(DATABASE_NAME);
        if(!destDatabaseFile.exists()) {
            copyDataBase(destDatabaseFile);
        }
        emotionSize = context.getResources().getDimension(R.dimen.emotion_size);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void copyDataBase(File destDatabaseFile) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            inputStream = context.getAssets().open(DATABASE_NAME);
            outputStream = new FileOutputStream(destDatabaseFile);
            byte[] buffer = new byte[1024];
            int readLength;
            while ((readLength = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readLength);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initialize(Context context) {
        if(instance == null) {
            instance = new EmotionDB(context);
            options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            cache  = new LruCache<String, Bitmap>(1024) {

                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / 1024;
                }
            };
        }
    }

    public static Bitmap get(String phrase) {
        Bitmap bitmap = null;
        if(instance != null) {
            String key = phrase;
            bitmap = getFromCache(key);
            if(bitmap == null) {
                Cursor cursor = instance.getReadableDatabase().query("emotion", new String[]{"image"}, "phrase=?", new String[]{phrase}, null, null,null);
                if(cursor.moveToNext()) {
                    byte[] bytes = cursor.getBlob(cursor.getColumnIndex("image"));
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int)instance.emotionSize + 1, (int) instance.emotionSize + 1, false);
                    putToCache(key, bitmap);
                }
                cursor.close();
            }
        }
        return bitmap;
    }

    private static Bitmap getFromCache(String key) {
        return cache.get(key);
    }

    private static void putToCache(String key, Bitmap bitmap) {
        if(getFromCache(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public static void destroy() {
        if(instance != null) {
            instance.close();
        }
    }
}
