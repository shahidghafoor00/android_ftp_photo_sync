package pk.codebase.ftp_images_uploader;

import android.app.Application;
import android.content.Context;


public class AppGlobals extends Application {
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
