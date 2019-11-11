/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.mina;

import client.MapleClient;
import handling.MaplePacket;
import tools.MapleAESOFB;

import java.util.concurrent.locks.Lock;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import server.ServerProperties;

public class MaplePacketEncoder implements ProtocolEncoder {

    private static final boolean crypt = Boolean.parseBoolean(ServerProperties.getProperty("server.crypt", "false"));

    @Override
    public void encode(final org.apache.mina.core.session.IoSession session, final Object message, final ProtocolEncoderOutput out) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null) {
            final MapleAESOFB send_crypto = client.getSendCrypto();
            final byte[] input = ((MaplePacket) message).getBytes();
            final byte[] unencrypted = new byte[input.length];
            System.arraycopy(input, 0, unencrypted, 0, input.length);
            final byte[] ret = new byte[unencrypted.length + 4];
            final byte[] header = send_crypto.getPacketHeader(unencrypted.length);
            //MapleCustomEncryption.encryptData(unencrypted);

            final Lock mutex = client.getLock();
            mutex.lock();
            try {
                send_crypto.crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
                System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
                out.write(IoBuffer.wrap(ret));
            } finally {
                mutex.unlock();
            }
            if (crypt) {
                for (int i = 0; i < ret.length; i++) {
                    ret[i] ^= 0x0C;
                }
            }
        } else {
            byte[] input = ((MaplePacket) message).getBytes();
            if (crypt) {
                for (int i = 0; i < input.length; i++) {
                    input[i] ^= 0x0C;
                }
            }
            out.write(IoBuffer.wrap(input));
        }
    }

    @Override
    public void dispose(org.apache.mina.core.session.IoSession session) throws Exception {
        int a = 0;
    }
}
