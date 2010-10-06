/*
 * Copyright (C) 2010 The Androidpn Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import java.util.Random;

import org.androidpn.demoapp.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/** 
 * This class is to notify the user of messages with NotificationManager.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {

    private static final String LOGTAG = LogUtil.makeLogTag(Notifier.class);

    private static final Random random = new Random(System.currentTimeMillis());

    private Context context;

    private SharedPreferences sharedPrefs;

    private NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;
        this.sharedPrefs = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notify(String notificationId, String apiKey, String title,
            String message, String ticker, String url) {
        Log.d(LOGTAG, "notify()...");

        if (isNotificationEnabled()) {
            Notification notification = new Notification();
            // notification.number++;
            notification.icon = getNotificationIcon();
            notification.defaults = Notification.DEFAULT_LIGHTS;
            if (isNotificationSoundEnabled()) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (isNotificationVibrateEnabled()) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            Log.d(LOGTAG, "notificationId=" + notificationId);
            Log.d(LOGTAG, "notificationApiKey=" + apiKey);
            Log.d(LOGTAG, "notificationTitle=" + title);
            Log.d(LOGTAG, "notificationMessage=" + message);
            Log.d(LOGTAG, "notificationTicker=" + ticker);
            Log.d(LOGTAG, "notificationUrl=" + url);

            Intent clickIntent = new Intent(
                    Constants.ACTION_NOTIFICATION_CLICKED);
            //            Intent clickIntent = new Intent(context, NotificationDetailsActivity.class);
            //            clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            clickIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            clickIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            clickIntent.putExtra(Constants.NOTIFICATION_TITLE, title);
            clickIntent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
            clickIntent.putExtra(Constants.NOTIFICATION_TICKER, ticker);
            clickIntent.putExtra(Constants.NOTIFICATION_URL, url);
            //        positiveIntent.setData(Uri.parse((new StringBuilder(
            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //                "/").append(System.currentTimeMillis()).toString()));
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(
                    context, 0, clickIntent, 0);

            Intent clearIntent = new Intent(
                    Constants.ACTION_NOTIFICATION_CLEARED);
            clearIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            clearIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //        negativeIntent.setData(Uri.parse((new StringBuilder(
            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //                "/").append(System.currentTimeMillis()).toString()));
            // PendingIntent clearPendingIntent = PendingIntent.getBroadcast(
            //         context, 0, clearIntent, 0);

            if (ticker != null && ticker.length() > 0) {
                notification.tickerText = ticker;
            } else {
                notification.tickerText = message;
            }
            notification.when = System.currentTimeMillis();
            notification.setLatestEventInfo(context, title, message,
                    clickPendingIntent);
            // notification.deleteIntent = clearPendingIntent;

            notificationManager.notify(random.nextInt(), notification);

            // Toast.makeText(context, title, Toast.LENGTH_SHORT).show();

        } else {
            Log.w(LOGTAG, "Notificaitons disabled.");
        }
    }

    private int getNotificationIcon() {
        return sharedPrefs.getInt(Constants.NOTIFICATION_ICON,
                R.drawable.notification);
    }

    private boolean isNotificationEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
                true);
    }

    private boolean isNotificationSoundEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
    }

    private boolean isNotificationVibrateEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
    }

}
