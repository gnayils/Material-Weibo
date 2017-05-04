package com.gnayils.obiew.weibo.bean;

import java.io.Serializable;

/**
 * Created by Gnayils on 19/03/2017.
 */

public class PicUrls implements Serializable {

    public String thumbnail_pic;

    public String middleThumbnailPic() {
        if(thumbnail_pic != null) {
            return thumbnail_pic.replace("/thumbnail/", "/bmiddle/");
        }
        return null;
    }

    public String largeThumbnailPic() {
        if(thumbnail_pic != null) {
            return thumbnail_pic.replace("/thumbnail/", "/large/");
        }
        return null;
    }
}
