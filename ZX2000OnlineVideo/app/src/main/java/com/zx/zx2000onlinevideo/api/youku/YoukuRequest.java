package com.zx.zx2000onlinevideo.api.youku;

import com.zx.zx2000onlinevideo.OnlineVideoApp;
import com.zx.zx2000onlinevideo.config.YoukuConfig;
import com.zx.zx2000onlinevideo.utils.NetWorkUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User: ShaudXiao
 * Date: 2016-08-04
 * Time: 14:22
 * Company: zx
 * Description:数据请求
 * FIXME
 */

public class YoukuRequest {

    public YoukuRequest() {}

    private static final String CACHE_CONTROL = "Cache-Control";
    private static final Object monitor = new Object();
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(OnlineVideoApp.getContext())) {
                int maxAge = 60; // 在线缓存在1分钟内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    private static File httpCacheDirectory = new File(OnlineVideoApp.getContext().getCacheDir(), "YoukuCache");
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(httpCacheDirectory, cacheSize);

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)//不添加离线缓存无效
            .cache(cache)
            .build();

    private static YoukuApi sYoukuApi = null;

    public static YoukuApi getYoukuApi() {
        synchronized (monitor) {
            if(sYoukuApi == null) {
                sYoukuApi = new Retrofit.Builder()
                        .baseUrl(YoukuConfig.YOUKU_BASE_URL)
                        .client(client)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(YoukuApi.class);
            }
        }

        return sYoukuApi;
    }
}
