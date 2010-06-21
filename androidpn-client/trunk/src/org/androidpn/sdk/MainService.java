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

import java.util.Random;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Class desciption here.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class MainService extends Service {

	public static final String SERVICE_NAME = "org.androidpn.sdk.MainService";

	private static final String LOGTAG = "MainService";

	private SharedPreferences sdkPreferences;

	private TelephonyManager telephonyManager;

	private WifiManager wifiManager;

	private ConnectivityManager connectivityManager;

	private String deviceId;

	public MainService() {
	}

	@Override
	public void onCreate() {
		Log.d(getClass().getSimpleName(), "onCreate()...");

		sdkPreferences = getSharedPreferences(ServiceManager.SDK_PREFERENCES,
				Context.MODE_PRIVATE);

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		deviceId = telephonyManager.getDeviceId();
		Log.d(getClass().getSimpleName(), "deviceId=" + deviceId);

		if (deviceId == null || deviceId.trim().length() == 0
				|| deviceId.matches("0+")) {
			if (sdkPreferences.contains("EMULATOR_DEVICE_ID")) {
				deviceId = sdkPreferences.getString("EMULATOR_DEVICE_ID", "");
			} else {
				deviceId = (new StringBuilder("EMU")).append(
						(new Random(System.currentTimeMillis())).nextLong())
						.toString();
				sdkPreferences.edit().putString("EMULATOR_DEVICE_ID", deviceId);
				sdkPreferences.edit().commit();
			}
		}

//		try {
//			ConnectionConfiguration connConfig = new ConnectionConfiguration(
//					"192.168.123.101", 5222, "vysper");
//			// connConfig.setSecurityMode(SecurityMode.disabled);
//
//			XMPPConnection connection = new XMPPConnection(connConfig);
//			connection.connect();
//			Log.e(LOGTAG, "XMPP CONNECTED......");
//
//			PacketFilter packetFilter = new AndFilter(new PacketTypeFilter(
//					Message.class));
//			;
//
//			PacketListener packetListener = new PacketListener() {
//
//				public void processPacket(Packet packet) {
//
//					Log.e(LOGTAG, "packet.toString()=" + packet.toString());
//					Log.e(LOGTAG, "packet.toXML()=" + packet.toXML());
//
//				}
//
//			};
//
//			connection.addPacketListener(packetListener, packetFilter);
//
//			// Login Information
//			connection.login("user1@vysper.org", "password1");
//			// connection.login("scott", "tiger");
//			Log.e(LOGTAG, "XMPP LOGGED IN......");
//
//		} catch (XMPPException xe) {
//			xe.printStackTrace();
//		}
		
		XmppManager xmppManager = new XmppManager(this);
		xmppManager.connect();
		//xmppManager.login();

	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(getClass().getSimpleName(), "onStart()...");

	}

	@Override
	public void onDestroy() {
		Log.d(getClass().getSimpleName(), "onDestroy()...");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(getClass().getSimpleName(), "onBind()...");
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(getClass().getSimpleName(), "onRebind()...");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(getClass().getSimpleName(), "onUnbind()...");
		return true;
	}

	// ================

	public static Intent getIntent() {
		return new Intent(SERVICE_NAME);
	}

}
