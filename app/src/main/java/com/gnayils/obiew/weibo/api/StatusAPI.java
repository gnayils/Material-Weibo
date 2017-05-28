package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.UploadedPic;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 16/11/2016.
 */

public interface StatusAPI {

    @GET("2/statuses/home_timeline.json")
    Observable<StatusTimeline> homeTimeline(@Query("max_id")long maxId, @Query("since_id")long sinceId);

    @GET("2/statuses/repost_timeline.json")
    Observable<RepostTimeline> repostTimeline(@Query("id") long statusId, @Query("max_id") long maxId, @Query("since_id") long sinceId);

    @GET("2/statuses/user_timeline.json")
    Observable<StatusTimeline> userTimeline(@Query("uid") long uid, @Query("feature")int feature, @Query("count") int count, @Query("page") int page);

    @FormUrlEncoded
    @POST("2/statuses/update.json")
    Observable<Status> update(@Field("status") String status);

    @Multipart
    @POST("2/statuses/upload_pic.json")
    Observable<UploadedPic> uploadPic(@Part("pic\"; filename=\"image.png") RequestBody image);

    @FormUrlEncoded
    @POST("2/statuses/upload_url_text.json")
    Observable<Status> uploadUrlText(@Field("status") String status, @Field("pic_id") String picId);

    @Multipart
    @POST("2/statuses/upload.json")
    Observable<Status> upload(@Part("status") RequestBody status, @Part() MultipartBody.Part image);

    @Multipart
    @POST("2/statuses/upload.json")
    Observable<Status> upload(@Part("status") RequestBody status, @Part("pic\"; filename=\"image.png") RequestBody image);

    @Deprecated
    @Multipart
    @POST("2/statuses/upload.json")
    Observable<Status> upload(@Part("status") RequestBody status, @PartMap Map<String, RequestBody> pic);

}
