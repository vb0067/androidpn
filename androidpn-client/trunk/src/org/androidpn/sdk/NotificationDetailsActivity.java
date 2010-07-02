/*
 * Copyright 2010 the original author or authors.
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
package org.androidpn.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationDetailsActivity extends Activity {

    private static final String LOGTAG = NotificationDetailsActivity.class
            .getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sdkPref = this.getSharedPreferences(
                Constants.SDK_PREFERENCES, Context.MODE_PRIVATE);

        Log.e(LOGTAG, "========================");

        Intent intent = getIntent();
        String notificationId = intent.getStringExtra("NOTIFICATION_ID");
        String notificationAppKey = intent
                .getStringExtra("NOTIFICATION_APP_KEY");
        String notificationFrom = intent.getStringExtra("NOTIFICATION_FROM");
        String notificationMessage = intent
                .getStringExtra("NOTIFICATION_MESSAGE");
        String notificationTicker = intent
                .getStringExtra("NOTIFICATION_TICKER");
        String notificationUrl = intent.getStringExtra("NOTIFICATION_URL");

        Log.d(LOGTAG, "notificationId=" + notificationId);
        Log.d(LOGTAG, "notificationAppKey=" + notificationAppKey);
        Log.d(LOGTAG, "notificationFrom=" + notificationFrom);
        Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
        Log.d(LOGTAG, "notificationTicker=" + notificationTicker);
        Log.d(LOGTAG, "notificationUrl=" + notificationUrl);

        //        Display display = getWindowManager().getDefaultDisplay();
        //        View rootView;
        //        if (display.getWidth() > display.getHeight()) {
        //            rootView = null;
        //        } else {
        //            rootView = null;
        //        }
        
        View rootView = createView(this);
        setContentView(rootView);
    }

    private View createView(Context context) {

        LinearLayout linearLayout = new LinearLayout(context);
        // linearLayout.setBackgroundResource(0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView text1 = new TextView(context);
        text1.setTextSize(12F);
        //textView.setTextColor();
        text1.setText("TITLE---");
        linearLayout.addView(text1);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(30, 20, 30, 0);

        Button closeButton = new Button(this);
        closeButton.setText("Close");
        closeButton.setWidth(100);
        linearLayout.addView(closeButton, layoutParams);

        Button viewButton = new Button(this);
        viewButton.setText("View");
        viewButton.setWidth(100);
        linearLayout.addView(viewButton, layoutParams);

        return linearLayout;
    }

}
