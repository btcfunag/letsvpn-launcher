package com.example.letsvpnlauncher;

import android.app.Application;
import android.content.Context;
import com.FSpaceCore.FCore;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        FCore.get().init(this);
        FCore.get().setAllowSystemInteraction(true);
        FCore.get().setAutoPreloadApplication(true);
        FCore.get().setHideRoot(true);
        FCore.get().setHideVPN(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (FCore.isClient()) return;
    }
}
