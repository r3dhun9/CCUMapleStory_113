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
package client;

import constants.GameConstants;
import constants.SkillType;
import java.util.ArrayList;
import java.util.List;

import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.life.Element;

public class Skill implements ISkill {

    /**
     * 技能名稱
     */
    private String name = "";
    /**
     * 技能效果
     */
    private final List<MapleStatEffect> effects = new ArrayList<>();
    /**
     * 技能元素
     */
    private Element element;
    /**
     * 技能等級
     */
    private byte level;
    /**
     * 技能id
     */
    private final int id;
    /**
     * 技能動畫時間
     */
    private int animationTime;
    /**
     * 前置技能
     */
    private int requiredSkill;
    /**
     * 最高等級
     */
    private int masterLevel;
    /** 
     * 有沒有action
     */
    private boolean action;
    private boolean invisible;
    private boolean chargeskill;
    private boolean timeLimited;

    public Skill(final int id) {
        super();
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public static final Skill loadFromData(final int id, final MapleData data) {
        Skill ret = new Skill(id);

        boolean isBuff = false;
        final int skillType = MapleDataTool.getInt("skillType", data, -1);
        final String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        } else {
            ret.element = Element.NEUTRAL;
        }
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        final MapleData effect = data.getChildByPath("effect");
        if (skillType != -1) {
            if (skillType == 2) {
                isBuff = true;
            }
        } else {
            final MapleData action_ = data.getChildByPath("action");
            final MapleData hit = data.getChildByPath("hit");
            final MapleData ball = data.getChildByPath("ball");

            boolean action = false;
            if (action_ == null) {
                if (data.getChildByPath("prepare/action") != null) {
                    action = true;
                } else {
                    switch (id) {
                        case 5201001:
                        case 5221009:
                        case 4221001:
                        case 4321001:
                        case 4321000:
                        case 4331001: //o_o
                        case 3101005: //or is this really hack
                            action = true;
                            break;
                    }
                }
            } else {
                action = true;
            }
            ret.action = action;
            isBuff = effect != null && hit == null && ball == null;
            isBuff |= action_ != null && MapleDataTool.getString("0", action_, "").equals("alert2");
            switch (id) {
                case SkillType.僧侶.群體治癒:
                case SkillType.火毒魔導士.致命毒霧:
                case SkillType.烈焰巫師3.火牢術屏障:
                case SkillType.火毒魔導士.末日烈焰:
                case SkillType.神偷.血魔轉換:
                case SkillType.火毒大魔導士.核爆術:
                case SkillType.冰雷大魔導士.核爆術:
                case SkillType.主教.核爆術:
                    isBuff = false;
                    break;
                case SkillType.冒險之技.怪物騎乘:
                case SkillType.貴族.怪物騎乘:
                case SkillType.傳說.怪物騎乘:
                case SkillType.十字軍.鬥氣集中:
                case SkillType.聖魂劍士3.鬥氣集中:
                case SkillType.烈焰巫師2.自然力重置:
                case SkillType.狂狼勇士2.強化連擊:
                case SkillType.神偷.勇者掠奪術:
                case SkillType.暗殺者.幸運術:
                case SkillType.閃雷悍將3.鬥神附體:
                case SkillType.格鬥家.鬥神附體:
                case SkillType.拳霸.鬥神降世:
                case SkillType.破風使者3.阿爾法:
                case SkillType.狂狼勇士1.矛之鬥氣:
                case SkillType.神槍手.章魚砲台:
                case SkillType.神槍手.海鷗突擊隊:
                case SkillType.槍神.砲台章魚王:
                case SkillType.海盜.衝鋒: 
                case SkillType.閃雷悍將1.衝鋒:
                case SkillType.神槍手.指定攻擊: 
                case SkillType.槍神.精準砲擊:
                case SkillType.格鬥家.蓄能激發:
                case SkillType.閃雷悍將2.蓄能激發:
                case SkillType.拳霸.最終極速:
                case SkillType.閃雷悍將3.最終極速:
                case SkillType.閃雷悍將2.雷鳴: 
                case SkillType.閃雷悍將3.閃光擊:
                case SkillType.黑騎士.黑暗守護:
                case SkillType.GM.終極隱藏:
                case SkillType.騎士.魔防消除:
                case SkillType.十字軍.防禦消除:
                case SkillType.龍騎士.力量消除:
                    isBuff = true;
                    break;
            }
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;

        for (final MapleData level : data.getChildByPath("level")) {
            ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, Byte.parseByte(level.getName())));
        }
        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill = Integer.parseInt(reqData.getName());
                ret.level = (byte) MapleDataTool.getInt(reqData, 1);
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    @Override
    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) { //incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    @Override
    public boolean hasAction() {
        return action;
    }

    @Override
    public boolean isChargeSkill() {
        return chargeskill;
    }

    @Override
    public boolean isInvisible() {
        return invisible;
    }

    @Override
    public boolean hasRequiredSkill() {
        return level > 0;
    }

    @Override
    public int getRequiredSkillLevel() {
        return level;
    }

    @Override
    public int getRequiredSkillId() {
        return requiredSkill;
    }

    @Override
    public byte getMaxLevel() {
        return (byte) effects.size();
    }

    @Override
    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = id / 10000;
        if (jid / 100 != skillForJob / 100) { // wrong job
            return false;
        } else if (jid / 1000 != skillForJob / 1000) { // wrong job
            return false;
        } else if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
            return false;
        } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if ((skillForJob / 10) % 10 > (jid / 10) % 10) { // wrong 2nd job
            return false;
        } else if (skillForJob % 10 > jid % 10) { // wrong 3rd/4th job
            return false;
        }
        return true;
    }

    @Override
    public boolean isTimeLimited() {
        return timeLimited;
    }

    @Override
    public boolean isFourthJob() {
        return ((id / 10000) % 10) == 2;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public int getAnimationTime() {
        return animationTime;
    }

    @Override
    public int getMasterLevel() {
        return masterLevel;
    }

    @Override
    public boolean isBeginnerSkill() {
        int jobId = id / 10000;
        return jobId == 0 || jobId == 1000 || jobId == 2000;
    }
}
