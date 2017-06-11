package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.Reposts;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.Statuses;
import com.gnayils.obiew.weibo.bean.UploadedPic;

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
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 16/11/2016.
 */

public interface StatusAPI {

    @GET("2/statuses/home_timeline.json")
    Observable<Statuses> homeTimeline(@Query("feature") int feature, @Query("page") int page, @Query("count") int count);

    @GET("2/statuses/bilateral_timeline.json")
    Observable<Statuses> bilateralTimeline(@Query("feature") int feature, @Query("page") int page, @Query("count") int count);

    @GET("2/statuses/public_timeline.json")
    Observable<Statuses> publicTimeline(@Query("feature") int feature, @Query("page") int page, @Query("count") int count);

    @GET("2/statuses/mentions.json")
    Observable<Statuses> mentions(@Query("page") int page, @Query("count") int count);

    @GET("2/statuses/repost_timeline.json")
    Observable<Reposts> repostTimeline(@Query("id") long statusId, @Query("page") int page, @Query("count") int count);

    @GET("2/statuses/user_timeline.json")
    Observable<Statuses> userTimeline(@Query("uid") long uid, @Query("feature")int feature, @Query("page") int page, @Query("count") int count);

    @FormUrlEncoded
    @POST("2/statuses/update.json")
    Observable<Status> update(@Field("source") String source, @Field("status") String status);

    @Multipart
    @POST("2/statuses/upload.json")
    Observable<Status> upload(@Part("status") RequestBody status, @Part() MultipartBody.Part image);

    @Multipart
    @POST("2/statuses/upload_pic.json")
    Observable<UploadedPic> uploadPic(@Part("pic\"; filename=\"image.png") RequestBody image);

    @FormUrlEncoded
    @POST("2/statuses/upload_url_text.json")
    Observable<Status> uploadUrlText(@Field("source") String source, @Field("status") String status, @Field("pic_id") String picId);



    @Deprecated
    @Multipart
    @POST("2/statuses/upload.json")
    Observable<Status> upload(@Part("status") RequestBody status, @Part("pic\"; filename=\"image.png") RequestBody image);

    @Deprecated
    @Multipart
    @POST("2/statuses/upload.json")
    Observable<Status> upload(@Part("status") RequestBody status, @PartMap Map<String, RequestBody> pic);

    @Deprecated
    @GET("2/statuses/home_timeline.json")
    Observable<Statuses> homeTimeline(@Query("max_id")long maxId, @Query("since_id")long sinceId);

    @Deprecated
    @GET("2/statuses/repost_timeline.json")
    Observable<Reposts> repostTimeline(@Query("id") long statusId, @Query("max_id") long maxId, @Query("since_id") long sinceId);

}
