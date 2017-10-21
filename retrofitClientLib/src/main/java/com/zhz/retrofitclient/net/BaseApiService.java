package com.zhz.retrofitclient.net;



import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 *
 */
public interface BaseApiService {

    @GET()
    Flowable<ResponseBody> get(
            @Url String url,
            @QueryMap Map<String, String> maps
    );

    @FormUrlEncoded
    @POST()
    Flowable<ResponseBody> post(
            @Url String url,
            @FieldMap Map<String, String> maps);

    //@Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头

    @POST()
    Flowable<BaseResponse> json(
            @Url String url,
            @Body RequestBody jsonStr);

    @Multipart
    @POST()
    Flowable<ResponseBody> upLoadFile(
            @Url String url,
            @Part() RequestBody requestBody);

    @POST()
    Flowable<ResponseBody> uploadFiles(
            @Url String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap Map<String, RequestBody> maps);

    @Streaming
    @GET
    Flowable<ResponseBody> downloadFile(@Url String fileUrl);


}
