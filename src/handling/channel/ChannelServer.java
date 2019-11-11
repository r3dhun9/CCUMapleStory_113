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
package handling.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import handling.MapleServerHandler;
import handling.cashshop.CashShopServer;
import handling.login.LoginServer;
import handling.mina.MapleCodecFactory;
import handling.world.CheaterData;
import handling.world.World;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.MapleSquad.MapleSquadType;
import server.maps.MapleMapFactory;
import server.shops.HiredMerchant;
import tools.MaplePacketCreator;
import server.life.PlayerNPC;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import server.ServerProperties;
import server.events.MapleCoconut;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleFitness;
import server.events.MapleOla;
import server.events.MapleOxQuiz;
import server.events.MapleSnowball;
import server.events.MapleJewel;
import server.life.CustomNPC;
import server.maps.MapleMapObject;
import server.shops.MaplePlayerShop;
import tools.CollectionUtil;
import tools.ConcurrentEnumMap;

public class ChannelServer implements Serializable {

    public static long serverStartTime;
    private int expRate, mesoRate, dropRate, cashRate;
    private short port = 8585;
    private String ip = "127.0.0.1";
    private static String gatewayIP = "127.0.0.1";
    private static final short DEFAULT_PORT = 14000;
    private final int channel;
    private final String key;
    private int running_MerchantID = 0, flags = 0, running_PlayerShopID = 0;
    private String serverMessage, serverName;
    private boolean shutdown = false, finishedShutdown = false, MegaphoneMuteState = false, adminOnly = false;
    private PlayerStorage players;
    private MapleServerHandler serverHandler;
    private NioSocketAcceptor acceptor;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private static final Map<Integer, ChannelServer> instances = new HashMap<>();
    private final Map<MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap<>(MapleSquadType.class);
    private final Map<Integer, HiredMerchant> merchants = new HashMap<>();
    private final Map<Integer, MaplePlayerShop> playershops = new HashMap<>();
    private final Map<Integer, PlayerNPC> playerNPCs = new HashMap<>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private final ReentrantReadWriteLock squadLock = new ReentrantReadWriteLock(); //squad
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<>(MapleEventType.class);
    private final boolean debugMode = false;
    private boolean canUseGMItem;

    private ChannelServer(final String key, final int channel) {
        this.key = key;
        this.channel = channel;
        mapFactory = new MapleMapFactory();
        mapFactory.setChannel(channel);
    }

    public static Set<Integer> getAllChannels() {
        return new HashSet<>(instances.keySet());
    }

    public final void loadEvents() {
        if (!events.isEmpty()) {
            return;
        }
        events.put(MapleEventType.打瓶蓋, new MapleCoconut(channel, MapleEventType.打瓶蓋.mapids));
        events.put(MapleEventType.打果子, new MapleCoconut(channel, MapleEventType.打果子.mapids));
        events.put(MapleEventType.終極忍耐, new MapleFitness(channel, MapleEventType.終極忍耐.mapids));
        events.put(MapleEventType.爬繩子, new MapleOla(channel, MapleEventType.爬繩子.mapids));
        events.put(MapleEventType.是非題大考驗, new MapleOxQuiz(channel, MapleEventType.是非題大考驗.mapids));
        events.put(MapleEventType.滾雪球, new MapleSnowball(channel, MapleEventType.滾雪球.mapids));
        events.put(MapleEventType.尋寶, new MapleJewel(channel, MapleEventType.尋寶.mapids));
    }

    public final void setup() {
        setChannel(channel); //instances.put
        try {
            expRate = Integer.parseInt(ServerProperties.getProperty("server.settings.expRate", "1"));
            mesoRate = Integer.parseInt(ServerProperties.getProperty("server.settings.mesoRate", "1"));
            dropRate = Integer.parseInt(ServerProperties.getProperty("server.settings.dropRate", "1"));
            cashRate = Integer.parseInt(ServerProperties.getProperty("server.settings.cashRate", "1"));
            serverMessage = ServerProperties.getProperty("server.settings.serverMessage", "");
            serverName = ServerProperties.getProperty("server.settings.serverName", "");
            flags = Integer.parseInt(ServerProperties.getProperty("server.settings.wflags", "0"));
            adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.admin", "false"));
            canUseGMItem = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.gmitems", "false"));
            eventSM = new EventScriptManager(this, ServerProperties.getProperty("server.settings.events").split(","));
            port = Short.parseShort(ServerProperties.getProperty("server.settings.channel" + channel + ".port", "0"));
            if (port == 0) {
                port = (short) (channel - 1 + Short.parseShort(ServerProperties.getProperty("server.settings.channel1.port", String.valueOf(DEFAULT_PORT))));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ip = ServerProperties.getProperty("server.settings.ip.bind");
        gatewayIP = ServerProperties.getProperty("server.settings.ip.gateway");

        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());

        acceptor = new NioSocketAcceptor();

        this.serverHandler = new MapleServerHandler(channel, false);
        acceptor.setHandler(serverHandler);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 15);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        acceptor.getSessionConfig().setTcpNoDelay(true);
        players = new PlayerStorage(channel);
        loadEvents();
        try {
            acceptor.bind(new InetSocketAddress(port));
            System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】  - 監聽端口: " + port + "");
            eventSM.init();
        } catch (IOException e) {
            System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】 綁定端口: " + port + " 失敗 (頻道 " + getChannel() + ")" + e);
        }
    }

    public final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        broadcastPacket(MaplePacketCreator.serverNotice("【頻道" + String.valueOf(this.getChannel()) + "】 這個頻道正在關閉中."));
        shutdown = true;

        System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】 儲存角色資料...");

        getPlayerStorage().disconnectAll();

        System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】 解除端口綁定中...");

        try {
            if (acceptor != null) {
                Iterator<IoSession> iterator = acceptor.getManagedSessions().values().iterator();
                while (iterator.hasNext()) {
                    iterator.next().close(true);
                }
                acceptor.unbind(new InetSocketAddress(port));
                System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】 解除端口成功");
            }
        } catch (Exception e) {
            System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】 解除端口失敗");
        }

        instances.remove(channel);
        LoginServer.removeChannel(channel);
        setFinishShutdown();

    }

    public final boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public final MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public final void addPlayer(final MapleCharacter chr) {
        getPlayerStorage().registerPlayer(chr);
        chr.getClient().sendPacket(MaplePacketCreator.serverMessage(serverMessage));
    }

    public final PlayerStorage getPlayerStorage() {
        if (players == null) { //wth
            players = new PlayerStorage(channel); //wthhhh
        }
        return players;
    }

    public final void removePlayer(final MapleCharacter chr) {
        getPlayerStorage().deregisterPlayer(chr);

    }

    public final void removePlayer(final int idz, final String namez) {
        getPlayerStorage().deregisterPlayer(idz, namez);

    }

    public final String getServerMessage() {
        return serverMessage;
    }

    public final void setServerMessage(final String newMessage) {
        serverMessage = newMessage;
        broadcastPacket(MaplePacketCreator.serverMessage(serverMessage));
    }

    public final void broadcastPacket(final MaplePacket data) {
        getPlayerStorage().broadcastPacket(data);
    }

    public final void broadcastSmegaPacket(final MaplePacket data) {
        getPlayerStorage().broadcastSmegaPacket(data);
    }

    public final void broadcastGMPacket(final MaplePacket data) {
        getPlayerStorage().broadcastGMPacket(data);
    }

    public final int getExpRate() {
        return expRate;
    }

    public final void setExpRate(final int expRate) {
        this.expRate = expRate;
    }

    public final int getCashRate() {
        return cashRate;
    }

    public final void setCashRate(final int cashRate) {
        this.cashRate = cashRate;
    }

    public final int getMesoRate() {
        return mesoRate;
    }

    public final void setMesoRate(final int mesoRate) {
        this.mesoRate = mesoRate;
    }

    public final int getDropRate() {
        return dropRate;
    }

    public final void setDropRate(final int dropRate) {
        this.dropRate = dropRate;
    }

    public final String getIP() {
        return this.ip;
    }

    public final String getGatewayIP() {
        return gatewayIP;
    }

    public final int getChannel() {
        return channel;
    }

    public final void setChannel(final int channel) {
        instances.put(channel, this);
        LoginServer.addChannel(channel);
    }

    public static final Collection<ChannelServer> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public final boolean isShutdown() {
        return shutdown;
    }

    public final int getLoadedMaps() {
        return mapFactory.getLoadedMaps();
    }

    public final EventScriptManager getEventSM() {
        return eventSM;
    }

    public final void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, ServerProperties.getProperty("server.settings.events").split(","));
        eventSM.init();
    }

    public Map<MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(mapleSquads);
    }

    public final MapleSquad getMapleSquad(final String type) {
        return getMapleSquad(MapleSquadType.valueOf(type.toLowerCase()));
    }

    public final MapleSquad getMapleSquad(final MapleSquadType type) {
        return mapleSquads.get(type);
    }

    public final boolean addMapleSquad(final MapleSquad squad, final String type) {
        final MapleSquadType types = MapleSquadType.valueOf(type.toLowerCase());
        if (types != null && !mapleSquads.containsKey(types)) {
            mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }

    public final boolean removeMapleSquad(final MapleSquadType types) {
        if (types != null && mapleSquads.containsKey(types)) {
            mapleSquads.remove(types);
            return true;
        }
        return false;
    }

    public final int closeAllPlayerShop() {
        int ret = 0;
        merchLock.writeLock().lock();
        try {
            final Iterator<Map.Entry<Integer, MaplePlayerShop>> playershops_ = playershops.entrySet().iterator();
            while (playershops_.hasNext()) {
                MaplePlayerShop hm = playershops_.next().getValue();
                hm.closeShop(true, false);
                hm.getMap().removeMapObject(hm);
                playershops_.remove();
                ret++;
            }
        } finally {
            merchLock.writeLock().unlock();
        }
        return ret;
    }

    public final int closeAllMerchant() {
        int ret = 0;
        merchLock.writeLock().lock();
        try {
            final Iterator<Map.Entry<Integer, HiredMerchant>> merchants_ = merchants.entrySet().iterator();
            while (merchants_.hasNext()) {
                HiredMerchant hm = merchants_.next().getValue();
                hm.closeShop(true, false);
                hm.getMap().removeMapObject(hm);
                merchants_.remove();
                ret++;
            }
        } finally {
            merchLock.writeLock().unlock();
        }
        //hacky
        for (int i = 910000001; i <= 910000022; i++) {
            for (MapleMapObject mmo : mapFactory.getMap(i).getAllHiredMerchantsThreadsafe()) {
                ((HiredMerchant) mmo).closeShop(true, false);
                ret++;
            }
        }
        return ret;
    }

    public final int addPlayerShop(final MaplePlayerShop PlayerShop) {
        merchLock.writeLock().lock();
        int runningmer = 0;
        try {
            runningmer = running_PlayerShopID;
            playershops.put(running_PlayerShopID, PlayerShop);
            running_PlayerShopID++;
        } finally {
            merchLock.writeLock().unlock();
        }
        return runningmer;
    }

    public final int addMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        int runningmer = 0;
        try {
            runningmer = running_MerchantID;
            merchants.put(running_MerchantID, hMerchant);
            running_MerchantID++;
        } finally {
            merchLock.writeLock().unlock();
        }
        return runningmer;
    }

    public final void removeMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        try {
            merchants.remove(hMerchant.getStoreId());
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final boolean containsMerchant(final int accid) {
        boolean contains = false;

        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                if (((HiredMerchant) itr.next()).getOwnerAccId() == accid) {
                    contains = true;
                    break;
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return contains;
    }

    public final List<HiredMerchant> searchMerchant(final int itemSearch) {
        final List<HiredMerchant> list = new LinkedList<>();
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return list;
    }

    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public final boolean getMegaphoneMuteState() {
        return MegaphoneMuteState;
    }

    public int getEvent() {
        return eventmap;
    }

    public final void setEvent(final int ze) {
        this.eventmap = ze;
    }

    public MapleEvent getEvent(final MapleEventType t) {
        return events.get(t);
    }

    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return playerNPCs.values();
    }

    public final void removeCustomNPC(final CustomNPC cnpc) {
        getMapFactory().getMap(cnpc.getMapId()).removeNpc(cnpc.getId(), true);
    }

    public final void addCustomNPC(final CustomNPC cnpc) {
        getMapFactory().getMap(cnpc.getMapId()).spawnNpc(cnpc.getId(), cnpc.getPosition());
    }

    public final PlayerNPC getPlayerNPC(final int id) {
        return playerNPCs.get(id);
    }

    public final void addPlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.containsKey(npc.getId())) {
            removePlayerNPC(npc);
        }
        playerNPCs.put(npc.getId(), npc);
        getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public final void removePlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.containsKey(npc.getId())) {
            playerNPCs.remove(npc.getId());
            getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }

    public final String getServerName() {
        return serverName;
    }

    public final void setServerName(final String sn) {
        this.serverName = sn;
    }

    public final int getPort() {
        return port;
    }

    public final void setPrepareShutdown() {
        this.shutdown = true;
        System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】 準備關閉.");
    }

    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("【頻道" + String.valueOf(this.getChannel()) + "】 已經關閉完成.");
    }

    public final boolean isAdminOnly() {
        return adminOnly;
    }

    public final MapleServerHandler getServerHandler() {
        return serverHandler;
    }

    public final int getTempFlag() {
        return flags;
    }

    public static Map<Integer, Integer> getChannelLoad() {
        Map<Integer, Integer> ret = new HashMap<>();
        for (ChannelServer cs : instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public int getConnectedClients() {
        return getPlayerStorage().getConnectedClients();
    }

    public List<CheaterData> getCheaters() {
        List<CheaterData> cheaters = getPlayerStorage().getCheaters();

        Collections.sort(cheaters);
        return CollectionUtil.copyFirst(cheaters, 20);
    }

    public void broadcastMessage(byte[] message) {
        broadcastPacket(new ByteArrayMaplePacket(message));
    }

    public void broadcastSmega(byte[] message) {
        broadcastSmegaPacket(new ByteArrayMaplePacket(message));
    }

    public void broadcastGMMessage(byte[] message) {
        broadcastGMPacket(new ByteArrayMaplePacket(message));
    }

    public void giveCSPoints() {
        List<MapleCharacter> all = this.players.getAllCharactersThreadSafe();
        for (MapleCharacter chr : all) {
            if (chr.getClient() != null) {
                if (chr.getClient().getLoginState() == MapleClient.LOGIN_NOTLOGGEDIN) {
                    this.removePlayer(chr);
                } else {
                    chr.autoGiveCSPoints();
                }
            }
        }
    }

    public void saveAll() {
        int ppl = 0;
        List<MapleCharacter> all = this.players.getAllCharactersThreadSafe();
        for (MapleCharacter chr : all) {
            try {
                if (chr.getClient() != null) {
                    if (chr.getClient().getLoginState() == MapleClient.LOGIN_NOTLOGGEDIN) {
                        this.removePlayer(chr);
                    }
                }
                int res = chr.saveToDB(false, false);
                if (res == 1) {
                    ++ppl;
                } else {
                    System.out.println("[自動存檔] 角色:" + chr.getName() + " 儲存失敗.");
                }

            } catch (Exception e) {
            }
        }
    }

    public boolean canUseGMItem() {
        return canUseGMItem;
    }

    public final int getMerchantMap(MapleCharacter chr) {
        int ret = -1;
        for (int i = 910000001; i <= 910000022; i++) {
            for (MapleMapObject mmo : mapFactory.getMap(i).getAllHiredMerchantsThreadsafe()) {
                if (((HiredMerchant) mmo).getOwnerId() == chr.getId()) {
                    return mapFactory.getMap(i).getId();
                }
            }
        }
        return ret;
    }

    public final static int getChannelCount() {
        return instances.size();
    }

    public static void forceRemovePlayerByAccId(MapleClient client, int accid) {

        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            List<MapleCharacter> chars = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (MapleCharacter c : chars) {
                if (c.getAccountID() == accid) {
                    c.getClient().unLockDisconnect(true, false);
                    return;
                }
            }
        }

        Collection<MapleCharacter> chrs = CashShopServer.getPlayerStorage().getAllCharactersThreadSafe();
        for (MapleCharacter c : chrs) {
            if (c.getAccountID() == accid) {
                c.getClient().unLockDisconnect(true, true);
                return;
            }
        }
    }

    public static final Set<Integer> getChannels() {
        return new HashSet<>(instances.keySet());
    }

    public static final ChannelServer newInstance(final String key, final int channel) {
        return new ChannelServer(key, channel);
    }

    public static final ChannelServer getInstance(final int channel) {
        return instances.get(channel);
    }

    public static final void startAllChannels() {
        serverStartTime = System.currentTimeMillis();

        int count = Integer.parseInt(ServerProperties.getProperty("server.settings.channel.count", "0"));
        for (int i = 0; i < count; i++) {
            newInstance(ServerConstants.Channel_Key[i], i + 1).setup();
        }
    }

    public static final void startChannel(final int channel) {
        serverStartTime = System.currentTimeMillis();
        for (int i = 0; i < Integer.parseInt(ServerProperties.getProperty("server.settings.channel.count", "0")); i++) {
            if (channel == i + 1) {
                newInstance(ServerConstants.Channel_Key[i], i + 1).setup();
                break;
            }
        }
    }
}
