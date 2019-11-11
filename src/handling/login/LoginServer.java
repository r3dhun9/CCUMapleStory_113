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
package handling.login;

import client.MapleClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import handling.MapleServerHandler;
import handling.mina.MapleCodecFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.WeakHashMap;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import server.ServerProperties;
import server.Timer;
import server.Timer.LoginTimer;
import tools.FilePrinter;

public class LoginServer {

    private static String ip = "127.0.0.1";
    private static short port = 8484;
    private static NioSocketAcceptor acceptor;
    private static Map<Integer, Integer> load = new HashMap<>();
    private static String serverName, eventMessage;
    private static byte flag;
    private static int maxCharacters, userLimit, usersOn = 0;
    private static boolean finishedShutdown = true;
    public static boolean AutoRegister = false, adminOnly = false;
    private static AccountStorage clients;
    private static final Map<Integer, String> LoginMacs = new WeakHashMap<>();

    public static final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public static final void setup() {
        try {
            ip = ServerProperties.getProperty("server.settings.ip.listen");
            port = Short.parseShort(ServerProperties.getProperty("server.settings.login.port"));
            userLimit = Integer.parseInt(ServerProperties.getProperty("server.settings.userlimit"));
            serverName = ServerProperties.getProperty("server.settings.serverName");
            eventMessage = ServerProperties.getProperty("server.settings.eventMessage");
            flag = Byte.parseByte(ServerProperties.getProperty("server.settings.flag"));
            adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.admin", "false"));
            maxCharacters = Integer.parseInt(ServerProperties.getProperty("server.settings.maxCharacters", "3"));
            AutoRegister = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.autoRegister", "false"));
            IoBuffer.setUseDirectBuffer(false);
            IoBuffer.setAllocator(new SimpleBufferAllocator());

            acceptor = new NioSocketAcceptor();
            acceptor.getFilterChain().addLast("codec", (IoFilter) new ProtocolCodecFilter(new MapleCodecFactory()));

            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 15);
            acceptor.setHandler(new MapleServerHandler(-1, false));
            acceptor.getSessionConfig().setTcpNoDelay(true);
            acceptor.bind(new InetSocketAddress(ip, port));
            System.out.println("\n【登入伺服器】  - 監聽端口: " + Short.toString(port) + " \n");

        } catch (IOException ex) {
            FilePrinter.printError(FilePrinter.LoginServer, ex, "IOException");
        }

    }

    public static final void shutdown() {
        if (finishedShutdown) {
            System.out.println("【登入伺服器】 已經關閉了...無法執行此動作");
            return;
        }
        System.out.println("【登入伺服器】 關閉中...");
        Iterator<IoSession> iterator = acceptor.getManagedSessions().values().iterator();
        while (iterator.hasNext()) {
            iterator.next().close(true);
        }
        acceptor.unbind(new InetSocketAddress(port));
        System.out.println("【登入伺服器】 關閉完畢...");
        finishedShutdown = true; //nothing. lol
    }

    public static final String getServerName() {
        return serverName;
    }

    public static final String getEventMessage() {
        return eventMessage;
    }

    public static final byte getFlag() {
        return flag;
    }

    public static final int getMaxCharacters() {
        return maxCharacters;
    }

    public static final Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static final void setEventMessage(final String newMessage) {
        eventMessage = newMessage;
    }

    public static final void setFlag(final byte newflag) {
        flag = newflag;
    }

    public static final int getUserLimit() {
        return userLimit;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

    public static final void setUserLimit(final int newLimit) {
        userLimit = newLimit;
    }

    public static final int getNumberOfSessions() {
        return acceptor.getManagedSessions().size();
    }

    public static final boolean isAdminOnly() {
        return adminOnly;
    }

    public static final boolean isShutdown() {
        return finishedShutdown;
    }

    public static final void setOn() {
        finishedShutdown = false;
    }

    public static void forceRemoveClient(MapleClient client) {
        Collection<MapleClient> cls = getClientStorage().getAllClientsThreadSafe();
        for (MapleClient c : cls) {
            if (c == null) {
                continue;
            }
            if (c.getAccID() == client.getAccID() || c == client) {
                if (c != client) {
                    c.unLockDisconnect(false, true);
                }
                removeClient(c);
            }
        }
    }

    public static AccountStorage getClientStorage() {
        if (clients == null) {
            clients = new AccountStorage();
        }
        return clients;
    }

    public static final void addClient(final MapleClient c) {
        getClientStorage().registerAccount(c);
    }

    public static final void removeClient(final MapleClient c) {
        getClientStorage().deregisterAccount(c);
    }

    public static final void addLoginMac(MapleClient c) {
        if (!LoginMacs.containsKey(c.getAccID())) {
            LoginMacs.put(c.getAccID(), c.getLoginMacs());
        }
    }

    public static final String getLoginMac(final MapleClient c) {
        String macs = null;
        if (LoginMacs.containsKey(c.getAccID())) {
            macs = LoginMacs.get(c.getAccID());
        }
        return macs;
    }

    public static final String removeLoginMac(final MapleClient c) {
        String macs = null;
        if (LoginMacs.containsKey(c.getAccID())) {
            LoginMacs.remove(c.getAccID());
        }
        return macs;
    }
}
