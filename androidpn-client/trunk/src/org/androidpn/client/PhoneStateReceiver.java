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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String LOGTAG = LogUtil
            .makeLogTag(PhoneStateReceiver.class);

    private MainService mainService;

    public PhoneStateReceiver(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "PhoneStateReceiver.onReceive()...");
        String action = intent.getAction();
        Log.d(LOGTAG, "action=" + action);        

        Object parcelableExtra = intent.getParcelableExtra("networkInfo");
        boolean connected = false;
        if (parcelableExtra instanceof NetworkInfo) {
            State state = ((NetworkInfo) parcelableExtra).getState();
            if (state != null) {
                Log.d(LOGTAG, "Wifi data state is " + state.toString());
                if (State.CONNECTED.equals(state)) {
                    connected = true;
                }
            }
        } else if (MainService.getConnectivityManager(mainService)
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            connected = true;
        }
        if (connected) {
            MainService.restart(mainService);
        }

    }

}
