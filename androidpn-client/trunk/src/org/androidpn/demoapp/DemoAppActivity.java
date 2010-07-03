package org.androidpn.demoapp;

import org.androidpn.sdk.ServiceManager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class DemoAppActivity extends Activity {

    private ServiceManager serviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //        Intent service = new Intent("org.androidpn.sdk.MainService");
        //        //service.putExtra("update-rate", 5000);
        //        startService(service);

        //        final Context context = this;
        //        Thread serviceThread = new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                serviceManager = new ServiceManager(context);
        //                serviceManager.startService();
        //            }
        //        });
        //        serviceThread.start();

        serviceManager = new ServiceManager(this);
        // serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();

        Log.d("DemoAppActivity", "onCreate()...");

        //        try {
        //            PackageManager packageManager = context.getPackageManager();
        //            ApplicationInfo applicationInfo = packageManager
        //                    .getApplicationInfo(context.getPackageName(), 128);
        //
        //            Log.e("", applicationInfo.className);
        //            Log.e("", applicationInfo.manageSpaceActivityName);
        //
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //            Log.e("", e.getMessage(), e);
        //        }

    }

}