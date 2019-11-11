package client.messages.commands;

import client.ISkill;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.inventory.ModifyInventory;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.world.World;
import handling.world.CheaterData;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.EventManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShopFactory;
import server.ShutdownServer;
import server.Timer.EventTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MobSkillFactory;
import server.life.OverrideMonsterStats;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import server.quest.MapleQuest;
import tools.ArrayMap;
import tools.CPUSampler;
import tools.MaplePacketCreator;
import tools.MockIOSession;
import tools.Pair;
import tools.StringUtil;
import tools.packet.MobPacket;
import java.util.concurrent.ScheduledFuture;
import scripting.NPCScriptManager;
import server.ServerProperties;
import handling.login.LoginServer;
import handling.world.MapleAntiMacro;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import server.CashItemFactory;
import server.FishingRewardFactory;
import server.ServerConfig;
import server.Timer;
import server.events.MapleOxQuizFactory;
import server.gashapon.GashaponFactory;
import server.life.CustomNPC;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Emilyx3
 */
public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class DEBUG extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (c.getPlayer() != null) {
                c.getPlayer().setShowDebugInfo(!c.getPlayer().isShowDebugInfo());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!debbug - 顯示DEBUG訊息").toString();
        }

    }

    public static class antiMacro extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (c.getPlayer() != null) {
                if (!MapleAntiMacro.startAntiMacro(c.getPlayer(), c.getPlayer(), (byte) MapleAntiMacro.GM_SKILL_ANTI)) {
                    c.getPlayer().dropMessage(5, "測謊測試失敗");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!antiMacro - 測謊測試").toString();
        }

    }

    public static class 聊天紀錄存檔 extends logChat {

        @Override
        public String getMessage() {
            return new StringBuilder().append("!聊天紀錄存檔 - 聊天紀錄存檔").toString();
        }
    }

    public static class logChat extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConfig.setLogChat(!ServerConfig.isLogChat());
            c.getPlayer().dropMessage("聊天紀錄存檔已" + (ServerConfig.isLogChat() ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!logChat - 聊天紀錄存檔").toString();
        }
    }

    public static class SavePlayerShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
                cserv.closeAllMerchant();
            }
            c.getPlayer().dropMessage(6, "精靈商人儲存完畢.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!savePlayerShops - 儲存精靈商人").toString();
        }
    }

    public static class Shutdown extends CommandExecute {

        private static Thread t = null;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, "關閉伺服器中...");
            if (t == null || !t.isAlive()) {
                t = new Thread(server.ShutdownServer.getInstance());
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "已在執行中...");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdown - 關閉伺服器").toString();
        }
    }

    public static class ShutdownTime extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;
        private static Thread t = null;

        public boolean execute(final MapleClient c, String splitted[]) {

            if (splitted.length < 2) {
                return false;
            }
            minutesLeft = Integer.parseInt(splitted[1]);
            LoginServer.adminOnly = true;
            c.getPlayer().dropMessage(6, "已經開啟管理員模式。");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if ((minutesLeft > 0 && minutesLeft <= 11) && !World.isShutDown) {
                            World.isShutDown = true;
                            c.getPlayer().dropMessage(6, "已經限制玩家玩家所有行動。");
                        } else if (minutesLeft == 0) {
                            ShutdownServer.getInstance().run();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        StringBuilder message = new StringBuilder();
                        message.append("[楓之谷公告] 伺服器將在 ");
                        message.append(minutesLeft);
                        message.append("分鐘後關閉. ");
                        World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice(message.toString()).getBytes());
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(message.toString()).getBytes());
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, new StringBuilder().append("伺服器關閉時間修改為 ")
                        .append(minutesLeft).append("分鐘後，清稍等伺服器關閉").toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdowntime <分鐘> - 關閉伺服器").toString();
        }
    }

    public static class copyAll extends CommandExecute {

        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            if (splitted.length < 2) {
                return false;
            }

            victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                player.dropMessage("找不到該玩家");
                return true;
            }
            c.getPlayer().clearSkills();
            c.getPlayer().unequips();
            c.getPlayer().setStr(victim.getStr());
            c.getPlayer().setDex(victim.getDex());
            c.getPlayer().setInt(victim.getInt());
            c.getPlayer().setLuk(victim.getLuk());

            c.getPlayer().setMeso(victim.getMeso());
            c.getPlayer().setLevel1(victim.getLevel());
            c.getPlayer().changeJob(victim.getJob());

            c.getPlayer().setHp(victim.getHp());
            c.getPlayer().setMp(victim.getMp());
            c.getPlayer().setMaxHp(victim.getMaxHp());
            c.getPlayer().setMaxMp(victim.getMaxMp());

            String normal = victim.getName();
            String after = (normal + "x2");
            if (after.length() <= 12) {
                c.getPlayer().setName(victim.getName() + "x2");
            }
            c.getPlayer().setRemainingAp(victim.getRemainingAp());
            c.getPlayer().setRemainingSp(victim.getRemainingSp());
            c.getPlayer().LearnSameSkill(victim);

            c.getPlayer().setFame(victim.getFame());
            c.getPlayer().setHair(victim.getHair());
            c.getPlayer().setFace(victim.getFace());

            c.getPlayer().setSkinColor(victim.getSkinColor() == 0 ? c.getPlayer().getSkinColor() : victim.getSkinColor());

            c.getPlayer().setGender(victim.getGender());

            for (IItem ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                IItem eq = ii.copy();
                eq.setPosition(eq.getPosition());
                eq.setQuantity((short) 1);
                c.getPlayer().forceReAddItem_NoUpdate(eq, MapleInventoryType.EQUIPPED);
            }
            c.getPlayer().fakeRelog();
            c.getPlayer().dropMessage(5, "複製人完成 新名字:" + c.getPlayer().getName());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!copyall 玩家名稱 - 複製玩家").toString();
        }
    }

    public static class uneq extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter victim;
            victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 1) {
                return false;
            }
            if (victim == null) {
                c.getPlayer().dropMessage("找不到該角色");
            }
            victim.unequips();
            c.getPlayer().dropMessage(5, "已經成功把該角色:" + victim.getName() + "脫個精光！");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!uneq [角色名字] - 脫人裝備").toString();
        }
    }

    public static class copyInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            int type = 1;
            victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                player.dropMessage("找不到該玩家");
                return true;
            }
            try {
                type = Integer.parseInt(splitted[2]);
            } catch (Exception ex) {
            }
            if (type == 0) {
                for (client.inventory.IItem ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                    client.inventory.IItem n = ii.copy();
                    player.getInventory(MapleInventoryType.EQUIP).addItem(n);
                }
                player.fakeRelog();
            } else {
                MapleInventoryType types;
                if (type == 1) {
                    types = MapleInventoryType.EQUIP;
                } else if (type == 2) {
                    types = MapleInventoryType.USE;
                } else if (type == 3) {
                    types = MapleInventoryType.ETC;
                } else if (type == 4) {
                    types = MapleInventoryType.SETUP;
                } else if (type == 5) {
                    types = MapleInventoryType.CASH;
                } else {
                    types = null;
                }
                if (types == null) {
                    c.getPlayer().dropMessage("發生錯誤");
                    return true;
                }
                int[] equip = new int[97];
                for (int i = 1; i < 97; i++) {
                    if (victim.getInventory(types).getItem((short) i) != null) {
                        equip[i] = i;
                    }
                }
                for (int i = 0; i < equip.length; i++) {
                    if (equip[i] != 0) {
                        IItem n = victim.getInventory(types).getItem((short) equip[i]).copy();
                        player.getInventory(types).addItem(n);
                        c.sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.ADD, n)));
                    }
                }
            }
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!copyinv 玩家名稱 裝備欄位(0 = 裝備中 1=裝備欄 2=消耗欄 3=其他欄 4=裝飾欄 5=點數欄)(預設裝備欄) - 複製玩家道具").toString();
        }
    }

    public static class 改名字 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            String after = splitted[2];
            MapleCharacter victim;
            victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 2) {
                return false;
            }
            if (victim == null) {
                c.getPlayer().dropMessage(6, "找不到該玩家");
                return true;
            }
            if (after.length() <= 12) {
                victim.setName(splitted[2]);
                victim.fakeRelog();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!改名字 [別人名字] [新名字] - 改角色名字").toString();
        }
    }

    public static class 高級檢索 extends CommandExecute {

        public boolean execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().start(c, 9010000, "AdvancedSearch");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!高級檢索 - 各種功能檢索功能").toString();
        }
    }

    public static class SaveAll extends CommandExecute {

        private int p = 0;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (MapleCharacter chr : chrs) {
                    p++;
                    chr.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage("[保存] " + p + "個玩家數據保存到數據中.");
            p = 0;
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!saveall - 儲存所有角色資料").toString();
        }
    }

    public static class LowHP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getStat().setHp((short) 1);
            c.getPlayer().getStat().setMp((short) 1);
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lowhp - 血魔歸ㄧ").toString();
        }
    }

    public static class Heal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp());
            c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!heal - 補滿血魔").toString();
        }
    }

    public static class Kill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            for (int i = 1; i < splitted.length; i++) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[i]);
                if (victim == null) {
                    c.getPlayer().dropMessage(6, "[kill] 玩家 " + splitted[i] + " 不存在.");
                } else if (player.allowedToTarget(victim)) {
                    victim.getStat().setHp((short) 0);
                    victim.getStat().setMp((short) 0);
                    victim.updateSingleStat(MapleStat.HP, 0);
                    victim.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!kill <玩家名稱1> <玩家名稱2> ...  - 殺掉玩家").toString();
        }
    }

    public static class Skill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!skill <技能ID> [技能等級] [技能最大等級] ...  - 學習技能").toString();
        }
    }

    public static class Fame extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            short fame = 0;
            try {
                fame = Short.parseShort(splitted[2]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(6, "不合法的數字");
                return false;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            } else {
                c.getPlayer().dropMessage(6, "[fame] 角色不存在");
            }
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!fame <角色名稱> <名聲> ...  - 名聲").toString();
        }
    }

    public static class autoreg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            LoginServer.AutoRegister = !LoginServer.AutoRegister;
            c.getPlayer().dropMessage(0, "[autoreg] " + (LoginServer.AutoRegister ? "開啟" : "關閉"));
            System.out.println("[autoreg] " + (LoginServer.AutoRegister ? "開啟" : "關閉"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!autoreg  - 自動註冊開關").toString();
        }
    }

    public static class logindoor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            LoginServer.adminOnly = !LoginServer.adminOnly;
            c.getPlayer().dropMessage(0, "[logindoor] " + (LoginServer.adminOnly ? "開啟" : "關閉"));
            System.out.println("[logindoor] " + (LoginServer.adminOnly ? "開啟" : "關閉"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!logindoor  - 管理員登入模式開關").toString();
        }
    }

    public static class 禁止玩家使用 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            World.isShutDown = !World.isShutDown;
            c.getPlayer().dropMessage(0, "[禁止玩家使用] " + (World.isShutDown ? "開啟" : "關閉"));
            System.out.println("[禁止玩家使用] " + (World.isShutDown ? "開啟" : "關閉"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!logindoor  - 管理員登入模式開關").toString();
        }
    }

    public static class HealMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            for (MapleCharacter mch : player.getMap().getCharacters()) {
                if (mch != null) {
                    mch.getStat().setHp(mch.getStat().getMaxHp());
                    mch.updateSingleStat(MapleStat.HP, mch.getStat().getMaxHp());
                    mch.getStat().setMp(mch.getStat().getMaxMp());
                    mch.updateSingleStat(MapleStat.MP, mch.getStat().getMaxMp());
                }
            }
            return true;

        }

        public String getMessage() {
            return new StringBuilder().append("!healmap  - 治癒地圖上所有的人").toString();
        }
    }

    public static class GodMode extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "無敵已經關閉");
            } else {
                player.setInvincible(true);
                player.dropMessage(6, "無敵已經開啟.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!godmode  - 無敵開關").toString();
        }
    }

    public static class GiveSkill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);

            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            victim.changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!giveskill <玩家名稱> <技能ID> [技能等級] [技能最大等級] - 給予技能").toString();
        }
    }

    public static class SP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.sendPacket(MaplePacketCreator.updateSp(c.getPlayer(), false));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sp [數量] - 增加SP").toString();
        }
    }

    public static class AP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setRemainingAp((short) CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            final List<Pair<MapleStat, Integer>> statupdate = new ArrayList<>();
            c.sendPacket(MaplePacketCreator.updateAp(c.getPlayer(), false));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ap [數量] - 增加AP").toString();
        }
    }

    public static class openNpc extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int npcid = 0;
            try {
                npcid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc != null) {
                NPCScriptManager.getInstance().start(c, npcid);
            } else {
                c.getPlayer().dropMessage(6, "未知NPC");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!openNpc [npcID] - 呼叫NPC").toString();
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = 0;
            try {
                shopId = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            } else {
                c.getPlayer().dropMessage(5, "此商店ID不存在");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shop [ShopID] - 開啟商店").toString();
        }
    }

    public static class 關鍵時刻 extends CommandExecute {

        protected static ScheduledFuture<?> ts = null;

        @Override
        public boolean execute(final MapleClient c, String splitted[]) {
            if (splitted.length < 1) {
                return false;
            }
            if (ts != null) {
                ts.cancel(false);
                c.getPlayer().dropMessage(0, "原定的關鍵時刻已取消");
            }
            int minutesLeft = 0;
            try {
                minutesLeft = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (minutesLeft > 0) {
                ts = EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                                if (mch.getLevel() >= 29) {
                                    NPCScriptManager.getInstance().start(mch.getClient(), 9010010, "CrucialTime");
                                }
                            }
                        }
                        World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("關鍵時刻開放囉，沒有30等以上的玩家是得不到的。").getBytes());
                        ts.cancel(false);
                        ts = null;
                    }
                }, minutesLeft * 60000);
                c.getPlayer().dropMessage(0, "關鍵時刻預定已完成");
            } else {
                c.getPlayer().dropMessage(0, "設定的時間必須 > 0。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!關鍵時刻 <時間:分鐘> - 關鍵時刻").toString();
        }
    }

    public static class 給人點數 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 4) {
                return false;
            }
            int nx = 0;
            String po = "";
            if (splitted[1].equalsIgnoreCase("點數")) {
                nx = 1;
                po = "Gash";
            } else if (splitted[1].equalsIgnoreCase("楓點")) {
                nx = 2;
                po = "楓葉";
            }
            String name = splitted[2];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return false;
            }
            MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到此玩家");
            } else {
                victim.modifyCSPoints(nx, Integer.parseInt(splitted[3]), true);
                c.getPlayer().dropMessage(6, "成功給予 " + victim.getName() + splitted[3] + "點 " + po + "點數");
            }

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!給人點數 點數/楓點 玩家名稱 數量").toString();
        }
    }

    public static class 給點數 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int nx = 0;
            String po = "";
            if (splitted[1].equalsIgnoreCase("點數")) {
                nx = 1;
                po = "Gash";
            } else if (splitted[1].equalsIgnoreCase("楓點")) {
                nx = 2;
                po = "楓葉";
            }
            c.getPlayer().modifyCSPoints(nx, Integer.parseInt(splitted[2]), true);
            c.getPlayer().dropMessage(6, "成功獲得" + splitted[2] + "點 " + po + "點數");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!給點數 點數/楓點 數量").toString();
        }
    }

    public static class 給贊助點 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().setPoints(c.getPlayer().getPoints() + Integer.parseInt(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!給贊助點 <數量> - 取得贊助點").toString();
        }
    }

    public static class 給投票點 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().setVPoints(c.getPlayer().getVPoints() + Integer.parseInt(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!給投票點 <數量> - 取得VPoint").toString();
        }
    }

    public static class UnlockInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            java.util.Map<IItem, MapleInventoryType> eqs = new ArrayMap<>();
            boolean add = false;
            if (splitted.length < 2 || splitted[1].equals("全部")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (IItem item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.LOCK.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "已經解鎖");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "已經解鎖");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            } else if (splitted[1].equals("已裝備道具")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("武器")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("消耗")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.USE);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("裝飾")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("其他")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("特殊")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            } else {
                return false;
            }

            for (Entry<IItem, MapleInventoryType> eq : eqs.entrySet()) {
                c.getPlayer().forceReAddItem_NoUpdate(eq.getKey().copy(), eq.getValue());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unlockinv <全部/已裝備道具/武器/消耗/裝飾/其他/特殊> - 解鎖道具").toString();
        }
    }

    public static class serverMsg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                for (ChannelServer ch : ChannelServer.getAllInstances()) {
                    ch.setServerMessage(sb.toString());
                }
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(sb.toString()).getBytes());
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!servermsg 訊息 - 更改上方黃色公告").toString();
        }
    }

    public static class Say extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice(sb.toString()).getBytes());
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!say 訊息 - 伺服器公告").toString();
        }
    }

    public static class Letter extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "指令規則: ");
                return false;
            }
            int start, nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted[1].equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().dropMessage(6, "未知的顏色!");
                return true;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            List<Integer> chars = new ArrayList<Integer>();
            splitString = splitString.toUpperCase();
            // System.out.println(splitString);
            for (int i = 0; i < splitString.length(); i++) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                } else if ((int) (chr) >= (int) 'A' && (int) (chr) <= (int) 'Z') {
                    chars.add((int) (chr));
                } else if ((int) (chr) >= (int) '0' && (int) (chr) <= (int) ('9')) {
                    chars.add((int) (chr) + 200);
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * w);
            for (Integer i : chars) {
                if (i == -1) {
                    dStart += w;
                } else if (i < 200) {
                    int val = start + i - (int) ('A');
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                } else if (i >= 200 && i <= 300) {
                    int val = nstart + i - (int) ('0') - 200;
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(" !letter <color (green/red)> <word> - 送信").toString();
        }

    }

    public static class 離婚 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 1) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return false;
            } else if (victim.getMarriageId() != 1) {
                c.getPlayer().dropMessage(6, victim.getName() + "離婚失敗！");
                return false;
            }
            victim.setMarriageId(0);
            victim.reloadC();
            victim.dropMessage(5, "離婚成功！");
            c.getPlayer().dropMessage(6, victim.getName() + "離婚成功！");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!離婚 <玩家名稱> - 離婚").toString();
        }
    }

    public static class 結婚 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "錯誤的戒指ID.");
            } else {
                MapleCharacter fff = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "玩家必須上線");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        MapleCharacter[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "錯誤的戒指ID.");
                                return true;
                            } else {
                                eq.setUniqueId(ringID[i]);
                                MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                                chrz[i].dropMessage(6, "成功與  " + chrz[i == 0 ? 1 : 0].getName() + " 結婚");
                            }
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!結婚 <玩家名稱> <戒指ID> - 結婚").toString();
        }
    }

    public static class ItemCheck extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3 || splitted[1] == null || splitted[1].equals("") || splitted[2] == null || splitted[2].equals("")) {
                return false;
            } else {
                int item = Integer.parseInt(splitted[2]);
                MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                int itemamount = chr.getItemQuantity(item, true);
                if (itemamount > 0) {
                    c.getPlayer().dropMessage(6, chr.getName() + " 有 " + itemamount + " (" + item + ").");
                } else {
                    c.getPlayer().dropMessage(6, chr.getName() + " 並沒有 (" + item + ")");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!itemcheck <playername> <itemid> - 檢查物品").toString();
        }
    }

    public static class MobVac extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (final MapleMapObject mmo : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
                final MapleMonster monster = (MapleMonster) mmo;
                c.getPlayer().getMap().broadcastMessage(MobPacket.moveMonster(false, -1, 0, monster.getObjectId(), monster.getPosition(), c.getPlayer().getPosition(), c.getPlayer().getLastRes()));
                monster.setPosition(c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mobvac - 全圖吸怪").toString();
        }
    }

    public static class ItemVac extends CommandExecute {

        public boolean execute(MapleClient c, String splitted[]) {
            boolean ItemVac = c.getPlayer().getItemVac();
            if (ItemVac == false) {
                c.getPlayer().stopItemVac();
                c.getPlayer().startItemVac();
            } else {
                c.getPlayer().stopItemVac();
            }
            c.getPlayer().dropMessage(6, "目前自動撿物狀態:" + (ItemVac == false ? "開啟" : "關閉"));
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mobvac - 全圖吸物開關").toString();
        }
    }

    public static class TimerStatus extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            StringBuilder sb = new StringBuilder();
            sb.append("BoatTimer : ").append(Timer.BoatTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("EventTimer : ").append(Timer.EventTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("BuffTimer : ").append(Timer.BuffTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("CheatTimer : ").append(Timer.CheatTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("CloneTimer : ").append(Timer.CloneTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("EtcTimer : ").append(Timer.EtcTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("LoginTimer : ").append(Timer.LoginTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("MapTimer : ").append(Timer.MapTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("PingTimer : ").append(Timer.PingTimer.getInstance().getQueueTaskCount()).append("\n");
            sb.append("WorldTimer : ").append(Timer.WorldTimer.getInstance().getQueueTaskCount()).append("\n");

            c.getPlayer().dropMessage(sb.toString());

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!TimerStatus - TimerStatus").toString();
        }

    }

    public static class Song extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!song - 播放音樂").toString();
        }
    }

    public static class 開啟自動活動 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!開啟自動活動 - 開啟自動活動").toString();
        }
    }

    public static class 活動開始 extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private int min = 1, sec = 0;

        @Override
        public boolean execute(final MapleClient c, String splitted[]) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                if (c.getPlayer().getMapId() == 109020001) {
                    sec = 10;
                    c.getPlayer().dropMessage(5, "已經關閉活動入口，１０秒後開始活動。");
                    World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("頻道:" + c.getChannel() + "活動目前已經關閉大門口，１０秒後開始活動。").getBytes());
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(sec));
                } else {
                    sec = 60;
                    c.getPlayer().dropMessage(5, "已經關閉活動入口，６０秒後開始活動。");
                    World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("頻道:" + c.getChannel() + "活動目前已經關閉大門口，６０秒後開始活動。").getBytes());
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(sec));
                }
                ts = EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if (min == 0) {
                            MapleEvent.onStartEvent(c.getPlayer());
                            ts.cancel(false);
                            return;
                        }
                        min--;
                    }
                }, sec * 1000);
                return true;
            } else {
                c.getPlayer().dropMessage(5, "您必須先使用 !選擇活動 設定當前頻道的活動，並在當前頻道活動地圖裡使用。");
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!活動開始 - 活動開始").toString();
        }
    }

    public static class 選擇活動 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("目前開放的活動有: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!選擇活動 - 選擇活動").toString();
        }
    }

    public static class CheckGash extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().dropMessage(5, "找不到該角色");
            } else {
                c.getPlayer().dropMessage(6, chrs.getName() + " 有 " + chrs.getCSPoints(1) + " 點數.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!checkgash <玩家名稱> - 檢查點數").toString();
        }
    }

    public static class RemoveItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "此玩家並不存在");
            } else {
                chr.removeAll(Integer.parseInt(splitted[2]));
                c.getPlayer().dropMessage(6, "所有ID為 " + splitted[2] + " 的道具已經從 " + splitted[1] + " 身上被移除了");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removeitem <角色名稱> <物品ID> - 移除玩家身上的道具").toString();
        }
    }

    public static class RemoveItemOff extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            Connection dcon = (Connection) DatabaseConnection.getConnection();
            try {
                int id = 0, quantity = 0;
                String name = splitted[2];
                int item = Integer.parseInt(splitted[1]);
                com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("select * from characters where name = ?");
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        id = rs.getInt("id");
                    }
                }
                if (id == 0) {
                    c.getPlayer().dropMessage(5, "角色不存在資料庫。");
                    return false;
                }
                com.mysql.jdbc.PreparedStatement ps2 = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("select * from inventoryitems WHERE itemid = ? and quantity");
                ps2.setInt(1, item);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        quantity = rs.getInt("quantity");
                    }
                }
                if (quantity == 0) {
                    c.getPlayer().dropMessage(5, "物品不存在資料庫。");
                    return false;
                }
                com.mysql.jdbc.PreparedStatement ps3 = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("delete from inventoryitems WHERE itemid = ? and characterid = ?");
                ps3.setInt(1, item);
                ps3.setInt(2, id);
                ps3.executeUpdate();
                c.getPlayer().dropMessage(6, "ID為: " + item + " 的道具 數量: x" + quantity + "個已經從 " + name + " 身上被移除了");
                ps.close();
                ps2.close();
                ps3.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removeitem <物品ID> <角色名稱> - 移除玩家身上的道具").toString();
        }
    }

    public static class LockItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "此玩家並不存在");
            } else {
                int itemid = Integer.parseInt(splitted[2]);
                MapleInventoryType type = GameConstants.getInventoryType(itemid);
                for (IItem item : chr.getInventory(type).listById(itemid)) {
                    item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                    chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.UPDATE, item)));
                }
                if (type == MapleInventoryType.EQUIP) {
                    type = MapleInventoryType.EQUIPPED;
                    for (IItem item : chr.getInventory(type).listById(itemid)) {
                        item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                        chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.UPDATE, item)));
                    }
                }
                c.getPlayer().dropMessage(6, "玩家 " + splitted[1] + "身上所有ID為 " + splitted[2] + " 的道具已經從鎖定了");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lockitem <角色名稱> <物品ID> - 上鎖玩家身上的道具").toString();
        }
    }

    public static class KillMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.getStat().setHp((short) 0);
                    map.getStat().setMp((short) 0);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmap - 殺掉所有玩家").toString();
        }
    }

    public static class 取消補助 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.cancelAllBuffs();
                    map.dropMessage(5, "系統已幫您把所有BUFF取消。");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!取消補助 - 清理地圖上玩家的Buff").toString();
        }
    }

    public static class 收起寵物 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.unequipAllPets();
                    map.dropMessage(5, "系統已幫您收起寵物。");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!收起寵物 - 清理地圖上玩家的Buff").toString();
        }
    }

    public static class 加入公會 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length != 2) {
                return false;
            }
            Connection dcon = (Connection) DatabaseConnection.getConnection();
            try {
                com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
                ps.setString(1, splitted[1]);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (c.getPlayer().getGuildId() > 0) {
                        try {
                            World.Guild.leaveGuild(c.getPlayer().getMGC());
                        } catch (Exception e) {
                            c.sendPacket(MaplePacketCreator.getErrorNotice("無法連接到世界伺服器，請稍後再嘗試。"));
                            return false;
                        }
                        c.sendPacket(MaplePacketCreator.showGuildInfo(null));

                        c.getPlayer().setGuildId(0);
                        c.getPlayer().saveGuildStatus();
                    }
                    c.getPlayer().setGuildId(rs.getInt("guildid"));
                    c.getPlayer().setGuildRank((byte) 2); // 副會長
                    try {
                        World.Guild.addGuildMember(c.getPlayer().getMGC());
                    } catch (Exception e) {
                    }
                    c.sendPacket(MaplePacketCreator.showGuildInfo(c.getPlayer()));
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.removePlayerFromMap(c.getPlayer().getId()), false);
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.spawnPlayerMapobject(c.getPlayer()), false);
                    c.getPlayer().saveGuildStatus();
                } else {
                    c.getPlayer().dropMessage(6, "公會名稱不存在。");
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!加入公會 [公會名字] - 強制加入公會").toString();
        }
    }

    public static class SpeakMega extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter victim = null;
            if (splitted.length >= 2) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            }
            try {
                World.Broadcast.broadcastSmega(MaplePacketCreator.getSuperMegaphone(victim == null ? splitted[1] : victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true, victim == null ? c.getChannel() : victim.getClient().getChannel()).getBytes());
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakmega [玩家名稱] <訊息> - 對某個玩家的頻道進行廣播").toString();
        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到 '" + splitted[1]);
                return false;
            } else {
                victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speak <玩家名稱> <訊息> - 對某個玩家傳訊息").toString();
        }
    }

    public static class SpeakMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakmap <訊息> - 對目前地圖進行傳送訊息").toString();
        }

    }

    public static class SpeakChannel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <訊息> - 對目前頻道進行傳送訊息").toString();
        }

    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <訊息> - 對目前伺服器進行傳送訊息").toString();
        }
    }

    public static class 異常 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "");
                return false;
            }
            int type = 0;
            MapleDisease dis = null;
            if (splitted[1].equalsIgnoreCase("封印")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("黑暗")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("虛弱")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("暈眩")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("詛咒")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("中毒")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("緩慢")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("誘惑")) {
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("相反")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("不死")) {
                type = 133;
            } else {
                return false;
            }
            dis = MapleDisease.getByMobSkill(type);
            if (splitted.length == 4) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[2]);
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "找不到此玩家");
                } else {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.getDiseaseBuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1)));
                }
            } else {
                for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.getDiseaseBuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1)));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!異常 <封印/黑暗/虛弱/暈眩/詛咒/中毒/緩慢/誘惑/相反/不死> [角色名稱] <狀態等級> - 讓人得到特殊狀態").toString();
        }

    }

    public static class SendAllNote extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length >= 1) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sendallnote <文字> 傳送Note給目前頻道的所有人").toString();
        }
    }

    public static class giveMeso extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到 '" + splitted[1]);
            } else {
                victim.gainMeso(Integer.parseInt(splitted[2]), true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainmeso <名字> <數量> - 給玩家楓幣").toString();
        }
    }

    public static class MesoEveryone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(Integer.parseInt(splitted[1]), true);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesoeveryone <數量> - 給所有玩家楓幣").toString();
        }
    }

    public static class CloneMe extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().cloneLook();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cloneme - 產生克龍體").toString();
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "個克龍體消失了.");
            c.getPlayer().disposeClones();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!disposeclones - 摧毀克龍體").toString();
        }
    }

    public static class Monitor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
                } else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
                }
            } else {
                c.getPlayer().dropMessage(5, "找不到該玩家");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!monitor <玩家> - 記錄玩家資訊").toString();
        }
    }

    public static class PermWeather extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeMapEffect());
                c.getPlayer().dropMessage(5, "Map weather has been disabled.");
            } else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().dropMessage(5, "Invalid ID.");
                } else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.startMapEffect("", weather, false));
                    c.getPlayer().dropMessage(5, "Map weather has been enabled.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!permweather - 設定天氣").toString();

        }
    }

    public static class CharInfo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length < 2) {
                return false;
            }
            final StringBuilder builder = new StringBuilder();
            final MapleCharacter other = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (other == null) {
                builder.append("角色不存在");
                c.getPlayer().dropMessage(6, builder.toString());
            } else {
                if (other.getClient().getLastPing() <= 0) {
                    other.getClient().sendPing();
                }
                builder.append(MapleClient.getLogMessage(other, ""));
                builder.append(" 在 ").append(other.getPosition().x);
                builder.append(" /").append(other.getPosition().y);

                builder.append(" || 血量 : ");
                builder.append(other.getStat().getHp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxHp());

                builder.append(" || 魔量 : ");
                builder.append(other.getStat().getMp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxMp());

                builder.append(" || 物理攻擊力 : ");
                builder.append(other.getStat().getTotalWatk());
                builder.append(" || 魔法攻擊力 : ");
                builder.append(other.getStat().getTotalMagic());
                builder.append(" || 最高攻擊 : ");
                builder.append(other.getStat().getCurrentMaxBaseDamage());
                builder.append(" || 攻擊%數 : ");
                builder.append(other.getStat().dam_r);
                builder.append(" || BOSS攻擊%數 : ");
                builder.append(other.getStat().bossdam_r);

                builder.append(" || 力量 : ");
                builder.append(other.getStat().getStr());
                builder.append(" || 敏捷 : ");
                builder.append(other.getStat().getDex());
                builder.append(" || 智力 : ");
                builder.append(other.getStat().getInt());
                builder.append(" || 幸運 : ");
                builder.append(other.getStat().getLuk());

                builder.append(" || 全部力量 : ");
                builder.append(other.getStat().getTotalStr());
                builder.append(" || 全部敏捷 : ");
                builder.append(other.getStat().getTotalDex());
                builder.append(" || 全部智力 : ");
                builder.append(other.getStat().getTotalInt());
                builder.append(" || 全部幸運 : ");
                builder.append(other.getStat().getTotalLuk());

                builder.append(" || 經驗值 : ");
                builder.append(other.getExp());

                builder.append(" || 組隊狀態 : ");
                builder.append(other.getParty() != null);

                builder.append(" || 交易狀態: ");
                builder.append(other.getTrade() != null);
                builder.append(" || Latency: ");
                builder.append(other.getClient().getLatency());
                builder.append(" || 最後PING: ");
                builder.append(other.getClient().getLastPing());
                builder.append(" || 最後PONG: ");
                builder.append(other.getClient().getLastPong());
                builder.append(" || IP: ");
                builder.append(other.getClient().getSessionIPAddress());
                other.getClient().DebugMessage(builder);

                c.getPlayer().dropMessage(6, builder.toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!charinfo <角色名稱> - 查看角色狀態").toString();

        }
    }

    public static class whoishere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            StringBuilder builder = new StringBuilder("在此地圖的玩家: ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) { // wild guess :o
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!whoishere - 查看目前地圖上的玩家").toString();

        }
    }

    public static class Cheaters extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            List<CheaterData> cheaters = World.getCheaters();
            for (int x = cheaters.size() - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cheaters - 查看作弊角色").toString();

        }
    }

    public static class Connected extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            java.util.Map<Integer, Integer> connected = World.getConnected();
            StringBuilder conStr = new StringBuilder("已連接的客戶端: ");
            boolean first = true;
            for (int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                } else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("所有: ");
                    conStr.append(connected.get(i));
                } else {
                    conStr.append("頻道 ");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!connected - 查看已連線的客戶端").toString();

        }
    }

    public static class ResetQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!resetquest <任務ID> - 重置任務").toString();

        }
    }

    public static class StartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!startquest <任務ID> - 開始任務").toString();

        }
    }

    public static class CompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!completequest <任務ID> - 完成任務").toString();

        }
    }

    public static class FStartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), splitted.length >= 4 ? splitted[3] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fstartquest <任務ID> - 強制開始任務").toString();

        }
    }

    public static class FCompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fcompletequest <任務ID> - 強制完成任務").toString();

        }
    }

    public static class FStartOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), splitted.length >= 4 ? splitted[4] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fstartother - 不知道啥").toString();

        }
    }

    public static class FCompleteOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fcompleteother - 不知道啥").toString();

        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MaplePortal portal = c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!nearestportal - 不知道啥").toString();

        }
    }

    public static class SpawnDebug extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawndebug - debug怪物出生").toString();

        }
    }

    public static class Threads extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().contains(filter.toLowerCase())) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!threads - 查看Threads資訊").toString();

        }
    }

    public static class ShowTrace extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!showtrace - show trace info").toString();

        }
    }

    public static class FakeRelog extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            c.sendPacket(MaplePacketCreator.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fakerelog - 假登出再登入").toString();

        }
    }

    public static class ToggleOffense extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;

            }
            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted[1] + " not found");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!toggleoffense <Offense> - 開啟或關閉CheatOffense").toString();

        }
    }

    public static class toggleDrop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().toggleDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!toggledrop - 開啟或關閉掉落").toString();

        }
    }

    public static class ToggleMegaphone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "廣播禁用 : " + (c.getChannelServer().getMegaphoneMuteState() ? "啟用" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!togglemegaphone - 開啟或關閉廣播").toString();

        }
    }

    public static class SpawnReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleReactorStats reactorSt = MapleReactorFactory.getReactor(Integer.parseInt(splitted[1]));
            MapleReactor reactor = new MapleReactor(reactorSt, Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawnreactor - 設立Reactor").toString();

        }
    }

    public static class HReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - 觸碰Reactor").toString();

        }
    }

    public static class DestroyReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (MapleMapObject reactorL : reactors) {
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!drstroyreactor - 移除Reactor").toString();

        }
    }

    public static class ResetReactors extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetReactors();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!resetreactors - 重置此地圖所有的Reactor").toString();

        }
    }

    public static class SetReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - 觸碰Reactor").toString();

        }
    }

    public static class RemoveDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(5, "Cleared " + c.getPlayer().getMap().getNumItems() + " drops");
            c.getPlayer().getMap().removeDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removedrops - 移除地上的物品").toString();

        }
    }

    public static class ExpRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                } else {
                    c.getChannelServer().setExpRate(rate);
                }
                c.getPlayer().dropMessage(6, "Exprate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!exprate <倍率> - 更改經驗備率").toString();

        }
    }

    public static class DropRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(rate);
                    }
                } else {
                    c.getChannelServer().setDropRate(rate);
                }
                c.getPlayer().dropMessage(6, "Drop Rate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!droprate <倍率> - 更改掉落備率").toString();

        }
    }

    public static class MesoRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                } else {
                    c.getChannelServer().setMesoRate(rate);
                }
                c.getPlayer().dropMessage(6, "Meso Rate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesorate <倍率> - 更改金錢備率").toString();

        }
    }

    public static class CashRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setCashRate(rate);
                    }
                } else {
                    c.getChannelServer().setCashRate(rate);
                }
                c.getPlayer().dropMessage(6, "點數倍率已經改為 " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cashrate <倍率> - 更改Gash備率").toString();

        }
    }

    public static class DCAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }
            if (range == -1) {
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll();
            } else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll(true);
            } else if (range == 2) {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dcall [m|c|w] - 所有玩家斷線").toString();

        }
    }

    public static class GoTo extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
            gotomaps.put("gmmap", 180000000);
            gotomaps.put("southperry", 2000000);
            gotomaps.put("amherst", 1010000);
            gotomaps.put("henesys", 100000000);
            gotomaps.put("ellinia", 101000000);
            gotomaps.put("perion", 102000000);
            gotomaps.put("kerning", 103000000);
            gotomaps.put("lithharbour", 104000000);
            gotomaps.put("sleepywood", 105040300);
            gotomaps.put("florina", 110000000);
            gotomaps.put("orbis", 200000000);
            gotomaps.put("happyville", 209000000);
            gotomaps.put("elnath", 211000000);
            gotomaps.put("ludibrium", 220000000);
            gotomaps.put("aquaroad", 230000000);
            gotomaps.put("leafre", 240000000);
            gotomaps.put("mulung", 250000000);
            gotomaps.put("herbtown", 251000000);
            gotomaps.put("omegasector", 221000000);
            gotomaps.put("koreanfolktown", 222000000);
            gotomaps.put("newleafcity", 600000000);
            gotomaps.put("sharenian", 990000000);
            gotomaps.put("pianus", 230040420);
            gotomaps.put("horntail", 240060200);
            gotomaps.put("chorntail", 240060201);
            gotomaps.put("mushmom", 100000005);
            gotomaps.put("griffey", 240020101);
            gotomaps.put("manon", 240020401);
            gotomaps.put("zakum", 280030000);
            gotomaps.put("czakum", 280030001);
            gotomaps.put("papulatus", 220080001);
            gotomaps.put("showatown", 801000000);
            gotomaps.put("zipangu", 800000000);
            gotomaps.put("ariant", 260000100);
            gotomaps.put("nautilus", 120000000);
            gotomaps.put("boatquay", 541000000);
            gotomaps.put("malaysia", 550000000);
            gotomaps.put("taiwan", 740000000);
            gotomaps.put("thailand", 500000000);
            gotomaps.put("erev", 130000000);
            gotomaps.put("ellinforest", 300000000);
            gotomaps.put("kampung", 551000000);
            gotomaps.put("singapore", 540000000);
            gotomaps.put("amoria", 680000000);
            gotomaps.put("timetemple", 270000000);
            gotomaps.put("pinkbean", 270050100);
            gotomaps.put("peachblossom", 700000000);
            gotomaps.put("fm", 910000000);
            gotomaps.put("freemarket", 910000000);
            gotomaps.put("oxquiz", 109020001);
            gotomaps.put("ola", 109030101);
            gotomaps.put("fitness", 109040000);
            gotomaps.put("snowball", 109060000);
            gotomaps.put("cashmap", 741010200);
            gotomaps.put("golden", 950100000);
            gotomaps.put("phantom", 610010000);
            gotomaps.put("cwk", 610030000);
            gotomaps.put("rien", 140000000);
            gotomaps.put("pachinko", 809030000);
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !goto <mapname>");
            } else if (gotomaps.containsKey(splitted[1])) {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else if (splitted[1].equals("locations")) {
                c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
                StringBuilder sb = new StringBuilder();
                for (String s : gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            } else {
                c.getPlayer().dropMessage(6, "Invalid command 指令規則 - Use !goto <location>. For a list of locations, use !goto locations.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!goto <名稱> - 到某個地圖").toString();

        }
    }

    public static class KillAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            MapleMonster mob;
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), false, false, (byte) 1);
            }
            c.getPlayer().dropMessage("您總共殺了 " + monsters.size() + " 怪物");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killall [range] [mapid] - 殺掉所有玩家").toString();

        }
    }

    public static class ResetMobs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().killAllMonsters(false);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!resetmobs - 重置地圖上所有怪物").toString();

        }
    }

    public static class 最近傳送點 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(-11, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!最近傳送點 - 查看最近的傳送點").toString();
        }
    }

    public static class KillMonster extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmonster <mobid> - 殺掉地圖上某個怪物").toString();

        }
    }

    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmonsterbyoid <moboid> - 殺掉地圖上某個怪物").toString();

        }
    }

    public static class HitMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            int damage = Integer.parseInt(splitted[2]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitmonsterbyoid <moboid> <damage> - 碰撞地圖上某個怪物").toString();

        }
    }

    public static class NPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null) {
                c.getPlayer().getMap().removeNpc(npcId, true);
                CustomNPC cnpc = new CustomNPC(npc.getId(), npc.getName(), c.getPlayer().getMapId(), c.getChannel());
                cnpc.setPosition(c.getPlayer().getPosition());
                cnpc.setCy(c.getPlayer().getPosition().y);
                cnpc.setRx0(c.getPlayer().getPosition().x + 50);
                cnpc.setRx1(c.getPlayer().getPosition().x - 50);
                cnpc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                if (splitted.length > 2 && splitted[2].equals("true")) {
                    cnpc.saveToDB();
                }
                c.getPlayer().getMap().addMapObject(cnpc);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(cnpc, true));
            } else {
                c.getPlayer().dropMessage(6, "你輸入不正確的NPC編號");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!npc <npcId> [true/false] - 放置自訂NPC，第二個參數為是否儲存到DB，預設false").toString();
        }
    }

    public static class RemoveNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int npcId = Integer.parseInt(splitted[1]);
            CustomNPC cnpc = CustomNPC.loadFromDB(npcId, c.getPlayer().getMapId(), c.getChannel());
            if (cnpc != null) {
                c.getPlayer().getMap().removeNpc(npcId, true);
                if (splitted.length > 2 && splitted[2].equals("true")) {
                    cnpc.deleteFromDB();
                }
            } else {
                c.getPlayer().dropMessage(6, "你輸入不正確的NPC編號");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removenpc <npcid> [true/false] - 刪除自訂NPC，第二個參數為是否從DB刪除，預設false").toString();
        }
    }

    public static class RemoveNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetNPCs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removenpcs - 刪除所有NPC").toString();
        }
    }

    public static class LookNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
                MapleNPC reactor2l = (MapleNPC) reactor1l;
                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!looknpcs - 查看所有NPC").toString();
        }
    }

    public static class LookReactors extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
                MapleReactor reactor2l = (MapleReactor) reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lookreactors - 查看所有反應堆").toString();
        }
    }

    public static class LookPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lookportals - 查看所有反應堆").toString();
        }
    }

    public static class MakePNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                MapleCharacter chhr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " is not online");

                } else {
                    int npcId = Integer.parseInt(splitted[2]);
                    PlayerNPC npc = new PlayerNPC(chhr, npcId, c.getPlayer().getMap(), c.getPlayer());
                    npc.addToServer();
                    c.getPlayer().dropMessage(6, "Done");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());

            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!makepnpc <playername> <npcid> - 創造玩家NPC").toString();
        }
    }

    public static class MakeOfflineP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                MapleClient cs = new MapleClient(null, null, new MockIOSession());
                MapleCharacter chhr = MapleCharacter.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted[1]), cs, false);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " does not exist");

                } else {
                    PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
                    npc.addToServer();
                    c.getPlayer().dropMessage(6, "Done");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());

            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!deletepnpc <charname> <npcid> - 創造離線PNPC").toString();
        }
    }

    public static class DestroyPNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                c.getPlayer().dropMessage(6, "Destroying playerNPC...");
                final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
                if (npc instanceof PlayerNPC) {
                    ((PlayerNPC) npc).destroy(true);
                    c.getPlayer().dropMessage(6, "Done");
                } else {
                    c.getPlayer().dropMessage(6, "!destroypnpc [objectid]");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!destroypnpc [objectid] - 刪除PNPC").toString();
        }

    }

    public static class MyPos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFH() + "| CY:" + pos.y);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mypos - 我的位置").toString();
        }
    }

    public static class ReloadOps extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadops - 重新載入OpCode").toString();
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloaddrops - 重新載入掉寶").toString();
        }
    }

    public static class ReloadPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            PortalScriptManager.getInstance().clearScripts();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadportals - 重新載入進入點").toString();
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory.getInstance().clear();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadshops - 重新載入商店").toString();
        }
    }

    public static class ReloadCS extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CashItemFactory.getInstance().clearItems();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadCS - 重新載入購物商城").toString();
        }
    }

    public static class ReloadGashapon extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            GashaponFactory.getInstance().reloadGashapons();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadGashapon - 重新載入轉蛋機").toString();
        }
    }

    public static class ReloadOX extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleOxQuizFactory.getInstance().reloadOX();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadox - 重新載入OX題目").toString();
        }
    }

    public static class ReloadFishing extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            FishingRewardFactory.getInstance().reloadItems();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadFishing - 重新載入釣魚獎勵").toString();
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadevents - 重新載入活動腳本").toString();
        }
    }

    public static class ReloadQuests extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleQuest.clearQuests();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadquests - 重新載入任務").toString();
        }
    }

    public static class Spawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final int mid = Integer.parseInt(splitted[1]);
            final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);

            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");

            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return true;
            }

            long newhp = 0;
            int newexp = 0;
            if (hp != null) {
                newhp = hp;
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (exp != null) {
                newexp = exp;
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawn <怪物ID> <hp|exp|php||pexp = ?> - 召喚怪物").toString();
        }
    }

    public static class Clock extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!clock <time> 時鐘").toString();
        }
    }

    public static class WarpPlayersTo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            } catch (Exception e) {
                return false; //assume drunk GM
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!warpplayersro <maipid> 把所有玩家傳送到某個地圖").toString();
        }
    }

    public static class WarpAllHere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                if (mch.getMapId() != c.getPlayer().getMapId()) {
                    mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!WarpAllHere 把所有玩家傳送到這裡").toString();
        }
    }

    public static class LOLCastle extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length != 2) {
                return false;
            }
            MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle" + splitted[1]).getMapFactory().getMap(990000300, false, false);
            c.getPlayer().changeMap(target, target.getPortal(0));

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lolcastle level (level = 1-5) - 不知道是啥").toString();
        }

    }

    public static class Map extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            try {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                MaplePortal targetPortal = null;
                if (splitted.length > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                    } catch (IndexOutOfBoundsException e) {
                        // noop, assume the gm didn't know how many portals there are
                        c.getPlayer().dropMessage(5, "Invalid portal selected.");
                    } catch (NumberFormatException a) {
                        // noop, assume that the gm is drunk
                    }
                }
                if (targetPortal == null) {
                    targetPortal = target.getPortal(0);
                }
                c.getPlayer().changeMap(target, targetPortal);
            } catch (Exception e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!map <mapid> [portal] - 傳送到某地圖").toString();
        }
    }

    public static class StartProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants"); //or should we do Packages.constants etc.?
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!startprofiling 開始紀錄JVM資訊").toString();
        }
    }

    public static class StopProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "The entered filename already exists, choose a different one");
                    return true;
                }
                sampler.stop();
                FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            } catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!stopprofiling <filename> - 取消紀錄JVM資訊並儲存到檔案").toString();
        }
    }

    public static class ReloadMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            final int mapId = Integer.parseInt(splitted[1]);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
                    c.getPlayer().dropMessage(5, "There exists characters on channel " + cserv.getChannel());
                    return true;
                }
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId)) {
                    cserv.getMapFactory().removeMap(mapId);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadmap <maipid> - 重置某個地圖").toString();
        }
    }

    public static class Respawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().respawn(true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!respawn - 重新進入地圖").toString();
        }
    }

    public static class ResetMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetFully();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!respawn - 重置這個地圖").toString();
        }
    }

    public static class Packet extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            int packetheader = Integer.parseInt(splitted[1]);
            String packet_in = " 00 00 00 00 00 00 00 00 00 ";
            if (splitted.length > 2) {
                packet_in = StringUtil.joinStringFrom(splitted, 2);
            }

            mplew.writeShort(packetheader);
            mplew.write(HexTool.getByteArrayFromHexString(packet_in));
            mplew.writeZeroBytes(20);
            c.getSession().write(mplew.getPacket());
            c.getPlayer().dropMessage(packetheader + "已傳送封包[" + packetheader + "] : " + mplew.toString());
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!Packet - <封包內容>").toString();
        }
    }

    public static class UpdateMap extends CommandExecute {

        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            boolean custMap = splitted.length >= 2;
            int mapid = custMap ? Integer.parseInt(splitted[1]) : player.getMapId();
            MapleMap map = custMap ? player.getClient().getChannelServer().getMapFactory().getMap(mapid) : player.getMap();
            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                MaplePortal newPor = newMap.getPortal(0);
                LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<>(map.getCharacters()); // do NOT remove, fixing ConcurrentModificationEx.
                outerLoop:
                for (MapleCharacter m : mcs) {
                    for (int x = 0; x < 5; x++) {
                        try {
                            m.changeMap(newMap, newPor);
                            continue outerLoop;
                        } catch (Throwable t) {
                        }
                    }
                    player.dropMessage("傳送玩家 " + m.getName() + " 到新地圖失敗. 自動省略...");
                }
                player.dropMessage("地圖刷新完成.");
                return true;
            }
            player.dropMessage("刷新地圖失敗!");
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!UpdateMap <maipid> - 刷新某個地圖").toString();
        }
    }

}
