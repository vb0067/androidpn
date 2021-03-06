/*
 * Copyright (C) 2010 The Androidpn Team
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.androidpn.server.XmppServer;
import org.androidpn.server.service.ServiceManager;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.util.StringUtils;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class AuthManager {

    private static final Log log = LogFactory.getLog(AuthManager.class);

    private static final Object DIGEST_LOCK = new Object();

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            log.error("Internal server error", e);
        }
    }

    public static String getPassword(String username)
            throws UserNotFoundException {
        return ServiceManager.getUserService().getUserByUsername(username)
                .getPassword();
    }

    public static AuthToken authenticate(String username, String password)
            throws UnauthorizedException {
        if (username == null || password == null) {
            throw new UnauthorizedException();
        }
        username = username.trim().toLowerCase();
        if (username.contains("@")) {
            int index = username.indexOf("@");
            String domain = username.substring(index + 1);
            if (domain.equals(XmppServer.getInstance().getServerName())) {
                username = username.substring(0, index);
            } else {
                throw new UnauthorizedException();
            }
        }
        try {
            if (!password.equals(getPassword(username))) {
                throw new UnauthorizedException();
            }
        } catch (UserNotFoundException unfe) {
            throw new UnauthorizedException();
        }
        return new AuthToken(username);
    }

    public static AuthToken authenticate(String username, String token,
            String digest) throws UnauthorizedException {
        if (username == null || token == null || digest == null) {
            throw new UnauthorizedException();
        }
        username = username.trim().toLowerCase();
        if (username.contains("@")) {
            int index = username.indexOf("@");
            String domain = username.substring(index + 1);
            if (domain.equals(XmppServer.getInstance().getServerName())) {
                username = username.substring(0, index);
            } else {
                throw new UnauthorizedException();
            }
        }
        try {
            String password = getPassword(username);
            String anticipatedDigest = createDigest(token, password);
            if (!digest.equalsIgnoreCase(anticipatedDigest)) {
                throw new UnauthorizedException();
            }
        } catch (UserNotFoundException unfe) {
            throw new UnauthorizedException();
        }

        return new AuthToken(username);
    }

    public static boolean authorize(String username, String principal) {
        if (log.isDebugEnabled()) {
            log.debug("Trying authorize(" + username + " , " + principal + ")");
        }
        try {
            ServiceManager.getUserService().getUserByUsername(username);
        } catch (UserNotFoundException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isPlainSupported() {
        return true;
    }

    public static boolean isDigestSupported() {
        return true;
    }

    private static String createDigest(String token, String password) {
        synchronized (DIGEST_LOCK) {
            digest.update(token.getBytes());
            return StringUtils.encodeHex(digest.digest(password.getBytes()));
        }
    }

}
