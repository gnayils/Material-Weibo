package com.gnayils.obiew.glide;

import android.graphics.BitmapFactory;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;

import java.io.File;
import java.io.IOException;

/**
 * Created by Gnayils on 21/05/2017.
 */

public class BitmapSizeDecoder implements ResourceDecoder<File, BitmapFactory.Options> {

    @Override
    public Resource<BitmapFactory.Options> decode(File source, int width, int height) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), options);
        return new SimpleResource<>(options);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
