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
package handling.channel.handler;

import java.awt.Point;
import java.util.List;

import client.MapleClient;
import client.MapleCharacter;
import client.anticheat.CheatingOffense;
import client.inventory.MapleInventoryType;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.maps.MapleMap;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleNodes.MapleNodeInfo;
import server.movement.AbstractLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.packet.MobPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MobHandler {

    public static final void MoveMonster(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        final MapleCharacter chr = c.getPlayer();

        if (chr == null || chr.getMap() == null) {
            return;
        }

        final int objectId = slea.readInt();
        final MapleMonster monster = chr.getMap().getMonsterByOid(objectId);

        if (monster == null) {
            chr.addMoveMob(objectId);
            return;
        }

        final short moveid = slea.readShort();
        final boolean useSkill = slea.readBool();
        final byte mobSkillId = slea.readByte();
        final int bForcedStop = slea.readInt();

        int realskill = 0;
        int level = 0;

        if (useSkill) {

            final byte size = monster.getNoSkills();
            boolean used = false;

            if (size > 0) {
                final Pair<Integer, Integer> skillToUse = monster.getSkills().get((byte) Randomizer.nextInt(size));
                realskill = skillToUse.getLeft();
                level = skillToUse.getRight();
                // Skill ID and Level
                final MobSkill mobSkill = MobSkillFactory.getMobSkill(realskill, level);

                if (mobSkill != null && !mobSkill.checkCurrentBuff(chr, monster)) {
                    final long now = System.currentTimeMillis();
                    final long ls = monster.getLastSkillUsed(realskill);

                    if ((ls == 0L) || ((now - ls > mobSkill.getCoolTime()) && (!mobSkill.onlyOnce()))) {
                        monster.setLastSkillUsed(realskill, now, mobSkill.getCoolTime());

                        final int reqHp = (int) (((float) monster.getHp() / monster.getMobMaxHp()) * 100); // In case this monster have 2.1b and above HP
                        if (reqHp <= mobSkill.getHP()) {
                            used = true;
                            mobSkill.applyEffect(chr, monster, true);
                        }
                    }
                }
            }
            if (!used) {
                realskill = 0;
                level = 0;
            }
        }
        slea.read(1);
        slea.read(4);
        slea.read(4);
        slea.read(4);
        final Point startPos = slea.readPos();
        final List<LifeMovementFragment> res;
        try {
            res = MovementParse.parseMovement(slea, 2);
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

        int unk = slea.readByte();

        for (int i = 0;; i += 2) {
            if (i >= unk) {
                break;
            }
            slea.readByte();
        }

        slea.readShort();
        slea.readShort();
        slea.readShort();
        slea.readShort();

        final MapleCharacter controller = monster.getController();
        MapleMap map = chr.getMap();

        controller.getCheatTracker().checkMonsterMovment(monster, res, startPos);

        c.sendPacket(MobPacket.moveMonsterResponse(monster.getObjectId(), moveid, monster.getMp(), monster.isControllerHasAggro(), realskill, level));

        if (controller != c.getPlayer()) {
            if (monster.isAttackedBy(c.getPlayer())) {// aggro and controller change
                monster.switchController(c.getPlayer(), true);
            } else if (controller.getMapId() == monster.getMap().getId()) {
                monster.setController(null);
                return;
            }
        } else if (mobSkillId == -1 && monster.isControllerKnowsAboutAggro() && !monster.getStats().getMobile() && !monster.isFirstAttack()) {
            monster.setControllerHasAggro(false);
            monster.setControllerKnowsAboutAggro(false);
        }

        if (res != null) {
            MovementParse.updatePosition(res, monster, -1);
            map.moveMonster(monster, monster.getPosition());
            map.broadcastMessage(chr, MobPacket.moveMonster(useSkill, mobSkillId, bForcedStop, monster.getObjectId(), startPos, monster.getPosition(), res), monster.getPosition());
        }

    }

    public static final void handleFriendlyDamage(final SeekableLittleEndianAccessor slea, MapleClient c) {
        final MapleCharacter chr = c.getPlayer();
        final MapleMap map = chr.getMap();

        final MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
        slea.skip(4); // Player ID
        final MapleMonster mobto = map.getMonsterByOid(slea.readInt());

        if (mobfrom != null && mobto != null && mobto.getStats().isFriendly()) {
            final int damage = (mobto.getStats().getLevel() * Randomizer.nextInt(99)) / 2; // Temp for now until I figure out something more effective
            mobto.damage(chr, damage, true);
            checkShammos(chr, mobto, map);
        }
    }

    public static final void checkShammos(final MapleCharacter chr, final MapleMonster mobto, final MapleMap map) {
        if (!mobto.isAlive() && mobto.getId() == 9300275) { //shammos
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) { //check for 2022698
                if (chrz.getParty() != null && chrz.getParty().getLeader().getId() == chrz.getId()) {
                    //leader
                    if (chrz.haveItem(2022698)) {
                        MapleInventoryManipulator.removeById(chrz.getClient(), MapleInventoryType.USE, 2022698, 1, false, true);
                        mobto.heal((int) mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                        return;
                    }
                    break;
                }
            }
            map.broadcastMessage(MaplePacketCreator.getItemNotice("Your party has failed to protect the monster."));
            final MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(921120001);
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                chrz.changeMap(mapp, mapp.getPortal(0));
            }
        } else if (mobto.getId() == 9300275 && mobto.getEventInstance() != null) {
            mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
        }
    }

    public static final void handleMonsterBomb(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        final MapleCharacter chr = c.getPlayer();
        final MapleMonster monster = chr.getMap().getMonsterByOid(slea.readInt());

        if (monster == null || !chr.isAlive() || chr.isHidden()) {
            return;
        }
        final byte selfd = monster.getStats().getSelfD();
        if (selfd != -1) {
            chr.getMap().killMonster(monster, chr, false, false, selfd);
        }
    }

    public static final void handleAutoAggro(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        final MapleCharacter chr = c.getPlayer();
        final MapleMonster monster = chr.getMap().getMonsterByOid(slea.readInt());

        if (chr == null || chr.getMap() == null || chr.isHidden()) { //no evidence :)
            return;
        }

        if (monster != null && chr.getPosition().distanceSq(monster.getPosition()) < 200000) {
            if (monster.getController() != null) {
                if (chr.getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(chr, true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else {
                monster.switchController(chr, true);
            }
        }
    }

    public static final void HypnotizeDmg(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getPlayer();
        final MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt()); // From
        slea.skip(4); // Player ID
        final int to = slea.readInt(); // mobto
        slea.skip(1); // Same as player damage, -1 = bump, integer = skill ID
        final int damage = slea.readInt();
//	slea.skip(1); // Facing direction
//	slea.skip(4); // Some type of pos, damage display, I think

        final MapleMonster mob_to = chr.getMap().getMonsterByOid(to);

        if (mob_from != null && mob_to != null && mob_to.getStats().isFriendly()) { //temp for now
            if (damage > 30000) {
                return;
            }
            mob_to.damage(chr, damage, true);
            checkShammos(chr, mob_to, chr.getMap());
        }
    }

    public static final void handleDisplayNode(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getPlayer();
        final MapleMonster mobFrom = chr.getMap().getMonsterByOid(slea.readInt()); // From
        if (mobFrom != null) {
            chr.getClient().sendPacket(MaplePacketCreator.getNodeProperties(mobFrom, chr.getMap()));
        }
    }

    public static final void handleMobNode(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getPlayer();
        final MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt()); // From
        final int newNode = slea.readInt();
        final int nodeSize = chr.getMap().getNodes().size();
        if (mob_from != null && nodeSize > 0 && nodeSize >= newNode) {
            final MapleNodeInfo mni = chr.getMap().getNode(newNode);
            if (mni == null) {
                return;
            }
            if (mni.attr == 2) { //talk
                chr.getMap().talkMonster("Please escort me carefully.", 5120035, mob_from.getObjectId()); //temporary for now. itemID is located in WZ file
            }
            if (mob_from.getLastNode() >= newNode) {
                return;
            }
            mob_from.setLastNode(newNode);
            if (nodeSize == newNode) { //the last node on the map.
                int newMap = -1;
                switch (chr.getMapId() / 100) {
                    case 9211200:
                        newMap = 921120100;
                        break;
                    case 9211201:
                        newMap = 921120200;
                        break;
                    case 9211202:
                        newMap = 921120300;
                        break;
                    case 9211203:
                        newMap = 921120400;
                        break;
                    case 9211204:
                        chr.getMap().removeMonster(mob_from);
                        break;

                }
                if (newMap > 0) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.getErrorNotice("Proceed to the next stage."));
                    chr.getMap().removeMonster(mob_from);
                }
            }
        }
    }
}
