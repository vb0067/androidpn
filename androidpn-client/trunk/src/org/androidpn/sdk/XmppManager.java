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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

/**
 * Class desciption here.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XmppManager {

    private static final String LOGTAG = XmppManager.class.getName();

    private Context context;

    private MainService.TaskSubmitter taskSubmitter;

    private MainService.TaskTracker taskTracker;

    private SharedPreferences sdkPreferences;

    private Editor editor;

    private String xmppHost;

    private int xmppPort;

    private XMPPConnection connection;

    private String username;

    private String password;

    private List<Runnable> taskList;

    private boolean running = false;

    private boolean connected = false;

    private Handler mHandler;

    private Runnable mRunnable;

    private Future mFuture;

    private PacketListener packetListener;

    private ConnectionListener connectionListener;

    private LoginListener loginListener;

    private Thread reconnect;

    public XmppManager(Context context,
            MainService.TaskSubmitter taskSubmitter,
            MainService.TaskTracker taskTracker) {
        this.context = context;
        this.taskSubmitter = taskSubmitter;
        this.taskTracker = taskTracker;

        sdkPreferences = context.getSharedPreferences(
                ServiceManager.SDK_PREFERENCES, Context.MODE_PRIVATE);
        editor = sdkPreferences.edit();

        xmppHost = sdkPreferences.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sdkPreferences.getInt(Constants.XMPP_PORT, 5222);
        username = sdkPreferences.getString(Constants.XMPP_USERNAME, username);
        password = sdkPreferences.getString(Constants.XMPP_PASSWORD, password);

        packetListener = new NotificationPacketListener(this);
        connectionListener = new PersistentConnectionListener(this);
        loginListener = new PersistentLoginListener(this);

        reconnect = new Reconnect(this);

        taskList = new ArrayList<Runnable>();
        mHandler = new Handler();

        mRunnable = new XmppTask(this);
    }

    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }

    private boolean isRegistered() {
        return sdkPreferences.contains(Constants.XMPP_USERNAME)
                && sdkPreferences.contains(Constants.XMPP_PASSWORD);
    }

    private void removeRegistration() {
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }

    private void submitConnectTask() {
        Log.d(LOGTAG, "submitConnectTask()...");
        runTask(new ConnectTask());
    }

    private void submitRegisterTask() {
        submitConnectTask();
        Log.d(LOGTAG, "submitRegisterTask()...");
        runTask(new RegisterTask());
    }

    private void submitLoginTask(LoginListener loginListener,
            ConnectionListener connectionListener) {
        submitRegisterTask();
        Log.d(LOGTAG, "submitLoginTask()...");
        runTask(new LoginTask(loginListener, connectionListener));
    }

    private void runTask() {
        Log.d(LOGTAG, "runTask()...");
        synchronized (taskList) {
            running = false;
            mHandler.removeCallbacks(mRunnable);
            mFuture = null;
            if (!taskList.isEmpty()) {
                Runnable runnable = (Runnable) taskList.get(0);
                taskList.remove(0);
                running = true;
                mFuture = taskSubmitter.submit(runnable);
                if (mFuture == null) {
                    taskTracker.decrease();
                }
                mHandler.postDelayed(mRunnable, 6000L);
            }
        }
        taskTracker.decrease();
        Log.d(LOGTAG, "runTask()...done");
    }

    private void runTask(Runnable runnable) {
        Log.d(LOGTAG, "runTask(runnable)...");
        taskTracker.increase();
        synchronized (taskList) {
            if (taskList.isEmpty() && !running) {
                running = true;
                mFuture = taskSubmitter.submit(runnable);
                if (mFuture == null) {
                    taskTracker.decrease();
                }
                mHandler.postDelayed(mRunnable, 6000L);
            } else {
                taskList.add(runnable);
            }
        }
        Log.d(LOGTAG, "runTask(runnable)... done");
    }

    public void connect() {
        Log.d(LOGTAG, "connect()...");
        connected = true;
        submitLoginTask(loginListener, connectionListener);
    }

    public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        connected = false;
        terminatePersistentConnection();
    }

    public void terminatePersistentConnection() {
        Log.d(LOGTAG, "terminatePersistentConnection()...");
        Runnable runnable = new Runnable() {

            final XmppManager xmppManager = XmppManager.this;

            public void run() {
                //                if (XmppManager.getXMPPConnection(xmppManager) != null
                //                        && XmppManager.getXMPPConnection(xmppManager)
                //                                .isConnected()
                //                        && !XmppManager.isLoggedIn(xmppManager)) {
                if (XmppManager.isConnected(xmppManager)
                        && !XmppManager.isLoggedIn(xmppManager)) {
                    Log.d(LOGTAG, "terminatePersistentConnection()... run()");
                    XmppManager.getXMPPConnection(xmppManager)
                            .removePacketListener(
                                    XmppManager.getPacketListener(xmppManager));
                    XmppManager.getXMPPConnection(xmppManager).disconnect();
                }
                XmppManager.runTask(xmppManager);
            }

        };
        Log.d(LOGTAG, "terminatePersistentConnection()... runTask()");
        runTask(runnable);
    }

    // ========================

    public static Context getContext(XmppManager xmppManager) {
        return xmppManager.context;
    }

    public static boolean isConnected(XmppManager xmppManager) {
        return xmppManager.isConnected();
    }

    public static boolean isAuthenticated(XmppManager xmppManager) {
        return xmppManager.isAuthenticated();
    }

    public static XMPPConnection getXMPPConnection(XmppManager xmppManager) {
        return xmppManager.connection;
    }

    public static String getUsername(XmppManager xmppManager) {
        return xmppManager.username;
    }

    public static String getPassword(XmppManager xmppManager) {
        return xmppManager.password;
    }

    public static SharedPreferences getSdkPreferences(XmppManager xmppManager) {
        return xmppManager.sdkPreferences;
    }

    public static boolean isRegistered(XmppManager xmppManager) {
        return xmppManager.isRegistered();
    }

    public static void setXMPPConnection(XmppManager xmppManager,
            XMPPConnection connection) {
        xmppManager.connection = connection;
    }

    public static void setUsername(XmppManager xmppManager, String username) {
        xmppManager.username = username;
    }

    public static void setPassword(XmppManager xmppManager, String password) {
        xmppManager.password = password;
    }

    public static void reconnect(XmppManager xmppManager,
            LoginListener loginListener, ConnectionListener connectionListener) {
        xmppManager.submitLoginTask(loginListener, connectionListener);
    }

    public static Handler getHandler(XmppManager xmppManager) {
        return xmppManager.mHandler;
    }

    public static void runTask(XmppManager xmppManager) {
        xmppManager.runTask();
    }

    public static boolean isLoggedIn(XmppManager xmppManager) {
        return xmppManager.connected;
    }

    public static PacketListener getPacketListener(XmppManager xmppManager) {
        return xmppManager.packetListener;
    }

    @SuppressWarnings("unchecked")
    static List getTaskList(XmppManager xmppManager) {
        return xmppManager.taskList;
    }

    @SuppressWarnings("unchecked")
    static Future getFutureTask(XmppManager xmppManager) {
        return xmppManager.mFuture;
    }

    // ====================

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public LoginListener getLoginListener() {
        return loginListener;
    }

    public void reconnect() {
        synchronized (reconnect) {
            if (!reconnect.isAlive()) {
                reconnect.setName("Xmpp Reconnection Thread");
                reconnect.start();
            }
        }
    }

    private class ConnectTask implements Runnable {

        final XmppManager xmppManager;

        private ConnectTask() {
            this.xmppManager = XmppManager.this;
        }

        public void run() {

            if (!XmppManager.isConnected(xmppManager)) {

                // Create the configuration for this new connection
                ConnectionConfiguration config = new ConnectionConfiguration(
                        xmppHost, xmppPort, "AndroidpnClient");
                //            config.setCompressionEnabled(true);
                //            config.setSASLAuthenticationEnabled(true);

                XMPPConnection connection = new XMPPConnection(config);
                XmppManager.setXMPPConnection(xmppManager, connection);

                try {
                    // Connect to the server
                    connection.connect();

                    // packet provider
                    ProviderManager.getInstance().addIQProvider("registration",
                            "androidpn:iq:registration",
                            new RegistrationProvider());
                    ProviderManager.getInstance().addIQProvider("notification",
                            "androidpn:iq:notification",
                            new RegistrationProvider());

                    // packet listener
                    connection.addPacketListener(
                            new NotificationPacketListener(xmppManager), null);

                } catch (XMPPException xe) {
                }

                XmppManager.runTask(xmppManager);

            } else {
                Log.i(LOGTAG, "XMPP connected already");

                XmppManager.runTask(xmppManager);
            }

        }
    }

    private class RegisterTask implements Runnable {

        final XmppManager xmppManager;

        private RegisterTask() {
            xmppManager = XmppManager.this;
        }

        public void run() {
            if (!xmppManager.isRegistered()) {

                // final XmppManager xmppManager = XmppManager.this;
                final String rUsername = newRandomUUID();
                final String rPassword = newRandomUUID();

                Registration registration = new Registration();
                //            PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                //                    registration.getPacketID()), new PacketTypeFilter(
                //                    Message.class));
                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()));

                PacketListener packetListener = new PacketListener() {

                    public void processPacket(Packet packet) {

                        Log.d("PacketListener", "processPacket().....");
                        Log.e("PacketListener", "packet=" + packet.toXML());

                        if (packet instanceof IQ) {
                            IQ response = (IQ) packet;
                            if (response.getType() == IQ.Type.ERROR) {
                                if (!response.getError().toString().contains(
                                        "409")) {
                                    Log.e(LOGTAG,
                                            "Unknown error while registering XMPP account! "
                                                    + response.getError()
                                                            .getCondition());
                                }
                            } else if (response.getType() == IQ.Type.RESULT) {
                                username = rUsername;
                                password = rPassword;
                                Log.d(LOGTAG, "username=" + username);
                                Log.d(LOGTAG, "password=" + password);

                                editor.putString(Constants.XMPP_USERNAME,
                                        username);
                                editor.putString(Constants.XMPP_PASSWORD,
                                        password);
                                editor.commit();

                                Log
                                        .i(LOGTAG,
                                                "Account registered successfully");

                            }
                        }
                    }
                };

                connection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);
                //registration.setTo(xmppHost);
                //            Map<String, String> attributes = new HashMap<String, String>();
                //            attributes.put("username", rUsername);
                //            attributes.put("password", rPassword);
                //            registration.setAttributes(attributes);
                registration.addAttribute("username", rUsername);
                registration.addAttribute("password", rPassword);
                connection.sendPacket(registration);

            } else {
                Log.i(LOGTAG, "Account registered already");
            }
        }
    }

    private class LoginTask implements Runnable {

        final XmppManager xmppManager;

        private LoginListener loginListener;

        private ConnectionListener connectionListener;

        private LoginTask(LoginListener loginListener,
                ConnectionListener connectionListener) {
            this.xmppManager = XmppManager.this;
            this.loginListener = loginListener;
            this.connectionListener = connectionListener;
        }

        public void run() {

            Log.e(LOGTAG, "isConnected()=" + connection.isConnected());
            Log.e(LOGTAG, "isAuthenticated()=" + connection.isAuthenticated());
            Log.e(LOGTAG, "isRegistered()=" + isRegistered());
            Log.e(LOGTAG, "!isAuthenticated() && isRegistered()="
                    + (!isAuthenticated() && isRegistered()));

            if (!XmppManager.isAuthenticated(xmppManager)) {

                Log.d(LOGTAG, "username=" + username);
                Log.d(LOGTAG, "password=" + password);

                try {
                    XmppManager.getXMPPConnection(xmppManager).login(
                            XmppManager.getUsername(xmppManager),
                            XmppManager.getPassword(xmppManager),
                            "AndroidpnXmppClient");
                    if (connectionListener != null) {
                        XmppManager.getXMPPConnection(xmppManager)
                                .addConnectionListener(connectionListener);
                    }

                } catch (XMPPException ex) {

                } catch (Exception ex) {

                }

            } else {
                Log.i(LOGTAG, "Account logged in already");
            }

        }
    }

}
