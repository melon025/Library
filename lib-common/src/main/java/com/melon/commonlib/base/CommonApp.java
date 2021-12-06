package info.emm.commonlib.base;

import android.content.Context;

public class CommonApp {

    private static Context app;

    public static void init(Context appContent) {
        app = appContent;
    }

    public static Context getAppContext() {
        return app;
    }
}
