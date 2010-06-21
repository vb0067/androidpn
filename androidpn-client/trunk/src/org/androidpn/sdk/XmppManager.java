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

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Class desciption here.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XmppManager {

	private static final String LOGTAG = "XmppManager";

	private Context context;

	private SharedPreferences sdkPreferences;

	private String xmppHost;

	private int xmppPort;

	private XMPPConnection connection;

	private String username;

	private String password;

	private boolean running;

	public XmppManager(Context context) {
		this.context = context;
		this.sdkPreferences = context.getSharedPreferences(
				ServiceManager.SDK_PREFERENCES, Context.MODE_PRIVATE);

		this.xmppHost = sdkPreferences.getString(KeyConstants.XMPP_HOST,
				"localhost");
		this.xmppPort = sdkPreferences.getInt(KeyConstants.XMPP_PORT, 5222);
		this.username = sdkPreferences.getString(KeyConstants.XMPP_USERNAME,
				username);
		this.password = sdkPreferences.getString(KeyConstants.XMPP_PASSWORD,
				password);
	}

	private boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	private boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}

	private boolean isRegistered() {
		return sdkPreferences.contains(KeyConstants.XMPP_USERNAME)
				&& sdkPreferences.contains(KeyConstants.XMPP_PASSWORD);
	}

	public void connect() {
		Log.e(LOGTAG, "connect().....");
		if (!isConnected()) {
			// Create the configuration for this new connection
			ConnectionConfiguration config = new ConnectionConfiguration(
					xmppHost, xmppPort);
			config.setCompressionEnabled(true);
			config.setSASLAuthenticationEnabled(true);

			connection = new XMPPConnection(config);

			try {
				// Connect to the server
				connection.connect();

				// ProviderManager.getInstance()
				// .addIQProvider("registration",
				// "xtify:iq:registration",
				// new RegistrationProvider());

			} catch (XMPPException xe) {
				Log.e(LOGTAG, "connect()..... failed.", xe);
			}
		}
	}

	public void login() {
		Log.e(LOGTAG, "login().....");
		if (!isAuthenticated()) {
			try {
				connection.login(username, password, "AndroidpnClient");
				Log.i(LOGTAG, "login()..... logged in successfully.");

			} catch (XMPPException xe) {
				Log.e(LOGTAG, "login()..... failed by a typical error.", xe);

			} catch (Exception e) {
				Log.e(LOGTAG, "login()..... failed by an unexpected error.", e);
			}
		} else {
			Log.i(LOGTAG, "login()..... logged in already.");
		}
	}

	public void register() {
		Log.e(LOGTAG, "register().....");

		if (!isRegistered()) {

		} else {

		}
	}

}
