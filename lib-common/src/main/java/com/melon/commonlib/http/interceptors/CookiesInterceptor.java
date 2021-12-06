package info.emm.commonlib.http.interceptors;

import android.content.SharedPreferences;

import java.io.IOException;
import java.util.HashSet;

import info.emm.commonlib.base.CommonApp;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
// this interceptor is used after the normal logging of OkHttp
public class CookiesInterceptor implements Interceptor {
    private  final String TAG = CookiesInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {


        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet) CommonApp.getAppContext().getSharedPreferences("httpcookies", CommonApp.getAppContext().MODE_PRIVATE).getStringSet("cookie", null);
        if (preferences != null) {
            for (String cookie : preferences) {
                builder.addHeader("Cookie", cookie);

            }
        }


        Response originalResponse = chain.proceed(builder.build());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            SharedPreferences.Editor config = CommonApp.getAppContext().getSharedPreferences("httpcookies", CommonApp.getAppContext().MODE_PRIVATE)
                    .edit();
            config.putStringSet("cookie", cookies);
            config.commit();
        }
        return originalResponse;
    }
}
