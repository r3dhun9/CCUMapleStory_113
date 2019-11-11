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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.Collections;
import java.util.Collection;
import client.MapleCharacterUtil;
import client.MapleCharacter;
import handling.MaplePacket;
import handling.world.CharacterTransfer;
import handling.world.CheaterData;
import handling.world.World;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Timer.PingTimer;

public class PlayerStorage {

    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final Lock readLock = mutex.readLock(), writeLock = mutex.writeLock();
    private final ReentrantReadWriteLock mutex2 = new ReentrantReadWriteLock();
    private final Lock readLock2 = mutex2.readLock(), writeLock2 = mutex2.writeLock();
    private final Map<Integer, MapleCharacter> idToChar = new HashMap<>();
    private final Map<Integer, CharacterTransfer> PendingCharacter = new HashMap<>();
    private final int channel;

    public PlayerStorage(int channel) {
        this.channel = channel;
        PingTimer.getInstance().schedule(new PersistingTask(), 900000);
    }

    public final Collection<MapleCharacter> getAllCharacters() {
        readLock.lock();
        try {
            return Collections.unmodifiableCollection(idToChar.values());
        } finally {
            readLock.unlock();
        }
    }

    public final List<MapleCharacter> getAllCharactersThreadSafe() {
        List<MapleCharacter> ret = new ArrayList<>();
        ret.addAll(getAllCharacters());
        return ret;
    }

    public final void registerPlayer(final MapleCharacter chr) {
        writeLock.lock();
        try {
            idToChar.put(chr.getId(), chr);
        } finally {
            writeLock.unlock();
        }
        World.Find.register(chr.getId(), chr.getName(), channel);
    }

    public final void registerPendingPlayer(final CharacterTransfer chr, final int playerid) {
        writeLock2.lock();
        try {
            PendingCharacter.put(playerid, chr);//new Pair(System.currentTimeMillis(), chr));
        } finally {
            writeLock2.unlock();
        }
    }

    public final void deregisterPlayer(final MapleCharacter chr) {
        writeLock.lock();
        try {
            if (chr.getMap() != null) {
                idToChar.get(chr.getId()).getMap().removePlayer(chr);
            }
            idToChar.remove(chr.getId());
        } finally {
            writeLock.unlock();
        }
        World.Find.forceDeregister(chr.getId(), chr.getName());
    }

    public final void deregisterPlayer(final int idz, final String namez) {
        writeLock.lock();
        try {
            idToChar.remove(idz);
        } finally {
            writeLock.unlock();
        }
        World.Find.forceDeregister(idz, namez);
    }

    public final void deregisterPendingPlayer(final int charid) {
        writeLock2.lock();
        try {
            PendingCharacter.remove(charid);
        } finally {
            writeLock2.unlock();
        }
    }

    public final CharacterTransfer getPendingCharacter(final int charid) {
        final CharacterTransfer toreturn;
        readLock2.lock();
        try {
            toreturn = PendingCharacter.get(charid);//.right;
        } finally {
            readLock2.unlock();
        }
        if (toreturn != null) {
            deregisterPendingPlayer(charid);
        }
        return toreturn;
    }

    public final MapleCharacter getCharacterByName(final String name) {
        MapleCharacter rchr = null;
        readLock.lock();
        try {
            for (MapleCharacter chr : idToChar.values()) {
                if (chr.getName().equalsIgnoreCase(name)) {
                    rchr = chr;
                }
            }
        } finally {
            readLock.unlock();
        }
        return rchr;
    }

    /*
     public MapleCharacter getPendingCharacter(int id) {
     for (MapleCharacter chr : pendingCharacter) {
     if (chr.getId() == id) {
     return chr;
     }
     }
     return null;
     }*/
    public final MapleCharacter getCharacterById(final int id) {
        readLock.lock();
        try {
            return idToChar.get(id);
        } finally {
            readLock.unlock();
        }
    }

    public final int getConnectedClients() {
        return idToChar.size();
    }

    public final List<CheaterData> getCheaters() {
        final List<CheaterData> cheaters = new ArrayList<>();

        readLock.lock();
        try {
            final Iterator<MapleCharacter> itr = this.idToChar.values().iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getCheatTracker().getPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), MapleCharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary()));
                }
            }
        } finally {
            readLock.unlock();
        }
        return cheaters;
    }

    public final void disconnectAll() {
        disconnectAll(false);
    }

    public final void disconnectAll(final boolean checkGM) {
        writeLock.lock();
        try {
            final Iterator<MapleCharacter> itr = idToChar.values().iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();
                if (!chr.isGM() || !checkGM) {
                    chr.getClient().disconnect(false, false, true);
                    World.Find.forceDeregister(chr.getId(), chr.getName());
                    itr.remove();
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public final String getOnlinePlayers(final boolean byGM) {
        final StringBuilder sb = new StringBuilder();

        if (byGM) {
            readLock.lock();
            try {
                final Iterator<MapleCharacter> itr = idToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(MapleCharacterUtil.makeMapleReadable(itr.next().getName()));
                    sb.append(", ");
                }
            } finally {
                readLock.unlock();
            }
        } else {
            readLock.lock();
            try {
                final Iterator<MapleCharacter> itr = idToChar.values().iterator();
                MapleCharacter chr;
                while (itr.hasNext()) {
                    chr = itr.next();

                    if (!chr.isGM()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
            } finally {
                readLock.unlock();
            }
        }
        return sb.toString();
    }

    public final void broadcastPacket(final MaplePacket data) {
        readLock.lock();
        try {
            final Iterator<MapleCharacter> itr = idToChar.values().iterator();
            while (itr.hasNext()) {
                itr.next().getClient().sendPacket(data);
            }
        } finally {
            readLock.unlock();
        }
    }

    public final void broadcastSmegaPacket(final MaplePacket data) {
        readLock.lock();
        try {
            final Iterator<MapleCharacter> itr = idToChar.values().iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getClient().isLoggedIn() && chr.getSmega()) {
                    chr.getClient().sendPacket(data);
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    public final void broadcastGMPacket(final MaplePacket data) {
        readLock.lock();
        try {
            final Iterator<MapleCharacter> itr = idToChar.values().iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getClient().isLoggedIn() && chr.isGM()) {
                    chr.getClient().sendPacket(data);
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    public class PersistingTask implements Runnable {

        @Override
        public void run() {
            writeLock2.lock();
            try {
                final long currenttime = System.currentTimeMillis();
                final Iterator<Map.Entry<Integer, CharacterTransfer>> itr = PendingCharacter.entrySet().iterator();

                while (itr.hasNext()) {
                    if (currenttime - itr.next().getValue().TranferTime > 40000) { // 40 sec
                        itr.remove();
                    }
                }
                PingTimer.getInstance().schedule(new PersistingTask(), 900000);
            } finally {
                writeLock2.unlock();
            }
        }
    }
}
