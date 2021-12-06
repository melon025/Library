package info.emm.commonlib.http.interceptors;

import android.content.Context;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 接口参数包装拦截器
 *
 * @author baoy
 * @version 1.0
 *          Create by 2017/6/30 上午9:56
 */
public class CommonParamsInterceptor implements Interceptor {

    private Context mContext;
    private String TAG=CommonParamsInterceptor.class.getSimpleName();

    public CommonParamsInterceptor(Context context) {
        this.mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String method = originalRequest.method();
        if (method.equalsIgnoreCase("get")) { //GET请求的参数封装

            HttpUrl httpUrl = originalRequest.url();
            String urlParam = httpUrl.query();

            RequestParamsWrapper requestParamsWrapper = new RequestParamsWrapper(mContext, urlParam);



            originalRequest = originalRequest.newBuilder()
                    .headers(requestParamsWrapper.getRequestHeaders(originalRequest.headers()))
                    .method(originalRequest.method(), originalRequest.body())
                    .url(requestParamsWrapper.getHttpUrl(httpUrl))
                    .build();
        } else if (method.equalsIgnoreCase("post")) { //POST请求的参数封装

            RequestBody requestBody = originalRequest.body();
            RequestParamsWrapper requestParamsWrapper = new RequestParamsWrapper(mContext, requestBody);
            originalRequest = originalRequest.newBuilder()
                    .method(originalRequest.method(), requestParamsWrapper.getRequestBody(originalRequest.body()))
                    .headers(requestParamsWrapper.getRequestHeaders(originalRequest.headers()))
                    .build();
        }

        return chain.proceed(originalRequest);
    }
}
