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

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Serializable;

import java.util.List;
import database.DatabaseConnection;
import server.MapleItemInformationProvider;
import server.movement.LifeMovementFragment;
import server.movement.StaticLifeMovement;
import tools.FilePrinter;

public class MaplePet implements Serializable {

    public static enum PetFlag {

        ITEM_PICKUP(0x01, 5190000, 5191000), //撿道具技能
        EXPAND_PICKUP(0x02, 5190002, 5191002), //擴大移動範圍技能
        AUTO_PICKUP(0x04, 5190003, 5191003), //範圍自動撿起功能
        UNPICKABLE(0x08, 5190005, -1), //勿撿特定道具技能
        LEFTOVER_PICKUP(0x10, 5190004, 5191004), //撿起無所有權道具&楓幣技能
        HP_CHARGE(0x20, 5190001, 5191001), //自動服用HP藥水技能
        MP_CHARGE(0x40, 5190006, -1), //自動服用MP藥水技能
        PET_BUFF(0x80, -1, -1), //idk
        PET_DRAW(0x100, 5190007, -1), //寵物召喚
        PET_DIALOGUE(0x200, 5190008, -1); //自言自語

        private final int i, item, remove;

        private PetFlag(int i, int item, int remove) {
            this.i = i;
            this.item = item;
            this.remove = remove;
        }

        public final int getValue() {
            return i;
        }

        public final boolean check(int flag) {
            return (flag & i) == i;
        }

        public static final PetFlag getByAddId(final int itemId) {
            for (PetFlag flag : PetFlag.values()) {
                if (flag.item == itemId) {
                    return flag;
                }
            }
            return null;
        }

        public static final PetFlag getByDelId(final int itemId) {
            for (PetFlag flag : PetFlag.values()) {
                if (flag.remove == itemId) {
                    return flag;
                }
            }
            return null;
        }
    }

    private static final long serialVersionUID = 9179541993413738569L;
    private String name;
    private final int uniqueid, petitemid;
    private int Fh = 0, stance = 0, limitedLife = 0;
    private Point pos;
    private byte fullness = 100, level = 1, summoned = 0;
    private short inventorypos = 0, closeness = 0, flags = 0;
    private boolean changed = false;

    private MaplePet(final int petitemid, final int uniqueid) {
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
    }

    private MaplePet(final int petitemid, final int uniqueid, final short inventorypos) {
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
        this.inventorypos = inventorypos;
    }

    public static final MaplePet loadFromDb(final int itemid, final int petid, final short inventorypos) {

        final MaplePet ret = new MaplePet(itemid, petid, inventorypos);

        Connection con = DatabaseConnection.getConnection(); // Get a connection to the database
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE petid = ?") // Get pet details..
                ) {
            ps.setInt(1, petid);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    return null;
                }

                ret.setName(rs.getString("name"));
                ret.setCloseness(rs.getShort("closeness"));
                ret.setLevel(rs.getByte("level"));
                ret.setFullness(rs.getByte("fullness"));
                ret.setLimitedLife(rs.getInt("seconds"));
                ret.setFlags(rs.getShort("flags"));
                ret.changed = false;
            }

            return ret;
        } catch (SQLException ex) {
            FilePrinter.printError("MaplePet.txt", ex, "loadFromDb");
            return null;
        }
    }

    public final void saveToDb(Connection conn) {
        if (!changed) {
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement("UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, seconds = ?, flags = ? WHERE petid = ?")) {
            ps.setString(1, name); // Set name
            ps.setByte(2, level); // Set Level
            ps.setShort(3, closeness); // Set Closeness
            ps.setByte(4, fullness); // Set Fullness
            ps.setInt(5, limitedLife);
            ps.setShort(6, flags);
            ps.setInt(7, uniqueid); // Set ID
            ps.executeUpdate(); // Execute statement

            changed = false;
        } catch (final SQLException ex) {
            FilePrinter.printError("MaplePet.txt", ex, "saveToDb");
        }
    }

    public final void saveToDb() {
        if (!changed) {
            return;
        }
        saveToDb(DatabaseConnection.getConnection());
    }

    public static final MaplePet createPet(final int itemid, final int uniqueid) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return createPet(itemid, ii.getName(itemid), 1, 0, 100, uniqueid, ii.getPetLimitLife(itemid), ii.getPetFlagInfo(itemid));
    }

    public static final MaplePet createPet(int itemid, String name, int level, int closeness, int fullness, int uniqueid, int limitedLife, short flag) {
        if (uniqueid <= -1) { //wah
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        try ( // Commit to db first
                PreparedStatement pse = DatabaseConnection.getConnection().prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, seconds, flags) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            pse.setInt(1, uniqueid);
            pse.setString(2, name);
            pse.setByte(3, (byte) level);
            pse.setShort(4, (short) closeness);
            pse.setByte(5, (byte) fullness);
            pse.setInt(6, limitedLife);
            pse.setShort(7, flag); //flags
            pse.executeUpdate();

        } catch (final SQLException ex) {
            FilePrinter.printError("MaplePet.txt", ex, "createPet");
            return null;
        }
        final MaplePet pet = new MaplePet(itemid, uniqueid);
        pet.setName(name);
        pet.setLevel(level);
        pet.setFullness(fullness);
        pet.setCloseness(closeness);
        pet.setFlags(flag);
        pet.setLimitedLife(limitedLife);

        return pet;
    }

    public final String getName() {
        if (name == null) {
            setName(MapleItemInformationProvider.getInstance().getName(petitemid));
        }
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
        this.changed = true;
    }

    public final boolean getSummoned() {
        return summoned > 0;
    }

    public final byte getSummonedValue() {
        return summoned;
    }

    public final void setSummoned(final int summoned) {
        this.summoned = (byte) summoned;
    }

    public final short getInventoryPosition() {
        return inventorypos;
    }

    public final void setInventoryPosition(final short inventorypos) {
        this.inventorypos = inventorypos;
    }

    public int getUniqueId() {
        return uniqueid;
    }

    public final short getCloseness() {
        return closeness;
    }

    public final void setCloseness(final int closeness) {
        this.closeness = (short) closeness;
        this.changed = true;
    }

    public final byte getLevel() {
        return level;
    }

    public final void setLevel(final int level) {
        this.level = (byte) level;
        this.changed = true;
    }

    public final byte getFullness() {
        return fullness;
    }

    public final void setFullness(final int fullness) {
        this.fullness = (byte) fullness;
        this.changed = true;
    }

    public final short getFlags() {
        return flags;
    }

    public final void setFlags(final int fffh) {
        this.flags = (short) fffh;
        this.changed = true;
    }

    public final int getFh() {
        return Fh;
    }

    public final void setFh(final int Fh) {
        this.Fh = Fh;
    }

    public final Point getPos() {
        return pos;
    }

    public final void setPos(final Point pos) {
        this.pos = pos;
    }

    public final int getStance() {
        return stance;
    }

    public final void setStance(final int stance) {
        this.stance = stance;
    }

    public final int getPetItemId() {
        return petitemid;
    }

    public final boolean canConsume(final int itemId) {
        final MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        for (final int petId : mii.petsCanConsume(itemId)) {
            if (petId == petitemid) {
                return true;
            }
        }
        return false;
    }

    public final void updatePosition(final List<LifeMovementFragment> movement) {
        for (final LifeMovementFragment move : movement) {
            if (move instanceof StaticLifeMovement) {
                Point newPos = move.getPosition();
                if (newPos != null) {
                    setPos(newPos);
                    setStance(((StaticLifeMovement) move).getNewstate());
                }
            }
        }
    }

    public final int getSecondsLeft() {
        return limitedLife;
    }

    public final void setLimitedLife(int sl) {
        this.limitedLife = sl;
        this.changed = true;
    }
}
