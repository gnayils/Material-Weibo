package com.gnayils.obiew.weibo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.LRUMemoryCache;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gnayils on 04/02/2017.
 */

public class EmotionLibrary {

    public static final String TAG = EmotionLibrary.class.getSimpleName();

    private static LRUMemoryCache emotionCache = LRUMemoryCache.create(App.context(), 1 * 1024 * 1024);
    private static Map<String, Integer> emotionResIdMap = new HashMap<>();

    static {
        String[] emotions = App.resources().getStringArray(R.array.emotion_map);
        for(String emotion : emotions) {
            String[] emotionInfo = emotion.split(",");
            String emotionKey = emotionInfo[0];
            String emotionResIdStr = emotionInfo[1];
            try {
                Field resIdField = R.drawable.class.getDeclaredField(emotionResIdStr);
                emotionResIdMap.put(emotionKey, (Integer) resIdField.get(null));
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "cannot find the emotion resource id: "  + emotionKey, e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "failed to get the emotion resource id: "  + emotionKey, e);
            }
        }
    }

    public static Bitmap get(String key) {
        Bitmap emotion = emotionCache.get(key);
        if(emotion == null) {
            Integer resId = emotionResIdMap.get(key);
            if(resId == null) {
                return null;
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                emotion = BitmapFactory.decodeResource(App.resources(), resId, options);
                emotionCache.put(key, emotion);
            }
        }
        return emotion;
    }
}
