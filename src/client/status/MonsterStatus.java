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
package client.status;

import client.MapleDisease;

public enum MonsterStatus {
//物攻
    WATK(0),
    //物防
    WDEF(1),
    //魔攻
    MATK(2),
    //魔防
    MDEF(3),
    //命中
    ACC(4),
    //迴避
    AVOID(5),
    //速度
    SPEED(6),
    //暈眩
    STUN(7),
    //結冰
    FREEZE(8),
    //中毒
    POISON(9),
    //封印、沉默
    SEAL(10),
    //黑暗
    DARKNESS(11),
    //物理攻擊提昇
    WEAPON_ATTACK_UP(12),
    //物理防禦提昇
    WEAPON_DEFENSE_UP(13),
    //魔法攻擊提昇
    MAGIC_ATTACK_UP(14),
    //魔法防禦提昇
    MAGIC_DEFENSE_UP(15),
    //死亡
    DOOM(16, 18),
    //影網
    SHADOW_WEB(17, 19),
    //物攻免疫
    WEAPON_IMMUNITY(18, 16),
    //魔攻免疫
    MAGIC_IMMUNITY(19, 17),
    //挑釁
    SHOWDOWN(20, 32),
    //免疫傷害
    DAMAGE_IMMUNITY(21, 20),
    //忍者伏擊
    NINJA_AMBUSH(22, 21),
    //
    DANAGED_ELEM_ATTR(23),
    //武器荼毒
    VENOMOUS_WEAPON(24, 22),
    //致盲
    BLIND(25, 23),
    //技能封印
    SEAL_SKILL(26, 24),
    //
    EMPTY(27, true, 33),
    //心靈控制
    HYPNOTIZE(28, 25),
    //反勝物攻
    WEAPON_DAMAGE_REFLECT(29, 26),
    //反射魔攻
    MAGIC_DAMAGE_REFLECT(30, 27),
    //
    SUMMON(31, 34),
    MBS_32(32, 28),
    NEUTRALISE(33, 29),
    IMPRINT(34, 30),
    MONSTER_BOMB(35, 31),
    MAGIC_CRASH(36);

    private final int i;
    private final int pos;
    private final boolean isDefault;
    private final int order; // 解包的順序

    private MonsterStatus(int i) {
        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.order = pos;
        this.isDefault = false;
    }

    private MonsterStatus(int i, int order) {
        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.order = order;
        this.isDefault = false;
    }

    private MonsterStatus(int i, boolean isDefault) {
        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.isDefault = isDefault;
        this.order = i;
    }

    private MonsterStatus(int i, boolean isDefault, int order) {
        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.isDefault = isDefault;
        this.order = order;
    }

    public int getPosition() {
        return pos;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public int getValue() {
        return i;
    }

    public int getOrder() {
        return order;
    }

    public static final MapleDisease getLinkedDisease(final MonsterStatus skill) {
        switch (skill) {
            case STUN:
            case SHADOW_WEB:
                return MapleDisease.STUN;
            case POISON:
            case VENOMOUS_WEAPON:
                //case BURN:
                return MapleDisease.POISON;
            case SEAL:
            case MAGIC_CRASH:
                return MapleDisease.SEAL;
            case FREEZE:
                return MapleDisease.FREEZE;
            case DARKNESS:
                return MapleDisease.DARKNESS;
            case SPEED:
                return MapleDisease.SLOW;
        }
        return null;
    }
}
