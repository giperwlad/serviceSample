package moba.gipger.com.timerservice;

import android.app.Application;

/**
 * Created by vladgerasimenko on 12/09/2017.
 */

public class TimerApplication extends Application {
    private static TimerApplication instance;

    public static TimerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
