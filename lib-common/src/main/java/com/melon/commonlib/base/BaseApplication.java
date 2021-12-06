package info.emm.commonlib.base;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * melon
 * on 2021/4/19
 */
public class BaseApplication extends Application {
    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    public static Context getAppContext() {
        return baseApplication;
    }
    public static Resources getAppResources() {
        return baseApplication.getResources();
    }

}
