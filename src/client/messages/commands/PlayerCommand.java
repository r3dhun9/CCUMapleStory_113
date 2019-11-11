package client.messages.commands;

import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import handling.world.World;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMap;
import java.util.Arrays;
import java.awt.Point;
import java.util.Calendar;
import server.Timer;
import server.life.MapleLifeFactory;
import server.life.OverrideMonsterStats;
import tools.FilePrinter;
import tools.StringUtil;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class help extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropNPC("\t\t #i3994014##i3994018##i3994070##i3994061##i3994005##i3991038##i3991004#\r\t\t\t\t\t\t #i3994078##i3991040#\t\t\r\n\t\t#i3991035##i3994067##i3994079##i3994071##i3994002##i3994012##i3994077#\r\r\n\t      #fMob/0100101.img/move/1##b 親愛的： #h \r\n #fMob/0100101.img/move/1##k\r\r\n\t      #fMob/0130101.img/move/1##g[以下是玩家指令]#k#fMob/0130101.img/move/1#\r\n\t  #d▇▇▆▅▄▃▂#r萬用指令區#d▂▃▄▅▆▇▇\r\n\t\t#b@ea#k - #r<解除異常+查看當前狀態>#k\r\n\t\t#b@mob#k - #r<查看身邊怪物訊息>#k\r\n\t\t#b@save#k - #r<存檔>#k\r\n\t\t#b@CGM <訊息>#k - #r<傳送訊息給GM>#k\r\n\t\t#b@dpm#k - #r<測試每分鐘平均傷害>#k\r\n\t  #g▇▇▆▅▄▃▂#dNPＣ指令區#g▂▃▄▅▆▇▇\r\n\t\t#b@丟裝/@DropCash#k - #r<丟棄點裝>#k\r\n\t\t#b@npc#k - #r<工具箱>#k\r\n\t\t#b@pk#k - #r<小遊戲>#k\r\n\t\t#b@event#k - #r<參加活動>#k\r\n\t\t#b@bspq#k - #r<BOSSPQ兌換NPC>#k");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@help - 幫助").toString();
        }
    }

    public abstract static class OpenNPCCommand extends CommandExecute {

        protected int npc = -1;
        private static final int[] npcs = { //Ish yur job to make sure these are in order and correct ;(
            9010017,
            9000001,
            9000058,
            9330082,
            9209002};

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                        return true;
                    }
                }
                if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                    c.getPlayer().dropMessage(1, "你的等級必須是10等.");
                    return true;
                }
                if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                    return true;
                }
                if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                    return true;
                }
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return true;
        }
    }

    public static class DropCash extends OpenNPCCommand {

        public DropCash() {
            npc = 0;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@dropbash - 呼叫清除現金道具npc").toString();
        }

    }

    public static class event extends OpenNPCCommand {

        public event() {
            npc = 1;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@event - 呼叫活動npc").toString();
        }
    }

    public static class npc extends OpenNPCCommand {

        public npc() {
            npc = 2;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@npc - 呼叫萬能npc").toString();
        }
    }

    public static class bspq extends OpenNPCCommand {

        public bspq() {
            npc = 3;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@bspq -　BSPQ兌換NPC").toString();
        }
    }
    public static class pk extends OpenNPCCommand {

        public pk() {
            npc = 4;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@pk - 呼叫猜拳npc").toString();
        }
    }

    public static class save extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int res = c.getPlayer().saveToDB(true, true);
            if (res == 1) {
                c.getPlayer().dropMessage(5, "保存成功！");
            } else {
                c.getPlayer().dropMessage(5, "保存失敗！");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@save - 存檔").toString();
        }
    }

    public static class dpm extends CommandExecute {

        @Override
        public boolean execute(final MapleClient c, String[] splitted) {
            if ((c.getPlayer().getMapId() == 100000000 && c.getPlayer().getLevel() >= 70) || c.getPlayer().isGM()) {
                if (!c.getPlayer().isTestingDPS()) {
                    c.getPlayer().toggleTestingDPS();
                    c.getPlayer().dropMessage(5, "請持續攻擊怪物1分鐘，來測試您的每秒輸出！");
                    final MapleMonster mm = MapleLifeFactory.getMonster(9001007);
                    int distance = ((c.getPlayer().getJob() >= 300 && c.getPlayer().getJob() < 413) || (c.getPlayer().getJob() >= 1300 && c.getPlayer().getJob() < 1500) || (c.getPlayer().getJob() >= 520 && c.getPlayer().getJob() < 600)) ? 125 : 50;
                    Point p = new Point(c.getPlayer().getPosition().x - distance, c.getPlayer().getPosition().y);
                    mm.setBelongTo(c.getPlayer());
                    final long newhp = Long.MAX_VALUE;
                    OverrideMonsterStats overrideStats = new OverrideMonsterStats();
                    overrideStats.setOHp(newhp);
                    mm.setHp(newhp);
                    mm.setOverrideStats(overrideStats);
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(mm, p);
                    final MapleMap nowMap = c.getPlayer().getMap();
                    Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            long health = mm.getHp();
                            nowMap.killMonster1(mm);
                            long dps = (newhp - health) / 15;
                            if (dps > c.getPlayer().getDPS()) {
                                c.getPlayer().dropMessage(6, "你的DPM是 " + dps + ". 這是一個新的紀錄！");
                                c.getPlayer().setDPS(dps);
                                c.getPlayer().savePlayer();
                                c.getPlayer().toggleTestingDPS();
                            } else {
                                c.getPlayer().dropMessage(6, "你的DPM是 " + dps + ". 您目前的紀錄是 " + c.getPlayer().getDPS() + ".");
                                c.getPlayer().toggleTestingDPS();
                            }

                        }
                    }, 60000);
                } else {
                    c.getPlayer().dropMessage(5, "請先把你的這回DPM測試完畢。");
                    return true;
                }
            } else {
                c.getPlayer().dropMessage(5, "只能在弓箭手村測試DPM，並且等級符合70以上。");
                return true;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("").toString();
        }
    }

    public static class expfix extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
            c.getPlayer().dropMessage(5, "經驗修復完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@expfix - 經驗歸零").toString();
        }
    }

    /*public static class 在線人數 extends online {
        
     }
    
     public static class online extends CommandExecute {
        
     @Override
     public boolean execute(MapleClient c, String[] splitted) {
     java.util.Map<Integer, Integer> connected = World.getConnected();
     StringBuilder conStr = new StringBuilder("當前伺服器總計: \r\n\r\n");
     boolean first = true;
     for (int i : connected.keySet()) {
     if (!first) {
     conStr.append(" ");
     } else {
     first = false;
     }
     if (i == 0) {
     conStr.append("\r\n");
     conStr.append(connected.get(i) + "人");
     } else {
     conStr.append("當前" + i + "頻道 \r\n");
     conStr.append(": ");
     conStr.append(connected.get(i));
     conStr.append("人");
     }
     }
     c.getPlayer().dropMessage(6, conStr.toString());
     return true;
     }
        
     @Override
     public String getMessage() {
     return new StringBuilder().append("@online - 查看線上人數").toString();
     }
     }*/
    public static class ea extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "解卡完畢.");
            c.getPlayer().dropMessage(6, "當前系統時間" + FilePrinter.getLocalDateString() + " 星期" + getDayOfWeek());
            c.getPlayer().dropMessage(6, "經驗值倍率 " + ((Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + (c.getPlayer().getStat().equippedFairy ? c.getPlayer().getFairyExp() : 0)) + "%, 掉寶倍率 " + Math.round(c.getPlayer().getDropMod() * (c.getPlayer().getStat().dropBuff / 100.0) * 100) + "%, 楓幣倍率 " + Math.round((c.getPlayer().getStat().mesoBuff / 100.0) * 100) + "%");
            c.getPlayer().dropMessage(6, "目前剩餘 " + c.getPlayer().getCSPoints(1) + " GASH " + c.getPlayer().getCSPoints(2) + " 楓葉點數 ");
            c.getPlayer().dropMessage(6, "已使用:" + c.getPlayer().getHpMpApUsed() + " 張能力重置捲");
            c.getPlayer().dropMessage(6, "當前延遲 " + c.getPlayer().getClient().getLatency() + " 毫秒");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@ea - 解卡").toString();
        }

        public static String getDayOfWeek() {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            String dd = String.valueOf(dayOfWeek);
            switch (dayOfWeek) {
                case 0:
                    dd = "日";
                    break;
                case 1:
                    dd = "一";
                    break;
                case 2:
                    dd = "二";
                    break;
                case 3:
                    dd = "三";
                    break;
                case 4:
                    dd = "四";
                    break;
                case 5:
                    dd = "五";
                    break;
                case 6:
                    dd = "六";
                    break;
            }
            return dd;
        }
    }

    public static class mob extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleMonster monster = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                monster = (MapleMonster) monstermo;
                if (monster.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物 " + monster.toString());
                }
            }
            if (monster == null) {
                c.getPlayer().dropMessage(6, "找不到地圖上的怪物");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@mob - 查看怪物狀態").toString();
        }
    }

    /*public static class 卡圖 extends stocked {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@卡圖 - 解除卡圖").toString();
        }
    }

    public static class stocked extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {

            if (MapConstants.isCar(c.getPlayer().getMapId())) {
                c.getPlayer().clearSavedLocation(SavedLocationType.MONSTER_CARNIVAL);
                MapleMap map = c.getChannelServer().getMapFactory().getMap(100000000);
                c.getPlayer().changeMap(map, map.getPortal(0));
                c.getPlayer().dropMessage(5, "卡圖解救成功！");
            } else {
                c.getPlayer().dropMessage(1, "你並沒有卡圖啊。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@stocked - 解除卡圖").toString();
        }
    }*/
   public static class CGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted[1].length() < 2) {
                return false;
            }
            String say = StringUtil.joinStringFrom(splitted, 1);
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因為你自己是GM所法使用此指令,可以嘗試!cngm <訊息> 來建立GM聊天頻道~");
            } else if (!c.getPlayer().getCheatTracker().GMSpam(60000, 1)) { // 1 minutes.
                boolean fake = false;
                if (BlackConfig.getBlackList().containsKey(c.getAccID())) {
                    fake = true;
                }
                c.getPlayer().dropMessage(6, "訊息已經寄送給GM了!");
                if (!fake) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[管理員幫幫忙] 頻道 " + c.getPlayer().getClient().getChannel() + " 玩家 " + c.getPlayer().getName() + " : " + say).getBytes());
                    //System.out.println("[管理員幫幫忙] " + c.getPlayer().getName() + " : " + StringUtil.joinStringFrom(splitted, 1));
                    FilePrinter.print("管理員幫幫忙.txt", FilePrinter.getLocalDateString() + " 玩家[" + c.getPlayer().getName() + "] 帳號[" + c.getAccountName() + "]: " + say);
                }
            } else {
                c.getPlayer().dropMessage(6, "為了防止對GM刷屏所以每1分鐘只能發一次.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@cgm - 跟GM回報").toString();
        }
    }
}
