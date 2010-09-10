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
package org.androidpn.server.xmpp.net;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class IoBufferWriter extends Writer {

    private CharsetEncoder encoder;

    private IoBuffer ioBuffer;

    public IoBufferWriter(IoBuffer ioBuffer, CharsetEncoder encoder) {
        this.encoder = encoder;
        this.ioBuffer = ioBuffer;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        ioBuffer.putString(new String(cbuf, off, len), encoder);
    }

    @Override
    public void flush() throws IOException {
        // Ignore
    }

    @Override
    public void close() throws IOException {
        // Ignore
    }

}