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
package handling.cashshop;

import client.MapleCharacter;
import client.MapleClient;
import java.net.InetSocketAddress;

import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.mina.MapleCodecFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import server.MTSStorage;
import server.ServerProperties;

public class CashShopServer {

    private static String bindIP;
    private static String gatewayIP;
    private static InetSocketAddress InetSocketadd;
    private static int port = 8600;
    private static NioSocketAcceptor acceptor;
    private static PlayerStorage players, playersMTS;
    private static boolean finishedShutdown = false;
    private static MapleServerHandler handler;

    public static final void setup() {
        port = Short.valueOf(ServerProperties.getProperty("server.settings.cashshop.port", "8600"));
        bindIP = ServerProperties.getProperty("server.settings.ip.bind") + ":" + port;
        gatewayIP = ServerProperties.getProperty("server.settings.ip.gateway") + ":" + port;

        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());

        acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 15);

        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        acceptor.getSessionConfig().setKeepAlive(false);
        players = new PlayerStorage(-10);
        playersMTS = new PlayerStorage(-20);
        if (handler == null) {
            handler = new MapleServerHandler(-1, true);
        }
        acceptor.setHandler(handler);
        try {
            InetSocketadd = new InetSocketAddress(port);
            acceptor.bind(InetSocketadd);
            System.out.println("購物商城    : 綁定端口 " + port);
        } catch (final Exception e) {
            System.err.println("[購物商城] 綁定端口 " + port + " 失敗");
            e.printStackTrace();
            throw new RuntimeException("Binding failed.", e);
        }
    }

    public static final String getIP() {
        return bindIP;
    }

    public static final String getGatewayIP() {
        return gatewayIP;
    }

    public static final PlayerStorage getPlayerStorage() {
        return players;
    }

    public static final PlayerStorage getPlayerStorageMTS() {
        return playersMTS;
    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("[購物商城] 準備關閉...");
        System.out.println("[購物商城] 儲存資料中...");
        players.disconnectAll();
        playersMTS.disconnectAll();
        //MTSStorage.getInstance().saveBuyNow(true);
        System.out.println("[購物商城] 解除綁定端口...");
        Iterator<IoSession> iterator = acceptor.getManagedSessions().values().iterator();
        while (iterator.hasNext()) {

            iterator.next().close(true);
        }
        acceptor.unbind(new InetSocketAddress(port));

        System.out.println("[購物商城] 關閉完成...");
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
    
}
