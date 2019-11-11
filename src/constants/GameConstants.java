package constants;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatus;
import handling.channel.handler.AttackInfo;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import server.MapleStatEffect;
import server.Randomizer;
import server.maps.MapleMapObjectType;

public class GameConstants {

    public static boolean isLinkedAttackSkill(final int id) {
        return getLinkedAttackSkill(id) != id;
    }

    public static int getLinkedAttackSkill(final int id) {
        switch (id) {
            case 11101220: // 皇家衝擊
                return 11101120; // 潛行突襲
            case 11101221: // 焚影
                return 11101121; // 殘像追擊
            case 11111120: // 月影
                return 11111220; // 光芒四射
            case 11111121: // 月光十字架
                return 11111221; // 日光十字架
            case 11121201: // 疾速黃昏
            case 11121102: // 月光之舞（空中）
            case 11121202: // 疾速黃昏（空中
                return 11121101; // 月光之舞
            case 11121103: // 新月分裂
                return 11121203; // 太陽穿刺
            case 21110007:
            case 21110008:
                return 21110002;
            case 21120009:
            case 21120010:
                return 21120002;
            case 5211015:
            case 5211016:
                return 5211011;
            case 5001008:
                return 5200010;
            case 5001009:
                return 5101004;
        }
        return id;
    }
    public static final List<MapleMapObjectType> rangedMapobjectTypes = Collections.unmodifiableList(Arrays.asList(
            MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER,
            MapleMapObjectType.DOOR,
            MapleMapObjectType.REACTOR,
            MapleMapObjectType.SUMMON,
            MapleMapObjectType.NPC,
            MapleMapObjectType.MIST));
    private static final int[] ExpTable = {0, 15, 34, 57, 92, 135, 372, 560, 840, 1242, 1716,
        2360, 3216, 4200, 5460, 7050, 8840, 11040, 13716, 16680, 20216,
        24402, 28980, 34320, 40512, 47216, 54900, 63666, 73080, 83720, 95700,
        108480, 122760, 138666, 155540, 174216, 194832, 216600, 240500, 266682, 294216,
        324240, 356916, 391160, 428280, 468450, 510420, 555680, 604416, 655200, 709716, // 51等到這
        748608, 789631, 832902, 878545, 926689, 977471, 1031036, 1087536, 1147132, 1209994,
        1276301, 1346242, 1420016, 1497832, 1579913, 1666492, 1757815, 1854143, 1955750, 2062925, // 71等到這
        2175973, 2295216, 2410993, 2553663, 2693603, 2841212, 2996910, 3161140, 3334370, 3517093,
        3709829, 3913127, 4127566, 4353756, 4592341, 4844001, 5109452, 5389449, 5684790, 5996316,
        6324914, 6671519, 7037118, 7422752, 7829518, 8258575, 8711144, 9188514, 9692044, 10223168, // 101等到這
        10783397, 11374327, 11997640, 12655110, 13348610, 14080113, 14851703, 15665576, 16524049, 17429566,
        18384706, 19392187, 20454878, 21575805, 22758159, 24005306, 25320796, 26708375, 28171993, 29715818,//121等到這
        31344244, 33061908, 34873700, 36784778, 38800583, 40926854, 43169645, 45535341, 48030677, 50662758,//131等到這
        53439077, 56367538, 59456479, 62714694, 66151459, 69776558, 73600313, 77633610, 81887931, 86375389,//141等到這
        91108760, 96101520, 101367883, 106922842, 112782213, 118962678, 125481832, 132358236, 139611467, 147262175,//151等到這
        155332142, 163844343, 172823012, 182293713, 192283408, 202820538, 213935103, 225658746, 238024845, 251068606, //160
        264827165, 279339693, 294647508, 310794191, 327825712, 345790561, 364739883, 384727628, 405810702, 428049128, //170
        451506220, 476248760, 502347192, 529875818, 558913012, 589541445, 621848316, 655925603, 691870326, 729784819,
        769777027, 811960808, 856456260, 903390063, 952895838, 1005114529, 1060194805, 1118293480, 1179575962, 1244216724,
        1312399800, 1384319309, 1460180007, 1540197871, 1624600714, 1713628833, 1807535693, 1906588648, 2011069705, 2121276324};
    private static final int[] ClosenessTable = {0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793,
        3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074,
        30000};
    private static final int[] MountExpTable = {0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247,
        2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550};

    public static final int[] itemBlock = {2340000, 2049100, 4001129, 2040037, 2040006, 2040007, 2040303, 2040403, 2040506, 2040507, 2040603, 2040709, 2040710, 2040711, 2040806, 2040903, 2041024, 2041025, 2043003, 2043103, 2043203, 2043303, 2043703, 2043803, 2044003, 2044103, 2044203, 2044303, 2044403, 2044503, 2044603, 2044908, 2044815, 2044019, 2044703, 1004001, 4007008, 1004002, 5152053, 5150040};
    public static final int[] cashBlock = {5222000, 5500001, 5500002, 5600001, 5252000, 5350003, 5401000, 5490000, 5490001, 5500000, 5252001, 5252003, 5220001, 5220002, 5200000, 5200001, 5200002, 5320000, 5440000, 5201001, 5201002};

    public static final int OMOK_SCORE = 122200;
    public static final int MATCH_SCORE = 122210;
    public static final int[] blockedSkills = {4341003};
    public static final String[] RESERVED = {"Rental"};
    public static int 商店一次拍賣獲得最大楓幣 = 1500000;

    public static int getExpNeededForLevel(final int level) {
        if (level < 0 || level >= ExpTable.length) {
            return Integer.MAX_VALUE;
        }
        return ExpTable[level];
    }

    public static boolean isNoDelaySkill(int skillId) {
        return skillId == SkillType.格鬥家.蓄能激發
                || skillId == SkillType.狂狼勇士2.強化連擊
                || skillId == SkillType.閃雷悍將2.蓄能激發
                || skillId == 2111007 || skillId == 2211007 || skillId == 2311007 || skillId == 32121003 || skillId == 35121005 || skillId == 35111004 || skillId == 35121013 || skillId == 35121003 || skillId == 22150004 || skillId == 22181004 || skillId == 11101002 || skillId == 51100002 || skillId == 13101002 || skillId == 24121000 || skillId == 112001008 || skillId == 22161005 || skillId == 22161005;
    }

    public static boolean exitem(int itemid) {
        switch (itemid) {
            case 2070015:
                return true;
        }
        return false;
    }

    public static boolean Novice_Skill(int skill) {
        switch (skill) {
            case 1000://嫩寶丟擲術
            case 10001000:
            case 20001000:
                return true;
        }
        return false;
    }

    public static int getClosenessNeededForLevel(final int level) {
        return ClosenessTable[level - 1];
    }

    public static int getMountExpNeededForLevel(final int level) {
        return MountExpTable[level - 1];
    }

    public static int getBookLevel(final int level) {
        return (int) ((5 * level) * (level + 1));
    }

    public static int getTimelessRequiredEXP(final int level) {
        return 70 + (level * 10);
    }

    public static int getReverseRequiredEXP(final int level) {
        return 60 + (level * 5);
    }

    public static int maxViewRangeSq() {
        return 800000; // 800 * 800
    }

    public static boolean isJobFamily(final int baseJob, final int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }

    public static boolean isAdv(final int job) {
        return job >= 0 && job < 1000;
    }

    public static boolean isKOC(final int job) {
        return job >= 1000 && job < 2000;
    }

    public static boolean isAran(final int job) {
        return job >= 2000 && job <= 2112 && job != 2001;
    }

    public static boolean isAdventurer(final int job) {
        return job >= 0 && job < 1000;
    }

    public static boolean isRecoveryIncSkill(final int id) {
        switch (id) {
            case SkillType.十字軍.魔力恢復:
            case SkillType.法師.魔力淨化:
            case SkillType.騎士.魔力恢復:
            case SkillType.聖魂劍士3.魔力恢復:
            case SkillType.刺客.恢復術:
            case SkillType.俠盜.恢復術:
                return true;
        }
        return false;
    }

    public static boolean isLinkedAranSkill(final int id) {
        return getLinkedAranSkill(id) != id;
    }

    public static int getLinkedAranSkill(final int id) {
        switch (id) {
            case 21110007:
            case 21110008:
                return 21110002;
            case 21120009:
            case 21120010:
                return 21120002;
            case 4321001:
                return 4321000;
            case 33101006:
            case 33101007:
                return 33101005;
            case 33101008:
                return 33101004;
            case 35101009:
            case 35101010:
                return 35100008;
            case 35111009:
            case 35111010:
                return 35111001;
        }
        return id;
    }

    public static int getBofForJob(final int job) {
        if (isAdventurer(job)) {
            return 12;
        } else if (isKOC(job)) {
            return 10000012;
        }
        return 20000012;
    }

    public static boolean isElementAmpSkill(final int skill) {
        switch (skill) {
            case 2110001:
            case 2210001:
            case 12110001:
            case 22150000:
                return true;
        }
        return false;
    }

    public static int getMPEaterForJob(final int job) {
        switch (job) {
            case 210:
            case 211:
            case 212:
                return 2100000;
            case 220:
            case 221:
            case 222:
                return 2200000;
            case 230:
            case 231:
            case 232:
                return 2300000;
        }
        return 2100000; // Default, in case GM
    }

    public static int getJobShortValue(int job) {
        if (job >= 1000) {
            job -= (job / 1000) * 1000;
        }
        job /= 100;
        if (job == 4) { // For some reason dagger/ claw is 8.. IDK
            job *= 2;
        } else if (job == 3) {
            job += 1;
        } else if (job == 5) {
            job += 11; // 16
        }
        return job;
    }

    public static boolean isPyramidSkill(final int skill) {
        switch (skill) {
            case 1020:
            case 10001020:
            case 20001020:
                return true;
        }
        return false;
    }

    public static boolean 武陵道場技能(final int skill) {
        switch (skill) {
            case 1009:
            case 1010:
            case 1011:
            case 10001009:
            case 10001010:
            case 10001011:
            case 20001009:
            case 20001010:
            case 20001011:
            case 20011009:
            case 20011010:
            case 20011011:
            case 30001009:
            case 30001010:
            case 30001011:
                return true;
        }
        return false;
    }

    public static boolean 飛鏢(final int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean 子彈(final int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean 可充值道具(final int itemId) {
        return 飛鏢(itemId) || 子彈(itemId);
    }

    public static boolean 套服(final int itemId) {
        return itemId / 10000 == 105;
    }

    public static boolean 寵物(final int itemId) {
        return itemId / 10000 == 500;
    }

    public static boolean 弩弓箭(final int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }

    public static boolean 弓箭(final int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }

    public static boolean 魔法武器(final int itemId) {
        final int s = itemId / 10000;
        return s == 137 || s == 138;
    }

    public static boolean 武器(final int itemId) {
        return itemId >= 1300000 && itemId < 1500000;
    }

    public static MapleInventoryType getInventoryType(final IItem item) {
        MapleInventoryType type = getInventoryType(item.getItemId());
        if (type == MapleInventoryType.EQUIP && item.getPosition() < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        return type;
    }

    public static MapleInventoryType getInventoryType(final int itemId) {
        MapleInventoryType type = MapleInventoryType.getByType((byte) (itemId / 1000000));
        if (type == MapleInventoryType.UNDEFINED || type == null) {
            final byte type2 = (byte) (itemId / 10000);
            switch (type2) {
                case 2:
                    type = MapleInventoryType.FACE;
                    break;
                case 3:
                case 4:
                    type = MapleInventoryType.HAIR;
                    break;
                default:
                    type = MapleInventoryType.UNDEFINED;
                    break;
            }
        }
        return type;
    }

    public static MapleWeaponType 武器種類(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) {
            case 30:
                return MapleWeaponType.單手劍;
            case 31:
                return MapleWeaponType.單手斧;
            case 32:
                return MapleWeaponType.單手棍;
            case 33:
                return MapleWeaponType.短劍;
            case 34:
                return MapleWeaponType.雙刀;
            case 37:
                return MapleWeaponType.長杖;
            case 38:
                return MapleWeaponType.短杖;
            case 40:
                return MapleWeaponType.雙手劍;
            case 41:
                return MapleWeaponType.雙手斧;
            case 42:
                return MapleWeaponType.雙手棍;
            case 43:
                return MapleWeaponType.矛;
            case 44:
                return MapleWeaponType.槍;
            case 45:
                return MapleWeaponType.弓;
            case 46:
                return MapleWeaponType.弩;
            case 47:
                return MapleWeaponType.拳套;
            case 48:
                return MapleWeaponType.指虎;
            case 49:
                return MapleWeaponType.火槍;
        }
        return MapleWeaponType.沒有武器;
    }

    public static boolean 盾(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        return cat == 9;
    }

    public static boolean 裝備(final int itemId) {
        return itemId / 1000000 == 1;
    }

    public static boolean isCleanSlate(int itemId) {
        return itemId / 100 == 20490;
    }

    public static boolean isAccessoryScroll(int itemId) {
        return itemId / 100 == 20492;
    }

    public static boolean isChaosScroll(int itemId) {
        if (itemId >= 2049105 && itemId <= 2049110) {
            return false;
        }
        return itemId / 100 == 20491;
    }

    public static int getChaosNumber(int itemId) {
        return itemId == 2049116 ? 10 : 5;
    }

    public static boolean isEquipScroll(int scrollId) {
        return scrollId / 100 == 20493;
    }

    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20494;
    }

    public static boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2040727: // Spikes on show
            case 2041058: // Cape for Cold protection
                return true;
        }
        return false;
    }

    public static boolean 雙手武器(final int itemId) {
        switch (武器種類(itemId)) {
            case 雙手斧:
            case 火槍:
            case 指虎:
            case 雙手棍:
            case 弓:
            case 拳套:
            case 弩:
            case 槍:
            case 矛:
            case 雙手劍:
                return true;
            default:
                return false;
        }
    }

    public static boolean 回村卷軸(final int id) {
        return id >= 2030000 && id < 2040000;
    }

    public static boolean isUpgradeScroll(final int id) {
        return id >= 2040000 && id < 2050000;
    }

    public static boolean 火槍(final int id) {
        return id >= 1492000 && id < 1500000;
    }

    public static boolean isUse(final int id) {
        return id >= 2000000 && id <= 2490000;
    }

    public static boolean isSummonSack(final int id) {
        return id / 10000 == 210;
    }

    public static boolean 怪物卡(final int id) {
        return id / 10000 == 238;
    }

    public static boolean isSpecialCard(final int id) {
        return id / 1000 >= 2388;
    }

    public static int getCardShortId(final int id) {
        return id % 10000;
    }

    public static boolean isGem(final int id) {
        return id >= 4250000 && id <= 4251402;
    }

    public static boolean isOtherGem(final int id) {
        switch (id) {
            case 4001174:
            case 4001175:
            case 4001176:
            case 4001177:
            case 4001178:
            case 4001179:
            case 4001180:
            case 4001181:
            case 4001182:
            case 4001183:
            case 4001184:
            case 4001185:
            case 4001186:
            case 4031980:
            case 2041058:
            case 2040727:
            case 1032062:
            case 4032334:
            case 4032312:
            case 1142156:
            case 1142157:
                return true; //mostly quest items
        }
        return false;
    }

    public static boolean isCustomQuest(final int id) {
        return id > 99999;
    }

    public static int getTaxAmount(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.06 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.05 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.04 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.018 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.008 * meso);
        }
        return 0;
    }

    public static int EntrustedStoreTax(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.025 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.02 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.015 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.009 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.004 * meso);
        }
        return 0;
    }

    public static short getSummonAttackDelay(final int id) {
        switch (id) {
            case 15001004: // Lightning
            case 14001005: // Darkness
            case 13001004: // Storm
            case 12001004: // Flame
            case 11001004: // Soul
            case 3221005: // Freezer
            case 3211005: // Golden Eagle
            case 3121006: // Phoenix
            case 3111005: // Silver Hawk
            case 2321003: // Bahamut
            case 2311006: // Summon Dragon
            case 2221005: // Infrit
            case 2121005: // Elquines
                return 3030;
            case 5211001: // Octopus
            case 5211002: // Gaviota
            case 5220002: // Support Octopus
                return 1530;
            case 3211002: // Puppet
            case 3111002: // Puppet
            case 1321007: // Beholder
            case 4341006:
            case 35121009:
            case 35121010:
            case 35111011:
            case 35111002:
                return 0;
        }
        return 0;
    }

    public static short getAttackDelay(final int id) {
        switch (id) { // Assume it's faster(2)
            case 4321001: //tornado spin
                return 40; //reason being you can spam with final assaulter
            case 3121004: // Storm of Arrow
            case 33121009:
            case 13111002: // Storm of Arrow
            case 5221004: // Rapidfire
            case 4221001: //Assassinate?
            case 5201006: // Recoil shot/ Back stab shot
                return 120;
            case 13101005: // Storm Break
                return 360;
            case 5001003: // Double Fire
            case SkillType.僧侶.群體治癒: // Heal
                return 390;
            case 5001001: // Straight/ Flash Fist
            case 15001001: // Straight/ Flash Fist
            case 1321003: // Rush
            case 1221007: // Rush
            case 1121006: // Rush
                return 450;
            case 5211004: // Flamethrower
            case 5211005: // Ice Splitter
            case 4201005: // Savage blow
                return 480;
            case 0: // Normal Attack, TODO delay for each weapon type
            case 5111002: // Energy Blast
            case 15101005: // Energy Blast
            case 1001004: // Power Strike
            case 11001002: // Power Strike
            case 1001005: // Slash Blast
            case 11001003: // Slash Blast
            case 1311005: // Sacrifice
                return 570;
            //case 2101004: // Fire Arrow
            case 12101002: // Fire Arrow
            case 2101005: // Poison Breath
            case 2121003: // Fire Demon
            case 2221003: // Ice Demon
            case 2121006: // Paralyze
            case 3111006: // Strafe
            case 311004: // Arrow Rain
            case 13111000: // Arrow Rain
            case 3111003: // Inferno
            case 3101005: // Arrow Bomb
            case 4001344: // Lucky Seven
            case 14001004: // Lucky seven
            case 4121007: // Triple Throw
            case 14111005: // Triple Throw
            case 4111004: // Shadow Meso
            case 4101005: // Drain
            case 4211004: // Band of Thieves
            case 4201004: // Steal
            case 4001334: // Double Stab
            case 5221007: // Battleship Cannon
            case 1211002: // Charged blow
            case 1311003: // Dragon Fury : Spear
            case 1311004: // Dragon Fury : Pole Arm
            case 3211006: // Strafe
            case 3211004: // Arrow Eruption
            case 3211003: // Blizzard Arrow
            case 3201005: // Iron Arrow
            case 3221001: // Piercing
            case 4111005: // Avenger
            case 14111002: // Avenger
            case 5201001: // Invisible shot
            case 5101004: // Corkscrew Blow
            case 15101003: // Corkscrew Blow
            case 1121008: // Brandish
            case 11111004: // Brandish
            case 1221009: // Blast
                return 600;
            case 5201004: // Blank Shot/ Fake shot
            case 5211000: // Burst Fire/ Triple Fire
            case 5001002: // Sommersault Kick
            case 15001002: // Sommersault Kick
            case 4221007: // Boomerang Stab
            case 1311001: // Spear Crusher, 16~30 pts = 810
            case 1311002: // PA Crusher, 16~30 pts = 810
            case 2221006: // Chain Lightning
                return 660;
            case 4121008: // Ninja Storm
            case 5211006: // Homing Beacon
            case 5221008: // Battleship Torpedo
            case 5101002: // Backspin Blow
            case 2001005: // Magic Claw
            case 12001003: // Magic Claw
            case 2001004: // Energy Bolt

            case 2121001: // Big Bang
            case 2221001: // Big Bang
            case 2321001: // Big Bang
            case 2321007: // Angel's Ray
            case 2201005: // Thunderbolt
            case 2201004: // Cold Beam
            case 2211002: // Ice Strike
            case 4211006: // Meso Explosion
            case 5121005: // Snatch
            case 12111006: // Fire Strike
            case 11101004: // Soul Blade
                return 750;
            case 2301005: // Holy Arrow
                return 500;
            case 15111007: // Shark Wave
            case 2111006: // Elemental Composition
            case 2211006: // Elemental Composition
                return 810;
            case 13111006: // Wind Piercing
            case 4211002: // Assaulter
            case 5101003: // Double Uppercut
            case 2111002: // Explosion
                return 900;
            case 5121003: // Energy Orb
            case 2311004: // Shining Ray
                return 530;
            case 13111007: // Wind Shot
                return 960;
            case 14101006: // Vampire
            case 4121003: // Showdown
            case 4221003: // Showdown
                return 1020;
            case 12101006: // Fire Pillar
                return 1050;
            case 5121001: // Dragon Strike
                return 1060;
            case 2211003: // Thunder Spear
            case 1311006: // Dragon Roar
                return 1140;
            case 11111006: // Soul Driver
                return 1230;
            case 12111005: // Flame Gear
                return 1260;
            case 2111003: // Poison Mist
                return 1320;
            case 5111006: // Shockwave
            case 15111003: // Shockwave
                return 1500;
            case 5121007: // Barrage
            case 15111004: // Barrage
                return 1830;
            case 5221003: // Ariel Strike
            case 5121004: // Demolition
                return 2160;
            case 2321008: // Genesis
                return 2700;
            case 2121007: // Meteor Shower
            case 10001011: // Meteo Shower
            case 2221007: // Blizzard
                return 3060;
        }
        // TODO delay for final attack, weapon type, swing,stab etc
        return 330; // Default usually
    }

    public static byte gachaponRareItem(final int id) {
        switch (id) {
            case 2022217: // 殘暴炎魔的御守
            case 2022221: // 緞帶肥肥的御守
            case 2022222: // 遠古精靈的御守
            case 2022223: // 企鵝王的御守
                return 1;
            case 2370000: // 兵法書(孫子)
            case 2370001: // 兵法書(吳子)
            case 2370002: // 兵法書(尉繚子)
            case 2370003: // 兵法書(六韜)
            case 2370004: // 兵法書(三略)
            case 2370005: // 兵法書(司馬法）
            case 2370006: // 兵法書(李衛公問對)
            case 2370007: // 兵法書(孫兵兵法)
            case 3010054: // 綿羊單人床
            case 2022483: // 加持道具
            case 2210029: // 黃金豬  變身道具
                return 2;
            case 2340000: // White Scroll
            case 2049100: // Chaos Scroll
            case 2049000: // Reverse Scroll
            case 2049001: // Reverse Scroll
            case 2049002: // Reverse Scroll
            case 2040006: // Miracle
            case 2040007: // Miracle
            case 2040303: // Miracle
            case 2040403: // Miracle
            case 2040506: // Miracle
            case 2040507: // Miracle
            case 2040603: // Miracle
            case 2040709: // Miracle
            case 2040710: // Miracle
            case 2040711: // Miracle
            case 2040806: // Miracle
            case 2040903: // Miracle
            case 2041024: // Miracle
            case 2041025: // Miracle
            case 2043003: // Miracle
            case 2043103: // Miracle
            case 2043203: // Miracle
            case 2043303: // Miracle
            case 2043703: // Miracle
            case 2043803: // Miracle
            case 2044003: // Miracle
            case 2044103: // Miracle
            case 2044203: // Miracle
            case 2044303: // Miracle
            case 2044403: // Miracle
            case 2044503: // Miracle
            case 2044603: // Miracle
            case 2044908: // Miracle
            case 2044815: // Miracle
            case 2044019: // Miracle
            case 2044703: // Miracle
            case 1372039: // Elemental wand lvl 130
            case 1372040: // Elemental wand lvl 130
            case 1372041: // Elemental wand lvl 130
            case 1372042: // Elemental wand lvl 130
            case 1092049: // Dragon Khanjar
            case 1382037: // Blade Staff
                return 3;
            case 1102084: // Pink Gaia Cape
            case 1102041: // Pink Adventurer Cape
            case 1402044: // Pumpkin Lantern
            case 1082149: // Brown Work glove
            case 1102086: // Purple Gaia Cape
            case 1102042: // Purple Adventurer Cape

            case 3010065: // Pink Parasol
            case 3010064: // Brown Sand Bunny Cushion
            case 3010063: // Starry Moon Cushion
            case 3010068: // Teru Teru Chair
            case 3012001: // Round the Campfire
            case 3012002: // Rubber Ducky Bath
            case 3010020: // Portable Meal Table
            case 3010041: // Skull Throne

            case 1082179: //yellow marker
                return 3;
            //1 = wedding msg o.o
        }
        return 0;
    }
    public final static int[] goldrewards = {
        2340000, 1, // white scroll
        1402037, 1, // Rigbol Sword
        2290096, 1, // Maple Warrior 20
        2290049, 1, // Genesis 30
        2290041, 1, // Meteo 30
        2290047, 1, // Blizzard 30
        2290095, 1, // Smoke 30
        2290017, 1, // Enrage 30
        2290075, 1, // Snipe 30
        2290085, 1, // Triple Throw 30
        2290116, 1, // Areal Strike
        1302059, 3, // Dragon Carabella
        2049100, 1, // Chaos Scroll
        2340000, 1, // White Scroll
        1092049, 1, // Dragon Kanjar
        1102041, 1, // Pink Cape
        1432018, 3, // Sky Ski
        1022047, 3, // Owl Mask
        3010051, 1, // Chair
        3010020, 1, // Portable meal table
        2040914, 1, // Shield for Weapon Atk

        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe

        1472051, 1, // Green Dragon Sleeve
        1482013, 1, // Emperor's Claw
        1492013, 1, // Dragon fire Revlover

        1382050, 1, // Blue Dragon Staff
        1382045, 1, // Fire Staff, Level 105
        1382047, 1, // Ice Staff, Level 105
        1382048, 1, // Thunder Staff
        1382046, 1, // Poison Staff

        1332032, 4, // Christmas Tree
        1482025, 3, // Flowery Tube

        4001011, 4, // Lupin Eraser
        4001010, 4, // Mushmom Eraser
        4001009, 4, // Stump Eraser

        2030008, 5, // Bottle, return scroll
        1442018, 3, // Frozen Tuna
        2040900, 4, // Shield for DEF
        2000005, 10, // Power Elixir
        2000004, 10, // Elixir
        4280000, 4}; // Gold Box
    public final static int[] silverrewards = {
        3010041, 1, // skull throne
        1002452, 3, // Starry Bandana
        1002455, 3, // Starry Bandana
        2290084, 1, // Triple Throw 20
        2290048, 1, // Genesis 20
        2290040, 1, // Meteo 20
        2290046, 1, // Blizzard 20
        2290074, 1, // Sniping 20
        2290064, 1, // Concentration 20
        2290094, 1, // Smoke 20
        2290022, 1, // Berserk 20
        2290056, 1, // Bow Expert 30
        2290066, 1, // xBow Expert 30
        2290020, 1, // Sanc 20
        1102082, 1, // Black Raggdey Cape
        1302049, 1, // Glowing Whip
        2340000, 1, // White Scroll
        1102041, 1, // Pink Cape
        1452019, 2, // White Nisrock
        4001116, 3, // Hexagon Pend
        4001012, 3, // Wraith Eraser
        1022060, 2, // Foxy Racoon Eye

        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe
        1002587, 3, // Black Wisconsin
        1402044, 1, // Pumpkin lantern
        2101013, 4, // Summoning Showa boss
        1442046, 1, // Super Snowboard
        1422031, 1, // Blue Seal Cushion
        1332054, 3, // Lonzege Dagger
        1012056, 3, // Dog Nose
        1022047, 3, // Owl Mask
        3012002, 1, // Bathtub
        1442012, 3, // Sky snowboard
        1442018, 3, // Frozen Tuna
        1432010, 3, // Omega Spear
        2000005, 10, // Power Elixir
        2000004, 10, // Elixir
        4280001, 4}; // Silver Box

    // 活動普通獎勵
    public static int[] 活動普獎 = {
        0, 40, // 楓幣
        1, 10, // 經驗值
        4, 10, // 10 點楓葉點數
        4031302, 5, // - 海底垃圾 - 污染大海的垃圾。
        4000377, 5, // - 小陀糞便 - 發出惡臭的小陀糞便。
        4000378, 5, // - 大陀糞便 - 發出惡臭的大陀糞便。
        4000110, 5, // - 玩具士兵的刀 - 玩具士兵拿著的塑膠刀。

        4031019, 5, //
        4280000, 3,
        4280001, 4,
        5490000, 3,
        5490001, 4
    };
    public static int[] 活動三獎 = {
        2, 4, // 666666 楓幣
        3, 4, // 10 名聲
        4, 4, // 10 點楓葉點數
        5, 4, // 50 點楓葉點數
        5160000, 5, //表情
        5160001, 5, //表情
        5160002, 5, //表情
        5160003, 5, //表情
        5160004, 5, //表情
        5160005, 5, //表情
        5160006, 5, //表情
        5160007, 5, //表情
        5160008, 5, //表情
        5160009, 5, //表情
        5160010, 5, //表情
        5160011, 5, //表情
        5160012, 5, //表情
        5160013, 5, //表情
        5240017, 5, //表情
        5240000, 5, //表情

        2100001, 5, // - 怪物召喚1 - 召喚1~10等級的怪物。
        2100002, 5, // - 怪物召喚2 - 召喚10~20等級的怪物。
        2100003, 5, // - 怪物召喚3 - 召喚20~30等級的怪物。
        2100004, 5, // - 怪物召喚4 - 召喚30~40等級的怪物。
        2100005, 5, // - 怪物召喚5 - 召喚40~50等級的怪物。
        2100006, 5, // - 怪物召喚6 - 召喚50~60等級的怪物。
        2100007, 5, // - 怪物召喚7 - 召喚60~70等級的怪物。   
        2101000, 5, // - 蘑菇王召喚 - 可以召喚蘑菇王
        2210006, 5, // - 彩虹蝸牛殼 - 由紅寶王掉出的蝸牛殼，傳說擁有可以讓人實現願望的能力。

        4080000, 5, // 綠水靈/菇菇五子棋 - 可以下五子棋的棋盤
        4080001, 5, // 綠水靈/三眼章魚五子棋 - 可以下五子棋
        4080002, 5, // 綠水靈/肥肥五子棋 - 可以下五子棋
        4080003, 5, // 三眼章魚/菇菇五子棋 - 可以下五子棋
        4080004, 5, // 肥肥/三眼章魚五子棋 - 可以下五子棋
        4080005, 5, // 肥肥/菇菇五子棋 - 可以下五子棋
        4080006, 5, // 機器章魚／粉紅發條熊五子棋 - 可下五子棋的棋具，使用機器章魚和粉紅發條熊形狀的棋子。
        4080007, 5, // 機器章魚／黃蜘蛛五子棋 - 可下五子棋的棋具，使用機器章魚和黃蜘蛛形狀的棋子。
        4080008, 5, // 粉紅發條熊／黃蜘蛛五子棋 - 可下五子棋的棋具，使用粉紅發條熊和黃蜘蛛形狀的棋子。
        4080009, 5, // 發條貓熊／機器章魚五子棋 - 可下五子棋的棋具，使用發條貓熊和機器章魚形狀的棋子。
        4080010, 5, // 發條貓熊／粉紅發條熊五子棋 - 可下五子棋的棋具，使用發條貓熊和粉紅發條熊形狀的棋子。
        4080011, 5, // 發條貓熊／黃蜘蛛五子棋 - 可下五子棋的棋具，使用發條貓熊和黃蜘蛛形狀的棋子。
        4080100, 5, // 找碴 - 可以玩找碴遊戲。

        4031019, 5, // 惡魔文件 - 用古代文字紀錄惡魔秘密的文件。

        5121003, 5, // 蔘雞湯 位在將該地圖的角色，約有15分鐘的時間可提 升物理攻擊 +20, 魔法攻擊 +30。可輸入所要傳達的訊息。
        5150000, 5, // 弓箭手村美髮店一般會員卡
        5150001, 5, // 弓箭手村美髮店高級會員卡
        5150002, 1, // 墮落城市美髮店一般會員卡
        5150003, 1, // 墮落城市美髮店高級會員卡
        5150004, 1, // 天空之城美髮店一般會員卡
        5150005, 2, // 天空之城美髮店高級會員卡
        5150006, 2, // 玩具城美髮店一般會員卡
        5150007, 2, // 玩具城美髮店高級會員卡
        5150008, 2, // 昭和村美髮店一般會員卡
        5150009, 14, // 昭和村美髮店高級會員卡

        2022459, 5, // 卡珊德拉的獎勵1 1小時之內額外掉落30%的楓幣。
        2022460, 5, // 卡珊德拉的獎勵2 40分鐘之內額外掉落50% 的楓幣。
        2022461, 5, // 卡珊德拉的獎勵3 30分鐘之內楓幣掉落增加 2倍。
        2022462, 5, // 卡珊德拉的獎勵4 1小時之內道具掉落率增加 50% 。
        2022463, 5, // 卡珊德拉的獎勵5 30分鐘之內道具掉落率增加 2倍。

        2450000, 2, // 獵人的幸運 30分鐘內，透過打怪獲得的經驗值會加倍。

        5152000, 5, // 弓箭手村整形手術一般會員卡
        5152001, 5 // 弓箭手村整形手術高級會員卡
    };
    public static int[] 活動二獎 = {
        4031019, 5, // 惡魔文件
        2049100, 5, // 混沌卷軸60%
        2340000, 1, // 祝福卷軸

        1112405, 1, // 莉琳的戒指
        1112413, 1, // 莉琳的戒指
        1112414, 1, // 莉琳的戒指

        2049000, 2, // 白衣卷軸1%
        2049001, 2, // 白衣卷軸3%
        2049002, 2, // 白衣卷軸5%

        5220040, 2, // 楓葉轉蛋券 - 在楓葉轉蛋機使用，可得到各種物品

        1012076, 2, // - 笑臉面具 - (無描述)
        1012077, 2, // - 哭臉面具 - (無描述)
        1012078, 2, // - 怒目面具 - (無描述)
        1012079, 2, // - 憂鬱面具 - (無描述)
        1012072, 2, // - 哈密瓜冰棒 - (無描述)
        1012073, 2, // - 西瓜冰棒 - (無描述)

        1012058, 2, // 皮諾丘的鼻子
        1012059, 2, // 皮諾丘的鼻子
        1012060, 2, // 皮諾丘的鼻子
        1012061, 2 // 皮諾丘的鼻子
    };
    public static int[] 活動大獎 = {
        6, 5, // 100 點楓葉點數
        4031307, 50, // 藍色禮物盒
        1012139, 10, // 金達萊貼紙
        1012140, 10, // 連翹花貼紙
        1012141, 10 // 幸運草貼紙
    };
    public static int[] fishingReward = {
        0, 40, // Meso
        1, 40, // EXP
        1302021, 5, // Pico Pico Hammer
        1072238, 1, // Voilet Snowshoe
        1072239, 1, // Yellow Snowshoe
        2049100, 1, // Chaos Scroll
        //2049301, 1, // Equip Enhancer Scroll
        //2049401, 1, // Potential Scroll
        1302000, 3, // Sword
        1442011, 1, // Surfboard
        //4000517, 8, // Golden Fish
        //4000518, 25, // Golden Fish Egg
        4031627, 2, // White Bait (3cm)
        4031628, 1, // Sailfish (120cm)
        4031630, 1, // Carp (30cm)
        4031631, 1, // Salmon(150cm)
        4031632, 1, // Shovel
        4031633, 2, // Whitebait (3.6cm)
        4031634, 1, // Whitebait (5cm)
        4031635, 1, // Whitebait (6.5cm)
        4031636, 1, // Whitebait (10cm)
        4031637, 2, // Carp (53cm)
        4031638, 2, // Carp (60cm)
        4031639, 1, // Carp (100cm)
        4031640, 1, // Carp (113cm)
        4031641, 2, // Sailfish (128cm)
        4031642, 2, // Sailfish (131cm)
        4031643, 1, // Sailfish (140cm)
        4031644, 1, // Sailfish (148cm)
        4031645, 2, // Salmon (166cm)
        4031646, 2, // Salmon (183cm)
        4031647, 1, // Salmon (227cm)
        4031648, 1, // Salmon (288cm)
        4031629, 1, // Pot
        1102041, 1, // 粉披
        1102042, 1, // 紫披
        2101120, 1 // 魚怪召喚袋
    };

    public static boolean isDragonItem(int itemId) {
        switch (itemId) {
            case 1372032:
            case 1312031:
            case 1412026:
            case 1302059:
            case 1442045:
            case 1402036:
            case 1432038:
            case 1422028:
            case 1472051:
            case 1472052:
            case 1332049:
            case 1332050:
            case 1322052:
            case 1452044:
            case 1462039:
            case 1382036:
            case 1342010:
                return true;
            default:
                return false;
        }
    }

    public static boolean isReverseItem(int itemId) {
        switch (itemId) {
            case 1002790:
            case 1002791:
            case 1002792:
            case 1002793:
            case 1002794:
            case 1082239:
            case 1082240:
            case 1082241:
            case 1082242:
            case 1082243:
            case 1052160:
            case 1052161:
            case 1052162:
            case 1052163:
            case 1052164:
            case 1072361:
            case 1072362:
            case 1072363:
            case 1072364:
            case 1072365:
            case 1302086:
            case 1312038:
            case 1322061:
            case 1332075:
            case 1332076:
            case 1372045:
            case 1382059:
            case 1402047:
            case 1412034:
            case 1422038:
            case 1432049:
            case 1442067:
            case 1452059:
            case 1462051:
            case 1472071:
            case 1482024:
            case 1492025:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTimelessItem(int itemId) {
        switch (itemId) {
            case 1032031:
            case 1102172:
            case 1002776:
            case 1002777:
            case 1002778:
            case 1002779:
            case 1002780:
            case 1082234:
            case 1082235:
            case 1082236:
            case 1082237:
            case 1082238:
            case 1052155:
            case 1052156:
            case 1052157:
            case 1052158:
            case 1052159:
            case 1072355:
            case 1072356:
            case 1072357:
            case 1072358:
            case 1072359:
            case 1092057:
            case 1092058:
            case 1092059:

            case 1122011:
            case 1122012:

            case 1302081:
            case 1312037:
            case 1322060:
            case 1332073:
            case 1332074:
            case 1372044:
            case 1382057:
            case 1402046:
            case 1412033:
            case 1422037:
            case 1432047:
            case 1442063:
            case 1452057:
            case 1462050:
            case 1472068:
            case 1482023:
            case 1492023:
            case 1342011:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMarrigeRing(int itemid) {
        switch (itemid) {
            case 1112300:
            case 1112301:
            case 1112302:
            case 1112303:
            case 1112304:
            case 1112305:
            case 1112306:
            case 1112307:
            case 1112308:
            case 1112309:
            case 1112310:
            case 1112311:
                return true;
        }
        return false;
    }

    public static boolean CakeMap(final int mapId) {
        return mapId >= 749020000 && mapId <= 749020800;
    }

    public static boolean isRing(int itemId) {
        return itemId >= 1112000 && itemId < 1113000;
    }// 112xxxx - pendants, 113xxxx - belts

    //if only there was a way to find in wz files -.-
    public static boolean isEffectRing(int itemid) {
        return isFriendshipRing(itemid) || isCrushRing(itemid) || isMarriageRing(itemid);
    }

    public static boolean isMarriageRing(int itemId) {
        switch (itemId) {
            case 1112806:
            case 1112807:
            case 1112809:
                return true;
        }
        return false;
    }

    public static boolean isFriendshipRing(int itemId) {
        switch (itemId) {
            case 1112800:
            case 1112801:
            case 1112802:
            case 1112810: //new
            case 1112811: //new, doesnt work in friendship?
            case 1112812: //new, im ASSUMING it's friendship cuz of itemID, not sure.
            case 1112803: //海灘聊天戒指
            case 1112806: // 巧克力聊天戒指
            case 1112807: // 粉紅糖果聊天戒指
            case 1112804:
            case 1049000:
                return true;
        }
        return false;
    }

    public static boolean isCrushRing(int itemId) {
        switch (itemId) {
            case 1112001:
            case 1112002:
            case 1112003:
            case 1112005: //new
            case 1112006: //new
            case 1112007:
            case 1112012:
            case 1112015: //new
            case 1112013:
            case 1048000:
                return true;
        }
        return false;
    }
    public static int[] Equipments_Bonus = {1122017};

    public static int Equipment_Bonus_EXP(final int itemid) { // TODO : Add Time for more exp increase
        switch (itemid) {
            case 1122017:
                return 10;
        }
        return 0;
    }
    public static int[] blockedMaps = {109050000, 280030000, 240060200, 280090000, 280030001, 240060201, 950101100, 950101010};
    //If you can think of more maps that could be exploitable via npc,block nao pliz!

    public static int getExpForLevel(int i, int itemId) {
        if (isReverseItem(itemId)) {
            return getReverseRequiredEXP(i);
        } else if (getMaxLevel(itemId) > 0) {
            return getTimelessRequiredEXP(i);
        }
        return 0;
    }

    public static int getMaxLevel(final int itemId) {
        if (isTimelessItem(itemId)) {
            return 5;
        } else if (isReverseItem(itemId)) {
            return 3;
        } else {
            switch (itemId) {
                case 1302109:
                case 1312041:
                case 1322067:
                case 1332083:
                case 1372048:
                case 1382064:
                case 1402055:
                case 1412037:
                case 1422041:
                case 1432052:
                case 1442073:
                case 1452064:
                case 1462058:
                case 1472079:
                case 1482035:

                case 1302108:
                case 1312040:
                case 1322066:
                case 1332082:
                case 1372047:
                case 1382063:
                case 1402054:
                case 1412036:
                case 1422040:
                case 1432051:
                case 1442072:
                case 1452063:
                case 1462057:
                case 1472078:
                case 1482036:
                    return 1;
                case 1072376:
                    return 2;
            }
        }
        return 0;
    }

    public static int getStatChance() {
        return 25;
    }

    public static MonsterStatus getStatFromWeapon(final int itemid) {
        switch (itemid) {
            case 1302109:
            case 1312041:
            case 1322067:
            case 1332083:
            case 1372048:
            case 1382064:
            case 1402055:
            case 1412037:
            case 1422041:
            case 1432052:
            case 1442073:
            case 1452064:
            case 1462058:
            case 1472079:
            case 1482035:
                return MonsterStatus.ACC;
            case 1302108:
            case 1312040:
            case 1322066:
            case 1332082:
            case 1372047:
            case 1382063:
            case 1402054:
            case 1412036:
            case 1422040:
            case 1432051:
            case 1442072:
            case 1452063:
            case 1462057:
            case 1472078:
            case 1482036:
                return MonsterStatus.SPEED;
        }
        return null;
    }

    public static int getXForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC:
                return -70;
            case SPEED:
                return -50;
        }
        return 0;
    }

    public static int getSkillForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC:
                return 3221006;
            case SPEED:
                return 3121007;
        }
        return 0;
    }
    public final static int[] normalDrops = {
        4001009, //real
        4001010,
        4001011,
        4001012,
        4001013,
        4001014, //real
        4001021,
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007, //end
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007, //end
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007}; //end
    public final static int[] rareDrops = {
        2049100,
        2049301,
        2049401,
        2022326,
        2022193,
        2049000,
        2049001,
        2049002};
    public final static int[] superDrops = {
        2040804,
        2049400,
        2049100};

    public static int getSkillBook(final int job) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 3210:
            case 3310:
            case 3510:
                return 1;
            case 3211:
            case 3311:
            case 3511:
                return 2;
            case 3212:
            case 3312:
            case 3512:
                return 3;
        }
        return 0;
    }

    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10000);
    }

    public static int getMountItem(final int sourceid) {
        switch (sourceid) {
            case 5221006: //海盜船
                return 1932000;
            case 1013: //宇宙船
            case 10001014:
                return 1932001;
            case 1014: // 宇宙衝鋒
            case 10001015:
                return 1932002;
            case 1015: //宇宙光束
            case 10001016:
                return 1932007;
            case 1017: //雪吉拉騎士
            case 10001019:
            case 20001019:
                return 1932003;
            default:
                return 0;
        }
    }

    public static boolean isKatara(int itemId) {
        return itemId / 10000 == 134;
    }

    public static boolean isDagger(int itemId) {
        return itemId / 10000 == 133;
    }

    public static boolean isApplicableSkill(int skil) {
        return skil < 40000000 && (skil % 10000 < 8000 || skil % 10000 > 8003); //no additional/decent skills
    }

    public static boolean isApplicableSkill_(int skil) { //not applicable to saving but is more of temporary
        return skil >= 90000000 || (skil % 10000 >= 8000 && skil % 10000 <= 8003);
    }

    public static boolean isTablet(int itemId) {
        return itemId / 1000 == 2047;
    }

    public static int getSuccessTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 55;
                case 2:
                    return 43;
                case 3:
                    return 33;
                case 4:
                    return 26;
                case 5:
                    return 20;
                case 6:
                    return 16;
                case 7:
                    return 12;
                case 8:
                    return 10;
                default:
                    return 7;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 35;
                case 2:
                    return 18;
                case 3:
                    return 12;
                default:
                    return 7;
            }
        } else {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 50; //-20
                case 2:
                    return 36; //-14
                case 3:
                    return 26; //-10
                case 4:
                    return 19; //-7
                case 5:
                    return 14; //-5
                case 6:
                    return 10; //-4
                default:
                    return 7;  //-3
            }
        }
    }

    public static int getCurseTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 12;
                case 2:
                    return 16;
                case 3:
                    return 20;
                case 4:
                    return 26;
                case 5:
                    return 33;
                case 6:
                    return 43;
                case 7:
                    return 55;
                case 8:
                    return 70;
                default:
                    return 100;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 12;
                case 1:
                    return 18;
                case 2:
                    return 35;
                case 3:
                    return 70;
                default:
                    return 100;
            }
        } else {
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 14; //+4
                case 2:
                    return 19; //+5
                case 3:
                    return 26; //+7
                case 4:
                    return 36; //+10
                case 5:
                    return 50; //+14
                case 6:
                    return 70; //+20
                default:
                    return 100;  //+30
            }
        }
    }

    public static boolean isAccessory(final int itemId) {
        return (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1153000) || (itemId >= 1112000 && itemId < 1113000);
    }

    public static boolean potentialIDFits(final int potentialID, final int newstate, final int i) {
        //first line is always the best
        //but, sometimes it is possible to get second/third line as well
        //may seem like big chance, but it's not as it grabs random potential ID anyway
        if (newstate == 7) {
            return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 30000 : potentialID >= 20000 && potentialID < 30000);
        } else if (newstate == 6) {
            return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 20000 && potentialID < 30000 : potentialID >= 10000 && potentialID < 20000);
        } else if (newstate == 5) {
            return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 10000 && potentialID < 20000 : potentialID < 10000);
        } else {
            return false;
        }
    }

    public static boolean optionTypeFits(final int optionType, final int itemId) {
        switch (optionType) {
            case 10: //weapon
                return 武器(itemId);
            case 11: //any armor
                return !武器(itemId);
            case 20: //shield??????????
                return itemId / 10000 == 109; //just a gues
            case 21: //pet equip?????????
                return itemId / 10000 == 180; //???LOL
            case 40: //face accessory
                return isAccessory(itemId);
            case 51: //hat
                return itemId / 10000 == 100;
            case 52: //cape
                return itemId / 10000 == 110;
            case 53: //top/bottom/overall
                return itemId / 10000 == 104 || itemId / 10000 == 105 || itemId / 10000 == 106;
            case 54: //glove
                return itemId / 10000 == 108;
            case 55: //shoe
                return itemId / 10000 == 107;
            case 90:
                return false; //half this stuff doesnt even work
            default:
                return true;
        }
    }

    public static final boolean isMountItemAvailable(final int mountid, final int jobid) {
        if (jobid != 900 && mountid / 10000 == 190) {
            if (isKOC(jobid)) {
                if (mountid < 1902005 || mountid > 1902007) {
                    return false;
                }
            } else if (isAdventurer(jobid)) {
                if (mountid < 1902000 || mountid > 1902002) {
                    return false;
                }
            } else if (isAran(jobid)) {
                if (mountid < 1902015 || mountid > 1902018) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isEvanDragonItem(final int itemId) {
        return itemId >= 1940000 && itemId < 1980000; //194 = mask, 195 = pendant, 196 = wings, 197 = tail
    }

    public static boolean canScroll(final int itemId) {
        return itemId / 100000 != 19 && itemId / 100000 != 16; //no mech/taming/dragon
    }

    public static boolean canHammer(final int itemId) {
        switch (itemId) {
            case 1122000:
            case 1122076: //ht, chaos ht
                return false;
        }
        if (!canScroll(itemId)) {
            return false;
        }
        return true;
    }
    public static int[] owlItems = new int[]{
        1082002, // work gloves
        2070005,
        2070006,
        1022047,
        1102041,
        2044705,
        2340000, // white scroll
        2040017,
        1092030,
        2040804};

    public static int getMasterySkill(final int job) {
        if (job >= 1410 && job <= 1412) {
            return 14100000;
        } else if (job >= 410 && job <= 412) {
            return 4100000;
        } else if (job >= 520 && job <= 522) {
            return 5200000;
        }
        return 0;
    }

    public static int getExpRate_Below10(final int job) {
        if (GameConstants.isAran(job) || GameConstants.isKOC(job)) {
            return 3;
        }
        return 1;
    }

    public static int getExpRate_Quest(final int level) {
        return 1;
    }

    public static String getCashBlockedMsg(final int id) {
        switch (id) {
            case 5062000:
                //cube
                return "這個東西只能通過自由市場玩家NPC";
        }
        return "這個道具無法購買\r\n未來有機會開放購買。";
    }

    public static boolean isCustomReactItem(final int rid, final int iid, final int original) {
        if (rid == 2008006) { //orbis pq LOL
            return iid == (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 4001055);
            //4001056 = sunday. 4001062 = saturday
        } else {
            return iid == original;
        }
    }
// Custom Balloon Tips on the Login Screen
    private static final List<Balloon> lBalloon = Arrays.asList(
            new Balloon("歡迎來到中正谷", 236, 122),
            new Balloon("禁止開外掛", 0, 276),
            new Balloon("遊戲愉快", 196, 263));

    public static List<Balloon> getBalloons() {
        return lBalloon;
    }

    public static int getJobNumber(int jobz) {
        int job = (jobz % 1000);
        if (job / 100 == 0) {
            return 0; //beginner
        } else if (job / 10 == 0) {
            return 1;
        } else {
            return 2 + (job % 10);
        }
    }

    public static boolean isCarnivalMaps(int mapid) {
        return mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100);

    }

    public static boolean isForceRespawn(int mapid) {
        switch (mapid) {
            case 925100100: //crocs and stuff
                return true;
            default:
                return false;
        }
    }

    public static int getFishingTime(boolean vip, boolean gm) {
        return gm ? 1000 : (vip ? 90000 : 120000);
    }

    public static int getCustomSpawnID(int summoner, int def) {
        switch (summoner) {
            case 9400589:
            case 9400748: //MV
                return 9400706; //jr
            default:
                return def;
        }
    }

    public static boolean canForfeit(int questid) {
        switch (questid) {
            case 20000:
            case 20010:
            case 20015: //cygnus quests
            case 20020:
                return false;
            default:
                return true;
        }
    }

    public static boolean is_new_year_card_item_etc(int nItemID) {
        return nItemID / 10000 == 430;
    }

    public static boolean is_new_year_card_item_con(int nItemID) {
        return nItemID / 10000 == 216;
    }

    public static boolean isGMEquip(final int itemId) {
        switch (itemId) {
            case 1002140://維澤特帽
            case 1042003://維澤特西裝
            case 1062007://維澤特西褲
            case 1322013://維澤特手提包
                return true;
        }
        return false;
    }

    public static boolean isExpChair(final int itemid) {

        switch (itemid / 10000) {
            case 302:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFishingMap(int mapId) {
        switch (mapId) {
            case 749050500:
            case 749050501:
            case 749050502:
                return true;
            default:
                return false;
        }
    }

    public static boolean isChair(final int itemid) {
        return itemid / 10000 == 302;
    }

    public static int getMaxDamage(int level, int jobid, int skillid) {
        int max = 0;

        if (level < 20) {
            max += 900;
        } else if (level < 30) {
            max += 1800;
        } else if (level < 40) {
            max += 5000;
        } else if (level < 50) {
            max += 7000;
        } else if (level < 60) {
            max += 8000;
        } else if (level < 70) {
            max += 9000;
        } else if (level < 80) {
            max += 10000;
        } else if (level < 90) {
            max += 11000;
        } else if (level < 100) {
            max += 12000;
        } else if (level < 110) {
            max += 13000;
        } else {
            max = 200000;
        }
        if (isKOC(jobid)) {
            max += 1000;
        }
        if (skillid == 21110004) {
            max *= 3;
        } else if (skillid == 1111005) {
            max *= 2;
        } else if (skillid == 21100004 || skillid == 4211006) {
            max *= 1.5;
        }
        return max;
    }

    public static boolean isElseSkill(int id) {
        switch (id) {
            case 10001009:
            case 20001009:
            case 1009:   // 武陵道場技能
            case 1020:   // 金字塔技能
            case 10001020:
            case 20001020:
            case 3221001:// 光速神弩
            case 4211006:// 楓幣炸彈
                return true;
        }
        return false;
    }

    private static double getAttackRangeBySkill(AttackInfo attack) {
        double defRange = 0;
        switch (attack.skill) {
            case 21120006: // 極冰暴風
                defRange = 800000.0;
                break;
            case 2121007: // 火流星
            case 2221007: // 暴風雪
            case 2321008: // 天怒
                defRange = 750000.0;
                break;
            case 2221006: // 閃電連擊
            case 3101005: // 炸彈箭
            case 21101003:// 強化連擊
                defRange = 600000.0;
                break;
            case 2111003:
                defRange = 400000.0;
                break;
            case 4001344: // 雙飛斬
            case 1121008: // 無雙劍舞
                defRange = 350000.0;
                break;
            case 2211002: // 冰風暴
                defRange = 300000.0;
                break;
            case 5110001: // 蓄能激發
            case 2311004: // 聖光
            case 2211003: // 落雷凝聚
            case 2001005: // 魔力爪
                defRange = 250000.0;
                break;
            case 5221004:// 迅雷
            case 2321007: // 天使之箭
                defRange = 200000.0;
                break;
            case 20001000: // 蝸牛投擲術
            case 1000: // 蝸牛投擲術
                defRange = 120000.0;
                break;
        }
        return defRange;
    }

    public static MapleWeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) {
            case 30:
                return MapleWeaponType.單手劍;
            case 31:
                return MapleWeaponType.單手斧;
            case 32:
                return MapleWeaponType.單手棍;
            case 33:
                return MapleWeaponType.短劍;
            case 34:
                return MapleWeaponType.雙刀;
            case 37:
                return MapleWeaponType.長杖;
            case 38:
                return MapleWeaponType.短杖;
            case 40:
                return MapleWeaponType.雙手劍;
            case 41:
                return MapleWeaponType.雙手斧;
            case 42:
                return MapleWeaponType.雙手棍;
            case 43:
                return MapleWeaponType.矛;
            case 44:
                return MapleWeaponType.槍;
            case 45:
                return MapleWeaponType.弓;
            case 46:
                return MapleWeaponType.弩;
            case 47:
                return MapleWeaponType.拳套;
            case 48:
                return MapleWeaponType.指虎;
            case 49:
                return MapleWeaponType.火槍;
        }
        return MapleWeaponType.沒有武器;
    }

    private static double getAttackRangeByWeapon(MapleCharacter chr) {
        IItem weapon_item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
        MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.沒有武器 : GameConstants.getWeaponType(weapon_item.getItemId());
        switch (weapon) {
            case 槍:       // 矛
                return 200000;
            case 拳套:     // 拳套
                return 250000;
            case 火槍:     // 火槍
            case 弩:       // 弩
            case 弓:       // 弓
                return 180000;
            default:
                return 100000;
        }
    }

    public static double getAttackRange(MapleCharacter chr, MapleStatEffect def, AttackInfo attack) {
        int rangeInc = chr.getStat().defRange;// 處理遠程職業
        double base = 450.0;// 基礎
        double defRange = ((base + rangeInc) * (base + rangeInc));// 基礎範圍
        if (def != null) {
            // 計算範圍((maxX * maxX) + (maxY * maxY)) + (技能範圍 * 技能範圍))
            defRange += def.getMaxDistanceSq() + (def.getRange() * def.getRange());
            if (getAttackRangeBySkill(attack) != 0) {// 直接指定技能範圍
                defRange = getAttackRangeBySkill(attack);
            }
        } else {// 普通攻擊
            defRange = getAttackRangeByWeapon(chr);// 從武器獲取範圍
        }
        return defRange;
    }
}
