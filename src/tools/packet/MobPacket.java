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
package tools.packet;

import java.util.Map;
import java.util.List;
import java.awt.Point;

import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.movement.LifeMovementFragment;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;

public class MobPacket {

    public static MaplePacket damageMonster(final int oid, final long damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        if (damage > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) damage);
        }

        return mplew.getPacket();
    }

    public static MaplePacket damageFriendlyMob(final MapleMonster mob, final long damage, final boolean display) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(mob.getObjectId());
        mplew.write(display ? 1 : 2); //false for when shammos changes map!
        if (damage > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) damage);
        }
        if (mob.getHp() > Integer.MAX_VALUE) {
            mplew.writeInt((int) (((double) mob.getHp() / mob.getMobMaxHp()) * Integer.MAX_VALUE));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }

        return mplew.getPacket();
    }

    public static MaplePacket killMonster(final int oid, final int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(animation); // 0 = dissapear, 1 = fade out, 2+ = special

        return mplew.getPacket();
    }

    public static MaplePacket healMonster(final int oid, final int heal) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);

        return mplew.getPacket();
    }

    public static MaplePacket showMonsterHP(int oid, int remhppercentage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
        mplew.writeInt(oid);
        mplew.write(remhppercentage);

        return mplew.getPacket();
    }

    public static MaplePacket showBossHP(final MapleMonster mob) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(mob.getId());
        if (mob.getHp() > Integer.MAX_VALUE) {
            mplew.writeInt((int) (((double) mob.getHp() / mob.getMobMaxHp()) * Integer.MAX_VALUE));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }
        mplew.write(mob.getStats().getTagColor());
        mplew.write(mob.getStats().getTagBgColor());

        return mplew.getPacket();
    }

    public static MaplePacket showBossHP(final int monsterId, final long currentHp, final long maxHp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(monsterId); //has no image
        if (currentHp > Integer.MAX_VALUE) {
            mplew.writeInt((int) (((double) currentHp / maxHp) * Integer.MAX_VALUE));
        } else {
            mplew.writeInt((int) (currentHp <= 0 ? -1 : currentHp));
        }
        if (maxHp > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) maxHp);
        }
        mplew.write(6);
        mplew.write(5);

        //colour legend: (applies to both colours)
        //1 = red, 2 = dark blue, 3 = light green, 4 = dark green, 5 = black, 6 = light blue, 7 = purple
        return mplew.getPacket();
    }

    public static MaplePacket moveMonster(boolean useskill, int skill, int bForcedStop, int oid, Point startPos, Point endPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0); //moveid but always 0
        mplew.write(useskill ? 1 : 0); //?? I THINK
        mplew.write(skill);
        mplew.writeInt(bForcedStop);
        mplew.writePos(startPos);
        serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    private static void serializeMovementList(LittleEndianWriter lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static MaplePacket spawnMonster(MapleMonster life, int spawnType, int effect, int link) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1); // 1 = Control normal, 5 = Control none
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(0); // FH
        mplew.writeShort(life.getFh()); // Origin FH

        if (effect != 0 || link != 0) {
            mplew.write(effect != 0 ? effect : -3);
            mplew.writeInt(link);
        } else {
            if (spawnType == 0) {
                mplew.writeInt(effect);
            }
            mplew.write(spawnType); // newSpawn ? -2 : -1
            //0xFB when wh spawns
        }
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(0); //v102 - another int here

        return mplew.getPacket();
    }

    private static void writeMaskFromList(MaplePacketLittleEndianWriter mplew, Collection<MonsterStatusEffect> ss) {
        int[] mask = new int[4];
        for (MonsterStatusEffect statup : ss) {
            mask[(statup.getStatus().getPosition())] |= statup.getStatus().getValue();
        }
        for (int i = 0; i < mask.length; i++) {
            mplew.writeInt(mask[(i)]);
        }
    }

    public static void addMonsterStatus(MaplePacketLittleEndianWriter mplew, MapleMonster life) {

        if (life.getStati().size() <= 0) {
            life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
        }
        LinkedList<MonsterStatusEffect> buffs = new LinkedList<>(life.getStati().values());
        EncodeTemporary(mplew, buffs);
        //wh spawn - 15 zeroes instead of 16, then 98 F4 56 A6 C7 C9 01 28, then 7 zeroes
    }

    public static MaplePacket controlMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(aggro ? 2 : 1);
        mplew.writeInt(life.getObjectId());
        mplew.write(1); // 1 = Control normal, 5 = Control none
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance()); // Bitfield
        mplew.writeShort(life.getFh()); // FH
        mplew.writeShort(life.getFh()); // Origin FH
        mplew.write(life.isFake() ? -4 : newSpawn ? -2 : -1);
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(0);
        if (life.getId() / 10000 == 961) {
            mplew.writeAsciiString("");
        }
        return mplew.getPacket();
    }

    public static MaplePacket stopControllingMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static MaplePacket makeMonsterInvisible(MapleMonster life) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(life.getObjectId());

        return mplew.getPacket();
    }

    public static MaplePacket makeMonsterReal(MapleMonster life) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1); // 1 = Control normal, 5 = Control none
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(0); // FH
        mplew.writeShort(life.getFh()); // Origin FH
        mplew.writeShort(-1);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());
        mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeShort(currentMp);
        mplew.write(skillId);
        mplew.write(skillLevel);

        return mplew.getPacket();
    }

    public static void SingleProcessStatSet(MaplePacketLittleEndianWriter mplew, MonsterStatusEffect buff) {
        List<MonsterStatusEffect> ss = new LinkedList<>();
        ss.add(buff);
        ProcessStatSet(mplew, ss);
    }

    public static void EncodeTemporary(MaplePacketLittleEndianWriter mplew, List<MonsterStatusEffect> buffs) {
        Set<MonsterStatus> mobstat = new HashSet();
        writeMaskFromList(mplew, buffs);
        Collections.sort(buffs, new Comparator<MonsterStatusEffect>() {
            @Override
            public int compare(final MonsterStatusEffect o1, final MonsterStatusEffect o2) {
                int val1 = o1.getStatus().getOrder();
                int val2 = o2.getStatus().getOrder();
                return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
            }
        });
        Collection<MonsterStatus> buffstatus = new LinkedList<>();
        for (MonsterStatusEffect buff : buffs) {
            buffstatus.add(buff.getStatus());
            if (buff.getStatus() == MonsterStatus.DANAGED_ELEM_ATTR) {
                continue;
            }
            if (buff.getStatus() == MonsterStatus.EMPTY) {
                int result = 0;
                mplew.writeInt(result);
                for (int i = 0; i < result; ++i) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                }
                continue;
            }
            mplew.writeShort(buff.getX());
            if (buff.getMobSkill() != null) {
                mplew.writeShort(buff.getMobSkill().getSkillId());
                mplew.writeShort(buff.getMobSkill().getSkillLevel());
            } else if (buff.getSkill() > 0) {
                mplew.writeInt(buff.getSkill());
            }
            mplew.writeShort(buff.getStatus().isDefault() ? 0 : 1);
        }
        if (buffstatus.contains(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
            mplew.writeInt(0);
        }
        if (buffstatus.contains(MonsterStatus.MAGIC_DAMAGE_REFLECT)) {
            mplew.writeInt(0);
        }
        if (buffstatus.contains(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
            mplew.writeInt(0);
        }
        if (buffstatus.contains(MonsterStatus.SUMMON)) {
            mplew.write(0);
            mplew.write(0);
        }

    }

    public static void ProcessStatSet(MaplePacketLittleEndianWriter mplew, List<MonsterStatusEffect> buffs) {
        EncodeTemporary(mplew, buffs);
        mplew.writeShort(2);
        mplew.write(1);
        mplew.write(1);
    }

    public static MaplePacket applyMonsterStatus(MapleMonster mons, MonsterStatusEffect ms) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(mons.getObjectId());
        /*writeMaskFromList(mplew, Collections.singletonList(ms));
         mplew.writeShort(ms.getX());
         mplew.writeShort(ms.getMobSkill().getSkillId());
         mplew.writeShort(ms.getMobSkill().getSkillLevel());
         mplew.writeShort(ms.getStati().isEmpty() ? 1 : 0); // might actually be the buffTime but it's not displayed anywhere
         mplew.writeShort(0); // delay in ms
         mplew.write(1); // size
         //        mplew.write(1); // ? v97*/
        SingleProcessStatSet(mplew, ms);

        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatus(MapleMonster mons, List<MonsterStatusEffect> mse) {
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return MaplePacketCreator.enableActions();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(mons.getObjectId());
        ProcessStatSet(mplew, mse);
//        System.out.println("applyMonsterStatus 2");

        return mplew.getPacket();
    }

    /* public static MaplePacket applyMonsterStatus(final int oid, final Map<MonsterStatus, Integer> stati, final List<Integer> reflection, MobSkill skil) {
     MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

     mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
     mplew.writeInt(oid);
     writeMaskFromList(mplew, stati);

     for (Map.Entry<MonsterStatus, Integer> mse : stati.entrySet()) {
     mplew.writeShort(mse.getValue());
     mplew.writeShort(skil.getSkillId());
     mplew.writeShort(skil.getSkillLevel());
     mplew.writeShort(mse.getKey().isEmpty() ? 1 : 0); // might actually be the buffTime but it's not displayed anywhere
     }
     mplew.writeShort(0);
     mplew.writeInt(0);
     /*for (Integer ref : reflection) {
     mplew.writeInt(ref);
     }
     mplew.writeInt(0);
     mplew.writeShort(0); // delay in ms

     int size = stati.size(); // size
     if (reflection.size() > 0) {
     size /= 2; // This gives 2 buffs per reflection but it's really one buff
     }
     mplew.write(size); // size
     //        mplew.write(1); // ? v97

     return mplew.getPacket();
     }*/
    public static MaplePacket cancelMonsterStatus(MapleMonster mons, MonsterStatusEffect ms) {
        List<MonsterStatusEffect> mse = new ArrayList<>();
        mse.add(ms);
        return cancelMonsterStatus(mons, mse);
    }

    public static MaplePacket cancelMonsterStatus(MapleMonster mons, List<MonsterStatusEffect> mse) {

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(mons.getObjectId());
        writeMaskFromList(mplew, mse);
        boolean cond = false;
        if (cond) {
            int v6 = 0;
            mplew.writeInt(v6);
            for (int i = 0; i < v6; i++) {
                mplew.writeInt(0);
            }
        }
        mplew.write(2);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket talkMonster(int oid, int itemId, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(500); //?
        mplew.writeInt(itemId);
        mplew.write(itemId <= 0 ? 0 : 1);
        mplew.write(msg == null || msg.length() <= 0 ? 0 : 1);
        if (msg != null && msg.length() > 0) {
            mplew.writeMapleAsciiString(msg);
        }
        mplew.writeInt(1); //?

        return mplew.getPacket();
    }

    public static MaplePacket removeTalkMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        return mplew.getPacket();
    }
}
