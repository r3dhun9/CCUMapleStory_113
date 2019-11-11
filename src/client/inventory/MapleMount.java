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
package client.inventory;

import client.MapleBuffStat;
import client.MapleCharacter;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.Serializable;

import database.DatabaseConnection;
import java.util.concurrent.ScheduledFuture;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer.MapTimer;
import tools.FilePrinter;
import tools.MaplePacketCreator;

public class MapleMount implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private final int skillid;
    private int itemid;
    private int exp;
    private byte fatigue, level;
    private transient boolean changed = false;
    private long lastFatigue = 0;
    private final transient WeakReference<MapleCharacter> owner;
    private ScheduledFuture<?> tirednessSchedule;

    public MapleMount(MapleCharacter owner, int id, int skillid, byte fatigue, byte level, int exp) {
        this.itemid = id;
        this.skillid = skillid;
        this.fatigue = fatigue;
        this.level = level;
        this.exp = exp;
        this.owner = new WeakReference<>(owner);
    }

    public void saveMount(final int charid, Connection con) {
        if (!changed) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE mountdata set `Level` = ?, `Exp` = ?, `Fatigue` = ? WHERE characterid = ?")) {
            ps.setByte(1, level);
            ps.setInt(2, exp);
            ps.setByte(3, fatigue);
            ps.setInt(4, charid);
            ps.executeUpdate();

        } catch (SQLException ex) {
            FilePrinter.printError("MapleMount.txt", ex, "saveMount");
        }
    }

    public int getItemId() {
        return itemid;
    }

    public int getSkillId() {
        return skillid;
    }

    public byte getFatigue() {
        return fatigue;
    }

    public int getExp() {
        return exp;
    }

    public byte getLevel() {
        return level;
    }

    public void setItemId(int c) {
        changed = true;
        this.itemid = c;
    }

    public void setFatigue(byte amount) {
        changed = true;
        fatigue += amount;
        if (fatigue < 0) {
            fatigue = 0;
        }
    }

    public void setExp(int c) {
        changed = true;
        this.exp = c;
    }

    public void setLevel(byte c) {
        changed = true;
        this.level = c;
    }

    public void increaseFatigue() {
        changed = true;
        this.fatigue++;
        if (owner.get() != null && owner.get().getMap() != null) {
            owner.get().getMap().broadcastMessage(MaplePacketCreator.updateMount(owner.get(), false));
        }
        if (fatigue > 99 && owner.get() != null) {
            this.fatigue = 95;
            owner.get().cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            owner.get().dropMessage(5, "由於騎寵疲憊了..所以自己先回去了。");
        }
        update();
    }

    public final boolean canTire(long now) {
        return lastFatigue > 0 && lastFatigue + 30000 < now;
    }

    public long getTiredness() {
        return lastFatigue;
    }

    public int i = 0;

    public void startSchedule() {
        this.changed = true;

        this.tirednessSchedule = MapTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                if (i != 0) {
                    increaseFatigue();
                    if (owner.get() != null) {
                        owner.get().dropMessage(5, "騎寵目前疲勞值:" + fatigue);
                    } else {
                        MapleMount.this.tirednessSchedule.cancel(false);
                    }
                }
                i++;
            }
        }, 5 * 60000);
    }

    public void cancelSchedule() {
        if (this.tirednessSchedule != null) {
            i = 0;
            this.tirednessSchedule.cancel(false);
        }
    }

    public void increaseExp() {
        int e;
        if (level >= 1 && level <= 7) {
            e = Randomizer.nextInt(10) + 15;
        } else if (level >= 8 && level <= 15) {
            e = Randomizer.nextInt(13) + 15 / 2;
        } else if (level >= 16 && level <= 24) {
            e = Randomizer.nextInt(23) + 18 / 2;
        } else {
            e = Randomizer.nextInt(28) + 25 / 2;
        }
        setExp(exp + e);
    }

    public void update() {
        final MapleCharacter chr = owner.get();
        if (chr != null) {
            chr.getMap().broadcastMessage(MaplePacketCreator.updateMount(chr, false));
        }
    }
}
