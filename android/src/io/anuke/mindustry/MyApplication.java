package io.anuke.mindustry;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import io.anuke.mindustry.game.Version;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.BUGLY_ID != null && !"".equals(BuildConfig.BUGLY_ID)) {
            CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_ID, false);
            Log.e("lq", "buglyid:" + BuildConfig.BUGLY_ID);
        }
    }
}
