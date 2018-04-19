package com.example.mint.model;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mint on 2018/4/15.
 */

public class HttpHelper {
    private OkHttpClient okHttpClient;
    private static HttpHelper instance;

    public static HttpHelper getInstance() {
        if (instance == null) {
            instance = new HttpHelper();
        }
        return instance;
    }

    private HttpHelper() {
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.3 Mobile/14E277 Safari/603.1.30")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }

    public Flowable<String> parseIcon(final String url){
        return Flowable.fromCallable(new Callable<String>(){
            @Override
            public String call() throws Exception {
                Thread.sleep(5000);
                String iconUrl;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    iconUrl = truncateIconUrl(response.body().string());
                }else {
                    iconUrl = "";
                }
                return iconUrl;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    /**
     * 解析获取Html body中的图标链接
     * @param body html body
     * @return 消息链接
     */
    public static String truncateIconUrl(String body){
        Log.d("zgl",body);
        long startTimeMillis = System.currentTimeMillis();
        String iconUrl = "";
        Pattern pattern = Pattern.compile("<link([^>]*?)rel=\"apple-touch-icon([^>]*?)>");
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()){
            matcher.end();
            iconUrl = matcher.group();

            pattern = Pattern.compile("href( *?)=( ?)\"(.*?)\"");
            matcher = pattern.matcher(iconUrl);
            if(matcher.find()){
                matcher.end();
                iconUrl = matcher.group();
                iconUrl = iconUrl.substring(iconUrl.indexOf("\"") + 1,iconUrl.lastIndexOf("\""));

                if(!iconUrl.startsWith("http")){
                    iconUrl = "http:" + iconUrl;
                }
            }

        }
        Log.d("zgl",iconUrl);
        Log.d("zgl","parse time millis-" + (System.currentTimeMillis() - startTimeMillis));
        return iconUrl;
    }
}
