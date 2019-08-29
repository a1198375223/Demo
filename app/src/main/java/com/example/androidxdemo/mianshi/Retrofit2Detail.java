package com.example.androidxdemo.mianshi;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 注解说明
 * 请求方法注解
 * @GET get请求
 * @POST post请求
 * @PUT put请求
 * @DELETE delete请求
 * @PATCH patch请求
 * @HEAD head请求
 * @OPTIONS option请求
 * @HTTP 通用注解, 可以替换以上的所有注解. 拥有3个属性: method, path, hasBody
 *
 * 请求头注解
 * @Headers 用于添加固定请求头,可以同时添加多个.通过该注解添加的请求头不会相互覆盖,而是共同存在
 * @Header 作为方法的参数传入, 用于添加不固定值的Header, 该注解会更新已有的请求头
 *
 * 请求参数注解
 * @Body 用于post发送非表单数据
 * @Filed 用于post的表单字段 Field和FiledMap需要FormUrlEncoded结合使用
 * @FiledMap 和@Field作用一致，用于不确定表单参数
 * @Part 用于表单字段， Part和PartMap与Multipart注解结合使用，适合文件上传的情况
 * @PartMap 用于表单字段, 默认接受的类型是Map<String, RequestBody>, 可以用实现多文件上传
 * @Path 用于url中的占位符
 * @Query 用于Get中指定参数
 * @QueryMap 和Query使用类似
 * @Url 指定请求路径
 *
 * 请求和响应格式注解
 * @FormUrlEncoded 表示请求发送编码表单数据，每个键值对需要使用@Filed注解
 * @Multipart 表示请求发送multipart数据， 需要配合使用@Part
 * @Streaming 表示响应用字节流的形式返回.如果没使用该注解, 默认会吧数据全部载入到内存中. 该注解在下载打文件的时候特别有用
 *
 *
 * 总结
 * 1. retrofit2内部使用的是动态代理的方式来实现与okhttp3的通信的. 将封装好的请求交给okhttp3来处理
 * 2. 在调用接口的网络请求方法的时候, retrofit2会解析注解来将该方法翻译成网络请求数据, 返回一个okhttp3的Call
 */
public class Retrofit2Detail {


    public Retrofit2Detail() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com")
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        RequestInterface api = retrofit.create(RequestInterface.class);
    }

    public interface RequestInterface {
        @FormUrlEncoded
        @POST("xx/xxx/xxx")
        Call<Response> getPersonalListInfo();
    }
}
