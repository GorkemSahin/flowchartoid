package com.sahin.gorkem.flowchartoid;

import android.app.Application;
import android.content.Context;

/**
 * Created by Gorkem on 4/17/2018.
 */

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}
