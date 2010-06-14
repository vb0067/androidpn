/**
 * $RCSfile$
 * $Revision: 2407 $
 * $Date: 2004-11-02 15:37:00 -0800 (Tue, 02 Nov 2004) $
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.androidpn.asmack.org.jivesoftware.smack;

/**
 * A listener for chat related events.
 *
 * @author Alexander Wenckus
 */
public interface ChatManagerListener {

    /**
     * Event fired when a new chat is created.
     *
     * @param chat the chat that was created.
     * @param createdLocally true if the chat was created by the local user and false if it wasn't.
     */
    void chatCreated(Chat chat, boolean createdLocally);
}
