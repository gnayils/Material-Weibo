package com.gnayils.obiew.weibo;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    private Context context;

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
        }
    }

    public static Bitmap get(String phrase, int inSampleSize) {
        Bitmap bitmap = null;
        if(instance != null) {
            Cursor cursor = instance.getReadableDatabase().query("emotion", new String[]{"_id", "phrase", "image"}, "phrase=?", new String[]{phrase}, null, null,null);
            if(cursor.moveToNext()) {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex("image"));
                options.inSampleSize = inSampleSize;
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            }
            if(cursor != null) {
                cursor.close();
            }
        }
        return bitmap;
    }

    public static void destroy() {
        if(instance != null) {
            instance.close();
        }
    }
}
