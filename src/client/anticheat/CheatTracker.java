package client.anticheat;

import client.MapleBuffStat;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import constants.GameConstants;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import handling.world.World;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.AutobanManager;
import server.Timer.CheatTimer;
import server.life.MapleMonster;
import server.movement.AbstractLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class CheatTracker {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = lock.readLock(), wL = lock.writeLock();
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new LinkedHashMap<>();
    private final WeakReference<MapleCharacter> chr;
    // For keeping track of speed attack hack.
    private int lastAttackTickCount = 0;
    private byte Attack_tickResetCount = 0;
    private long Server_ClientAtkTickDiff = 0;
    private long lastDamage = 0;
    private long takingDamageSince;
    private int numSequentialDamage = 0;
    private long lastDamageTakenTime = 0;
    private byte numZeroDamageTaken = 0;
    private int numSequentialSummonAttack = 0;
    private long summonSummonTime = 0;
    private int numSameDamage = 0;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit = 0;
    private byte dropsPerSecond = 0;
    private long lastDropTime = 0;
    private byte msgsPerSecond = 0;
    private long lastMsgTime = 0;
    private ScheduledFuture<?> invalidationTask;
    private int gm_message = 100;
    private int lastTickCount = 0, tickSame = 0;
    private long lastASmegaTime = 0;
    public long[] lastTime = new long[6];
    public int 打怪 = 0, 吸怪 = 0, FLY_吸怪 = 0;

    public CheatTracker(final MapleCharacter chr) {
        this.chr = new WeakReference<>(chr);
        invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000);
        takingDamageSince = System.currentTimeMillis();
    }

    /**
     * 檢查攻擊延遲
     *
     * @param skillId
     * @param tickCount
     */
    public final void checkAttack(final int skillId, final int tickCount) {
        short AtkDelay = GameConstants.getAttackDelay(skillId);
        /**
         * 檢查客戶端傳回的攻擊時間
         */
        /**
         * 檢查伺服器端的攻擊時間，阻擋更改客戶端時間加速
         *
         * ServerClientAttackTickCountDiff 伺服器與客戶端時間差距
         */
        if (chr.get().getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null) {
            AtkDelay /= 6;// 使用這Buff之後 tickcount - lastAttackTickCount 可以為0...
        }
        // 攻擊加速
        if (chr.get().getBuffedValue(MapleBuffStat.BOOSTER) != null) {
            AtkDelay /= 1.5;
        }
        // 最終極速
        if (chr.get().getBuffedValue(MapleBuffStat.SPEED_INFUSION) != null) {
            AtkDelay /= 1.35;
        }
        // 狂郎
        if (GameConstants.isAran(chr.get().getJob())) {
            AtkDelay /= 1.4;// 407
        }
        // 海盜、拳霸
        if (chr.get().getJob() >= 500 && chr.get().getJob() <= 512) {
            AtkDelay = 0;// 407
        }
        // 強化連擊
        if (skillId == 21101003 || skillId == 5110001) {
            AtkDelay = 0;
        }
        if (chr.get().isShowDebugInfo()) {
            chr.get().dropMessage(5, "SS攻擊速度檢測，間隔:" + (tickCount - lastAttackTickCount) + "，最大限度：" + AtkDelay);
        }
        if ((tickCount - lastAttackTickCount) < AtkDelay) {
            if (打怪 >= 100) {
                if (!chr.get().hasGmLevel(1)) {
                    chr.get().ban("攻擊速度異常，技能: " + skillId + " check: " + (tickCount - lastAttackTickCount) + " " + "AtkDelay: " + AtkDelay, true, true, false);
                    chr.get().sendHackShieldDetected();
                    chr.get().getClient().disconnect(true, false);
                    String reason = "使用違法程式練功";
                    World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("[自動封鎖系統] " + chr.get().getName() + " 因為" + reason + "而被管理員永久停權。").getBytes());
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + chr.get().getName() + " 攻擊無延遲自動封鎖! ").getBytes());
                } else {
                    chr.get().dropMessage("觸發攻擊速度封鎖");
                }
                return;
            }
            打怪++;
            registerOffense(CheatingOffense.攻擊速度過快_伺服器端, "攻擊速度異常，技能: " + skillId + " check: " + (tickCount - lastAttackTickCount) + " " + "AtkDelay: " + AtkDelay);
        }
        chr.get().updateTick(tickCount);
        lastAttackTickCount = tickCount;
    }

    /**
     * 檢查角色受到傷害
     *
     * @param damage
     */
    public final void checkTakeDamage(final int damage) {
        numSequentialDamage++;
        lastDamageTakenTime = System.currentTimeMillis();

        // System.out.println("tb" + timeBetweenDamage);
        // System.out.println("ns" + numSequentialDamage);
        // System.out.println(timeBetweenDamage / 1500 + "(" + timeBetweenDamage / numSequentialDamage + ")");
        if (lastDamageTakenTime - takingDamageSince / 500 < numSequentialDamage) {
//            registerOffense(CheatingOffense.FAST_TAKE_DAMAGE);
        }
        if (lastDamageTakenTime - takingDamageSince > 4500) {
            takingDamageSince = lastDamageTakenTime;
            numSequentialDamage = 0;
        }
        /*	(non-thieves)
         Min Miss Rate: 2%
         Max Miss Rate: 80%
         (thieves)
         Min Miss Rate: 5%
         Max Miss Rate: 95%*/
        if (damage == 0) {
            numZeroDamageTaken++;
            if (numZeroDamageTaken >= 35) { // Num count MSEA a/b players
                numZeroDamageTaken = 0;
                registerOffense(CheatingOffense.迴避過高, "迴避率過高 ");
            }
        } else if (damage != -1) {
            numZeroDamageTaken = 0;
        }
    }

    /**
     * 檢查相同傷害
     *
     * @param dmg
     * @param expected
     */
    public final void checkSameDamage(final int dmg, final double expected) {
        if (dmg > 2000 && lastDamage == dmg && chr.get() != null && (chr.get().getLevel() < 175 || dmg > expected * 2)) {
            numSameDamage++;

            if (numSameDamage > 5) {
                numSameDamage = 0;
                registerOffense(CheatingOffense.攻擊數值異常相同, numSameDamage + " 次, 攻擊傷害: " + dmg + ", 預計傷害: " + expected + " [等級: " + chr.get().getLevel() + ", 職業: " + chr.get().getJob() + "]");
            }
        } else {
            lastDamage = dmg;
            numSameDamage = 0;
        }
    }

    public final void resetSummonAttack() {
        summonSummonTime = System.currentTimeMillis();
        numSequentialSummonAttack = 0;
    }

    /**
     * 檢查召喚獸攻擊
     *
     * @return
     */
    public final boolean checkSummonAttack() {
        numSequentialSummonAttack++;
        //estimated
        // System.out.println(numMPRegens + "/" + allowedRegens);
        // long time = (System.currentTimeMillis() - summonSummonTime) / (2000 + 1) + 3l;
        //  if (time < numSequentialSummonAttack) {
        //        registerOffense(CheatingOffense.FAST_SUMMON_ATTACK, chr.get().getName() + "快速召喚獸攻擊 " + time + " < " + numSequentialSummonAttack);
        //      return false;
        //  }
        return true;
    }

    public final void checkDrop() {
        checkDrop(false);
    }

    /**
     * 檢查掉落
     *
     * @param dc
     */
    public final void checkDrop(final boolean dc) {
        if ((System.currentTimeMillis() - lastDropTime) < 1000) {
            dropsPerSecond++;
            if (dropsPerSecond >= (dc ? 32 : 16) && chr.get() != null) {
                if (dc) {
                    chr.get().sendHackShieldDetected();
                    chr.get().getClient().disconnect(true, false);
                } else {
                    chr.get().getClient().setMonitored(true);
                }
            }
        } else {
            dropsPerSecond = 0;
        }
        lastDropTime = System.currentTimeMillis();
    }

    public boolean canAvatarSmega2() {
        long time = 10 * 1000;
        if (chr.get() != null) {
            if (chr.get().getId() == 845 || chr.get().getId() == 5247 || chr.get().getId() == 12048) {
                time = 20 * 1000;
            }
            if (lastASmegaTime + time > System.currentTimeMillis() && !chr.get().isGM()) {
                return false;
            }
        }
        lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public synchronized boolean GMSpam(int limit, int type) {
        if (type < 0 || lastTime.length < type) {
            type = 1; // default xD
        }
        if (System.currentTimeMillis() < limit + lastTime[type]) {
            return true;
        }
        lastTime[type] = System.currentTimeMillis();
        return false;
    }

    public final void checkMsg() { //ALL types of msg. caution with number of  msgsPerSecond
        if ((System.currentTimeMillis() - lastMsgTime) < 1000) { //luckily maplestory has auto-check for too much msging
            msgsPerSecond++;
            /*if (msgsPerSecond > 10 && chr.get() != null) {
                chr.get().sendHackShieldDetected();
                chr.get().getClient().getSession().close();
                }
             }*/
        } else {
            msgsPerSecond = 0;
        }
        lastMsgTime = System.currentTimeMillis();
    }

    public final int getAttacksWithoutHit() {
        return attacksWithoutHit;
    }

    public final void setAttacksWithoutHit(final boolean increase) {
        if (increase) {
            this.attacksWithoutHit++;
        } else {
            this.attacksWithoutHit = 0;
        }
    }

    public final void registerOffense(final CheatingOffense offense) {
        registerOffense(offense, null);
    }

    public final void registerOffense(final CheatingOffense offense, final String param) {
        final MapleCharacter chrhardref = chr.get();
        if (chrhardref == null || !offense.isEnabled() || chrhardref.isClone()) {
            return;
        }
        if (chr.get().hasGmLevel(5)) {
            chr.get().dropMessage("註冊：" + offense + " 原因：" + param);
        }
        CheatingOffenseEntry entry = null;
        rL.lock();
        try {
            entry = offenses.get(offense);
        } finally {
            rL.unlock();
        }
        if (entry != null && entry.isExpired()) {
            expireEntry(entry);
            entry = null;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        if (offense.shouldAutoban(entry.getCount())) {
            final byte type = offense.getBanType();
            String outputFileName;
            if (type == 1) {
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()), 5000);
            } else if (type == 2) {
                outputFileName = "斷線";
                chrhardref.sendHackShieldDetected();
                chrhardref.getClient().disconnect(true, false);
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + chrhardref.getName() + " 自動斷線 類別: " + offense.toString() + " 原因: " + (param == null ? "" : (" - " + param))).getBytes());
            } else if (type == 3) {
                boolean ban = true;
                outputFileName = "封鎖";
                String show = "使用違法程式練功";
                String real = "";
                if (offense.toString() == "ITEMVAC_SERVER") {
                    outputFileName = "全圖吸物";
                    real = "使用全圖吸物";
                } else if (offense.toString() == "FAST_SUMMON_ATTACK") {
                    outputFileName = "召喚獸無延遲";
                    real = "使用召喚獸無延遲攻擊";
                } else if (offense.toString() == "MOB_VAC") {
                    outputFileName = "吸怪";
                    real = "使用吸怪";
                } else if (offense.toString() == "ATTACK_FARAWAY_MONSTER_BAN") {
                    outputFileName = "全圖打";
                    real = "使用全圖打";
                } else {
                    ban = false;
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + " (編號: " + chrhardref.getId() + " )使用外掛! " + StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : (" - " + param))).getBytes());
                }

                if (chr.get().hasGmLevel(1)) {
                    chr.get().dropMessage("觸發違規: " + real + " param: " + (param == null ? "" : (" - " + param)));
                } else if (ban) {
                    chrhardref.ban(chrhardref.getName() + real, true, true, false);
                    chrhardref.sendHackShieldDetected();
                    chrhardref.getClient().disconnect(true, false);
                    World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("[封鎖系統] " + chrhardref.getName() + " 因為" + show + "而被管理員永久停權。").getBytes());
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + chrhardref.getName() + " " + real + "自動封鎖! ").getBytes());
                } else {
                }
            }
            gm_message = 100;
            return;
        }

        wL.lock();

        try {
            offenses.put(offense, entry);
        } finally {
            wL.unlock();
        }
        switch (offense) {
            case 召喚獸無延遲:
            case 拾取物品距離過遠_伺服器端:
            case 怪物全圖吸:
            case 魔法攻擊過高_1:
            case 魔法攻擊過高_2:
            case 攻擊過高_1:
            case 攻擊過高_2:
            case 攻擊距離過遠:
            //case ATTACK_FARAWAY_MONSTER_SUMMON:
            case 攻擊數值異常相同:
                gm_message--;
                boolean log = false;
                String out_log = "";
                String show = offense.name();
                switch (show) {
                    case "ATTACK_FARAWAY_MONSTER":
                        show = "全圖打";
                        out_log = "攻擊範圍異常";
                        log = true;
                        break;
                    case "MOB_VAC":
                        show = "使用吸怪";
                        out_log = "吸怪";
                        log = true;
                        break;
                }
                if (gm_message % 5 == 0) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + chrhardref.getName() + " (編號:" + chrhardref.getId() + ")疑似外掛! " + show + (param == null ? "" : (" - " + param))).getBytes());
                }
                if (gm_message == 0) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[封號系統] " + chrhardref.getName() + " (編號: " + chrhardref.getId() + " )疑似外掛！" + show + (param == null ? "" : (" - " + param))).getBytes());
                    AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()), 5000);
                    gm_message = 100;
                }
                break;
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        if (newTick == lastTickCount) { //definitely packet spamming
/*	    if (tickSame >= 5) { //i could also add a check for less than, but i'm not too worried at the moment :)
            chr.get().sendHackShieldDetected();
            chr.get().getClient().getSession().close();
        } else {*/
            tickSame++;
//	    }
        } else {
            tickSame = 0;
        }
        lastTickCount = newTick;
    }

    public final void expireEntry(final CheatingOffenseEntry coe) {
        wL.lock();
        try {
            offenses.remove(coe.getOffense());
        } finally {
            wL.unlock();
        }
    }

    public final int getPoints() {
        int ret = 0;
        CheatingOffenseEntry[] offenses_copy;
        rL.lock();
        try {
            offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
        } finally {
            rL.unlock();
        }
        for (final CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                expireEntry(entry);
            } else {
                ret += entry.getPoints();
            }
        }
        return ret;
    }

    public final Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(offenses);
    }

    public final String getSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<CheatingOffenseEntry> offenseList = new ArrayList<>();
        rL.lock();
        try {
            for (final CheatingOffenseEntry entry : offenses.values()) {
                if (!entry.isExpired()) {
                    offenseList.add(entry);
                }
            }
        } finally {
            rL.unlock();
        }
        Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>() {

            @Override
            public final int compare(final CheatingOffenseEntry o1, final CheatingOffenseEntry o2) {
                final int thisVal = o1.getPoints();
                final int anotherVal = o2.getPoints();
                return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
            }
        });
        final int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).getOffense().name()));
            ret.append(": ");
            ret.append(offenseList.get(x).getCount());
            if (x != to - 1) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public final void dispose() {
        if (invalidationTask != null) {
            invalidationTask.cancel(false);
        }
        invalidationTask = null;

    }

    public long[] getLastGMspam() {
        return lastTime;
    }
    
    public void checkMonsterMovment(MapleMonster monster, List<LifeMovementFragment> res, Point startPos)
    {
        try {
            boolean fly = monster.getStats().getFly();
            Point endPos = null;
            int reduce_x = 0;
            int reduce_y = 0;
            for (LifeMovementFragment move : res) {
                if ((move instanceof AbstractLifeMovement)) {
                    endPos = ((LifeMovement) move).getPosition();
                    try {
                        reduce_x = Math.abs(startPos.x - endPos.x);
                        reduce_y = Math.abs(startPos.y - endPos.y);
                    } catch (Exception ex) {
                    }
                }
            }

            if (!fly) {
                int GeneallyDistance_y = 150;
                int GeneallyDistance_x = 200;
                int Check_x = 250;
                int max_x = 450;
                switch (chr.get().getMapId()) {
                    case 100040001:
                    case 926013500:
                        GeneallyDistance_y = 200;
                        break;
                    case 200010300:
                        GeneallyDistance_x = 1000;
                        GeneallyDistance_y = 500;
                        break;
                    case 220010600:
                    case 926013300:
                        GeneallyDistance_x = 200;
                        break;
                    case 211040001:
                        GeneallyDistance_x = 220;
                        break;
                    case 101030105:
                        GeneallyDistance_x = 250;
                        break;
                    case 541020500:
                        Check_x = 300;
                        break;
                }
                switch (monster.getId()) {
                    case 4230100:
                        GeneallyDistance_y = 200;
                        break;
                    case 9410066:
                        Check_x = 1000;
                        break;
                }
                if (GeneallyDistance_x > max_x) {
                    max_x = GeneallyDistance_x;
                }
                if (((reduce_x > GeneallyDistance_x || reduce_y > GeneallyDistance_y) && reduce_y != 0) || (reduce_x > Check_x && reduce_y == 0) || reduce_x > max_x ) {
                    吸怪++;
                    if (吸怪 % 50 == 0 || reduce_x > max_x) {
                        chr.get().getCheatTracker().registerOffense(CheatingOffense.怪物全圖吸, "(地圖: " +  chr.get().getMapId() + " 怪物數量:" +  吸怪 + ")");
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " +  chr.get().getName() + " (編號: " +  chr.get().getId() + ")使用吸怪(" +  吸怪 + ")! - 地圖:" +  chr.get().getMapId() + "(" +  chr.get().getMap().getMapName() + ")").getBytes());
                    }
                }
            }

        } catch (Exception ex) {

        }
    }

    private final class InvalidationTask implements Runnable {

        @Override
        public final void run() {
            CheatingOffenseEntry[] offenses_copy;
            rL.lock();
            try {
                offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
            } finally {
                rL.unlock();
            }
            for (CheatingOffenseEntry offense : offenses_copy) {
                if (offense.isExpired()) {
                    expireEntry(offense);
                }
            }
            if (chr.get() == null) {
                dispose();
            }
        }
    }
}
