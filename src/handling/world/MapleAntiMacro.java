/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.world;

import client.MapleCharacter;
import client.anticheat.captcha.Captcha;
import client.anticheat.captcha.CaptchaFactory;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import server.Randomizer;
import server.Randomizer;
import server.Timer.MapTimer;

import tools.MaplePacketCreator;
import tools.Pair;

/**
 *
 * @author pungin
 */
public class MapleAntiMacro {

    public static class MapleAntiMacroInfo {

        private final MapleCharacter source;
        private final int mode;
        private String code;
        private final long startTime;
        private ScheduledFuture<?> schedule;
        private int timesLeft = 2;

        MapleAntiMacroInfo(MapleCharacter from, int mode, String code, long time, ScheduledFuture<?> schedule) {
            source = from;
            this.mode = mode;
            this.code = code;
            startTime = time;
            this.schedule = schedule;
        }

        public MapleCharacter getSourcePlayer() {
            return source;
        }

        public int antiMode() {
            return mode;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setSchedule(ScheduledFuture<?> schedule) {
            cancelSchedule();
            this.schedule = schedule;
        }

        public void cancelSchedule() {
            if (schedule != null) {
                schedule.cancel(false);
            }
        }

        public int antiFailure() {
            return --timesLeft;
        }

        public int getTimesLeft() {
            return timesLeft - 1;
        }
    }

    private static final Map<String, MapleAntiMacroInfo> antiPlayers = new HashMap();
    private static final Map<String, Long> lastAntiTime = new HashMap();

    // 測謊機類型
    public final static int SYSTEM_ANTI = 0; // 系統自動偵測
    public final static int ITEM_ANTI = 1; // 道具測謊
    public final static int GM_SKILL_ANTI = 2; // 管理員技能測謊

    // 角色測謊狀態常量
    public final static int CAN_ANTI = 0; // 可測謊
    public final static int NON_ATTACK = 1; // 非攻擊狀態
    public final static int ANTI_COOLING = 2; // 已通過測謊
    public final static int ANTI_NOW = 3; // 正在測謊

    public static int getCharacterState(MapleCharacter chr) {
        // 判斷非攻擊狀態
        /*
        if (!chr.isAttacking) { TODO : 測謊機攻擊狀態判斷
            return NON_ATTACK;
        }
         */

        // 判斷冷卻狀態
        if (isCooling(chr.getName())) {
            return ANTI_COOLING;
        }

        // 判斷是否正在被測謊
        if (isAntiNow(chr.getName())) {
            return ANTI_NOW;
        }

        // 可測謊
        return CAN_ANTI;
    }

    public static boolean isCooling(String name) {
        if (lastAntiTime.containsKey(name)) {
            if (System.currentTimeMillis() - lastAntiTime.get(name) < 30 * 60 * 1000) { // 30分鐘冷卻
                return true;
            } else {
                lastAntiTime.remove(name);
            }
        }
        return false;
    }

    public static boolean isAntiNow(String name) {
        return antiPlayers.containsKey(name) && System.currentTimeMillis() - antiPlayers.get(name).getStartTime() < 60 * 1000;
    }

    public static boolean startAntiMacro(MapleCharacter chr, final MapleCharacter victim, byte mode) {
        int antiState = MapleAntiMacro.getCharacterState(victim);
        switch (antiState) {
            case MapleAntiMacro.CAN_ANTI: {
                break;
            }
            case MapleAntiMacro.NON_ATTACK: {
                if (chr != null) {
                    chr.getClient().sendPacket(MaplePacketCreator.AntiMacro.nonAttack());
                }
                return false;
            }
            case MapleAntiMacro.ANTI_COOLING: {
                if (chr != null) {
                    chr.getClient().sendPacket(MaplePacketCreator.AntiMacro.alreadyPass());
                }
                return false;
            }
            case MapleAntiMacro.ANTI_NOW: {
                if (chr != null) {
                    chr.getClient().sendPacket(MaplePacketCreator.AntiMacro.antiMacroNow());
                }
                return false;
            }
            default: {
                System.out.println("測謊機狀態出現未知類型：" + antiState);
                return false;
            }
        }

        Captcha captcha = CaptchaFactory.getInstance().getCaptcha();
        MapleAntiMacroInfo ami = new MapleAntiMacroInfo(chr, mode, captcha.getAnswer(), System.currentTimeMillis(), MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (antiPlayers.containsKey(victim.getName())) {
                    antiFailure(victim);
                }
            }
        }, 60 * 1000));
        antiPlayers.put(victim.getName(), ami);
        victim.getClient().sendPacket(MaplePacketCreator.AntiMacro.getImage(mode, captcha.getImageData(), ami.getTimesLeft()));
        if (chr != null) {
            chr.getClient().sendPacket(MaplePacketCreator.AntiMacro.antiMsg(mode, victim.getName()));
        }
        return true;
    }

    public static void antiSuccess(MapleCharacter victim) {
        MapleAntiMacroInfo ami = null;
        if (antiPlayers.containsKey(victim.getName())) {
            ami = antiPlayers.get(victim.getName());
            if (ami.antiMode() == ITEM_ANTI) {
                victim.gainMeso(5000, true);
            }
            MapleCharacter chr = ami.getSourcePlayer();
            if (chr != null) {
                chr.getClient().sendPacket(MaplePacketCreator.AntiMacro.successMsg(2, victim.getName()));
            }
        }
        victim.setAntiMacroFailureTimes(0);
        victim.getClient().sendPacket(MaplePacketCreator.AntiMacro.success(ami == null ? SYSTEM_ANTI : ami.antiMode()));
        stopAnti(victim.getName());
        lastAntiTime.put(victim.getName(), System.currentTimeMillis());
    }

    public static void antiFailure(MapleCharacter victim) {
        MapleAntiMacroInfo ami = null;
        if (antiPlayers.containsKey(victim.getName())) {
            ami = antiPlayers.get(victim.getName());
            MapleCharacter chr = ami.getSourcePlayer();
            if (chr != null && ami.antiMode() == GM_SKILL_ANTI) {
                chr.getClient().sendPacket(MaplePacketCreator.AntiMacro.failureScreenshot(victim.getName()));
            }
        }
        if (victim.addAntiMacroFailureTimes() < 5) {
            victim.changeMap(victim.getMap().getReturnMap().getId());
            victim.getClient().sendPacket(MaplePacketCreator.AntiMacro.failure(ami == null ? SYSTEM_ANTI : ami.antiMode()));
        } else {
            victim.setAntiMacroFailureTimes(0);
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            victim.tempban("測謊機連續失敗5次。", cal, 1, false);
            victim.getClient().disconnect(true, false);
            victim.getClient().disconnect(true, false);

            String msg = "[GM 密語] " + victim.getName() + "  因為測謊機連續失敗5次而被封鎖一個禮拜。";
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice(msg).getBytes());

        }
        stopAnti(victim.getName());
    }

    public static void stopAnti(String name) {
        if (antiPlayers.containsKey(name)) {
            antiPlayers.get(name).cancelSchedule();
        }
        antiPlayers.remove(name);
    }

    public static void antiReduce(MapleCharacter victim) {
        if (antiPlayers.containsKey(victim.getName())) {
            MapleAntiMacroInfo ami = antiPlayers.get(victim.getName());
            if (ami.antiFailure() > 0) {
                refreshCode(victim);
            } else {
                antiFailure(victim);
            }
        }
    }

    public static boolean verifyCode(String name, String code) {
        if (!antiPlayers.containsKey(name)) {
            return false;
        }
        return antiPlayers.get(name).getCode().equalsIgnoreCase(code);
    }

    /**
     *
     * @param victim
     */
    public static void refreshCode(final MapleCharacter victim) {
        if (antiPlayers.containsKey(victim.getName())) {
            MapleAntiMacroInfo ami = antiPlayers.get(victim.getName());
            Captcha captcha = CaptchaFactory.getInstance().getCaptcha();
            ami.setCode(captcha.getAnswer());
            ami.setSchedule(MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (antiPlayers.containsKey(victim.getName())) {
                        antiFailure(victim);
                    }
                }
            }, 60 * 1000));
            victim.getClient().sendPacket(MaplePacketCreator.AntiMacro.getImage((byte) ami.antiMode(), captcha.getImageData(), ami.getTimesLeft()));
            
        }
    }
}
