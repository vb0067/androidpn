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
package org.androidpn.server.mina;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.vysper.mina.MinaBackedSessionContext;
import org.apache.vysper.xml.fragment.XMLText;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XmppIoHandler implements IoHandler {

    public static final String ATTRIBUTE_ANDROIDPN_SESSION = "androidpnSession";

    public static final String ATTRIBUTE_ANDROIDPN_SESSIONSTATEHOLDER = "androidpnSessionStateHolder";

    private Log log = LogFactory.getLog(getClass());

    private ServerRuntimeContext serverRuntimeContext;

    public void setServerRuntimeContext(
            ServerRuntimeContext serverRuntimeContext) {
        this.serverRuntimeContext = serverRuntimeContext;
    }

    public void sessionCreated(IoSession session) throws Exception {
        log.debug("sessionCreated()...");
        SessionStateHolder stateHolder = new SessionStateHolder();
        SessionContext sessionContext = new MinaBackedSessionContext(
                serverRuntimeContext, stateHolder, session);
        session.setAttribute(ATTRIBUTE_ANDROIDPN_SESSION, sessionContext);
        session.setAttribute(ATTRIBUTE_ANDROIDPN_SESSIONSTATEHOLDER, stateHolder);
    }

    public void sessionOpened(IoSession session) throws Exception {
        log.debug("sessionOpened()...");
    }

    public void sessionClosed(IoSession session) throws Exception {
        log.debug("sessionClosed()...");
    }

    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        log.debug("sessionIdle()...");
    }

    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        log.debug("sessionCaught()...");
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        log.debug("messageReceived()...");

        log.debug(session.getRemoteAddress());
        log.debug("message=" + message);

        if (!(message instanceof Stanza)) {
            if (message instanceof XMLText) {
                String text = ((XMLText) message).getText();
                // tolerate reasonable amount of whitespaces for stanza separation
                if (text.length() < 40 && text.trim().length() == 0)
                    return;
            }

            log.debug("NO STANZA...");
            return;
        }

        Stanza stanza = (Stanza) message;

        log.debug(">>>>>>>>>>>>>> " + stanza.toString());
        log.debug("PROCESSING STANZA...");

        SessionContext sessionContext = extractSessionContext(session);
        SessionStateHolder stateHolder = (SessionStateHolder) session
                .getAttribute(ATTRIBUTE_ANDROIDPN_SESSIONSTATEHOLDER);

        serverRuntimeContext.getStanzaProcessor().processStanza(
                serverRuntimeContext, sessionContext, stanza, stateHolder);

    }

    public void messageSent(IoSession session, Object message) throws Exception {
        log.debug("messageSent()...");
    }

    private SessionContext extractSessionContext(IoSession ioSession) {
        return (SessionContext) ioSession
                .getAttribute(ATTRIBUTE_ANDROIDPN_SESSION);
    }

}