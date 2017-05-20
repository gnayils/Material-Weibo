package com.gnayils.obiew.weibo;

import android.content.Context;
import android.util.Log;

import com.gnayils.obiew.weibo.bean.AccessToken;
import com.gnayils.obiew.weibo.bean.User;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * Created by Gnayils on 20/05/2017.
 */

public class Account {

    public static User user;
    public static AccessToken accessToken;

    public static final String CACHE_FILE_NAME = "Account";

    public static final String TAG = Account.class.getSimpleName();

    public static void cache(Context context) {
        if(user == null || accessToken == null) {
            throw new IllegalStateException("nothing to cache...");
        }
        File file = context.getFileStreamPath(CACHE_FILE_NAME);
        ObjectOutputStream objectOutputStream = null;
        try {
            file.createNewFile();
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(Account.user);
            objectOutputStream.writeObject(Account.accessToken);
            objectOutputStream.flush();
        } catch (Exception e) {
            Log.e(TAG, "cache account failed", e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean loadCache(Context context) {
        File file = context.getFileStreamPath(CACHE_FILE_NAME);
        if (file.exists()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(new FileInputStream(file));
                Account.user = (User) objectInputStream.readObject();
                Account.accessToken = (AccessToken) objectInputStream.readObject();
                return true;
            } catch (Exception e) {
                Log.e(TAG, "get cached account failed", e);
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    public static void clearCache(Context context) {
        File file = context.getFileStreamPath(CACHE_FILE_NAME);
        file.delete();
    }
}
