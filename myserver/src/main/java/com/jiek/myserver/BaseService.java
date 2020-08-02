package com.jiek.myserver;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

abstract class BaseService extends Service {

    private static final String TAG = "BaseService";

    protected void l(String msg) {
        Log.e(TAG, "l: " + msg + " >> " + Thread.currentThread());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        l("onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        l("onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        l("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        l("onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        l("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        l("onTrimMemory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        l("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        l("onRebind");
    }

}
