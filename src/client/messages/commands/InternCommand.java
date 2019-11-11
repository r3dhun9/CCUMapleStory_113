package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.StringUtil;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

public class InternCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.INTERN;
    }

    public static class HellBan extends Ban {

        public HellBan() {
            hellban = true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hellban <玩家名稱> <原因> - hellban").toString();
        }
    }

    public static class BanID extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "Ban";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" 封鎖 ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            boolean offline = false;
            boolean ban = false;
            MapleCharacter target;
            int id = 0;
            String input = "null";
            try {
                id = Integer.parseInt(splitted[1]);
                input = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(id);
            String name = c.getPlayer().getCharacterNameById(id);
            if (ch <= 0) {
                if (c.getPlayer().OfflineBanById(id, sb.toString())) {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功離線封鎖 " + name + ".");
                    ban = true;
                    offline = true;
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗 " + splitted[1]);
                    return true;
                }
            } else {
                target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(id);
                if (target != null) {
                    if (c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().hasGmLevel(5)) {
                        sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                        if (target.ban(sb.toString(), c.getPlayer().hasGmLevel(5), false, hellban)) {
                            ban = true;
                            c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功封鎖 " + target.getName() + ".");
                            target.getClient().disconnect(true, false);
                        } else {
                            c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗.");
                            return true;
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 無法封鎖GMs...");
                        return true;
                    }
                    name = target.getName();
                }

            }

            String reason = "null".equals(input) ? "使用違法程式練功" : StringUtil.joinStringFrom(splitted, 2);
            World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice( "[封鎖系統] " + name + " 因為" + reason + "而被管理員永久停權。").getBytes());

            String msg = "[GM 密語] GM " + c.getPlayer().getName() + "  封鎖了 " + name + " 是否離線封鎖 " + offline + " 原因：" + reason;
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice( msg).getBytes());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!BanID <玩家ID> <原因> - 封鎖玩家").toString();
        }
    }

    public static class WarpID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int input = 0;
            try {
                input = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(input);
            if (ch < 0) {
                c.getPlayer().dropMessage(6, "玩家編號[" + input + "] 不在線上");
                return true;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterById(input);
            if (victim != null) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                } else {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "地圖不存在");
                    } else {
                        victim.changeMap(target, target.getPortal(0));
                    }
                }
            } else {
                try {
                    victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(Integer.parseInt(splitted[1]));
                    if (victim != null) {
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().dropMessage(6, "正在改變頻道請等待");
                        c.getPlayer().changeChannel(ch);

                    } else {
                        c.getPlayer().dropMessage(6, "角色不存在");
                    }

                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "出問題了 " + e.getMessage());
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!warpID [玩家編號] - 移動到某個玩家所在的地方").toString();
        }
    }

    public static class Ban extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "Ban";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" 封鎖 ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            boolean offline = false;
            boolean ban = false;
            MapleCharacter target;
            String name = "";
            String input = "null";
            try {
                name = splitted[1];
                input = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                if (c.getPlayer().OfflineBanByName(name, sb.toString())) {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功離線封鎖 " + splitted[1] + ".");
                    ban = true;
                    offline = true;
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗 " + splitted[1]);
                    return true;
                }
            } else {
                target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (target != null) {
                    if (c.getPlayer().getGMLevel() >= target.getGMLevel()) {
                        sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                        if (target.ban(sb.toString(), c.getPlayer().hasGmLevel(5), false, hellban)) {
                            ban = true;
                            c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功封鎖 " + target.getName() + ".");
                            target.getClient().disconnect(true, false);
                        } else {
                            c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗.");
                            return true;
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 無法封鎖GMs...");
                        return true;
                    }
                }
            }
            String reason = "null".equals(input) ? "使用違法程式練功" : StringUtil.joinStringFrom(splitted, 2);
            World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice( "[封鎖系統] " + splitted[1] + " 因為" + reason + "而被管理員永久停權。").getBytes());

            String msg = "[GM 密語] GM " + c.getPlayer().getName() + "  封鎖了 " + splitted[1] + " 是否離線封鎖 " + offline + " 原因：" + reason;
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice( msg).getBytes());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ban <玩家> <原因> - 封鎖玩家").toString();
        }
    }

    public static class UnHellBan extends UnBan {

        public UnHellBan() {
            hellban = true;
        }
    }

    public static class UnBan extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "UnBan";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            byte ret;
            if (hellban) {
                ret = MapleClient.unHellban(splitted[1]);
            } else {
                ret = MapleClient.unban(splitted[1]);
            }
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL 錯誤");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 目標玩家不存在");
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功解除鎖定");
            }
            byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL 錯誤.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 角色不存在.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] IP或Mac已解鎖其中一個.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] IP以及Mac已成功解鎖.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unban <玩家> - 解鎖玩家").toString();
        }
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL 錯誤.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] 角色不存在.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP或Mac已解鎖其中一個.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP以及Mac已成功解鎖.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unbanip <玩家名稱> - 解鎖玩家").toString();
        }
    }

    public static class TempBan extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            final int reason = Integer.parseInt(splitted[2]);
            final int numDay = Integer.parseInt(splitted[3]);

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, numDay);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null) {
                c.getPlayer().dropMessage(6, "[tempban] 找不到目標角色");

            } else {
                victim.tempban("由" + c.getPlayer().getName() + "暫時鎖定了", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted[1] + " 已成功被暫時鎖定至 " + df.format(cal.getTime()));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!tempban <玩家名稱> <理由> <時間> - 暫時鎖定玩家").toString();
        }
    }

    public static class banMac extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String mac = splitted[1];
            if (mac.equalsIgnoreCase("00-00-00-00-00-00") || mac.length() != 17) {
                c.getPlayer().dropMessage("封鎖MAC失敗，可能為格式錯誤或是長度錯誤 Ex: 00-00-00-00-00-00 ");
                return true;
            }
            c.getPlayer().dropMessage("封鎖MAC [" + mac + "] 成功");
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO macbans (mac) VALUES (?)")) {
                ps.setString(1, mac);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                System.err.println("Error banning MACs" + e);
                return true;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!BanMAC <MAC> - 封鎖MAC ").toString();
        }
    }

    public static class BanIP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            boolean error = false;
            String IP = splitted[1];
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps;
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, IP);
                ps.execute();
                ps.close();
            } catch (Exception ex) {
                error = true;
            }
            try {
                for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : cs.getPlayerStorage().getAllCharactersThreadSafe()) {
                        if (chr.getClient().getSessionIPAddress().equals(IP)) {
                            if (!chr.getClient().isGm()) {
                                chr.getClient().disconnect(true, false);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
            }
            c.getPlayer().dropMessage("封鎖IP [" + IP + "] " + (error ? "成功 " : "失敗"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!BanIP <IP> - 封鎖IP ").toString();
        }
    }

    public static class 加黑單 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String input = splitted[1];
            int ch = World.Find.findChannel(input);
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim.isAdmin()) {
                c.getPlayer().dropMessage("玩家:" + input + " 是GM不能加黑單！");
                return true;
            }
            if (ch <= 0) {
                c.getPlayer().dropMessage("玩家:" + input + " 不在線上。");
                return true;
            }
            int accID = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(input).getAccountID();
            BlackConfig.setBlackList(accID, input);
            String msg = "[GM 密語] GM " + c.getPlayer().getName() + " 在回報系統黑單了 " + input;
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice( msg).getBytes());
            FilePrinter.print("PlayerBlackList.txt", "\r\n  " + FilePrinter.getLocalDateString() + " GM :" + c.getPlayer().getName() + " 在回報系統黑單了 " + input);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!黑單 <玩家名稱> - 將玩家設定為無法回報的黑名單").toString();
        }
    }

    public static class BanStatus extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            String name = splitted[1];
            String mac = "";
            String ip = "";
            int acid = 0;
            boolean Systemban = false;
            boolean ACbanned = false;
            boolean IPbanned = false;
            boolean MACbanned = false;
            String reason = null;
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps;
                ps = con.prepareStatement("select accountid from characters where name = ?");
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        acid = rs.getInt("accountid");
                    }
                }
                ps = con.prepareStatement("select banned, banreason, macs, Sessionip from accounts where id = ?");
                ps.setInt(1, acid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Systemban = rs.getInt("banned") == 2;
                        ACbanned = rs.getInt("banned") == 1 || rs.getInt("banned") == 2;
                        reason = rs.getString("banreason");
                        mac = rs.getString("macs");
                        ip = rs.getString("Sessionip");
                    }
                }
                ps.close();
            } catch (Exception e) {
            }
            if (reason == null || reason.isEmpty()) {
                reason = "無";
            }
            if (c.isBannedIP(ip)) {
                IPbanned = true;
            }
            if (c.hasBannedMac()) {
                MACbanned = true;
            }

            c.getPlayer().dropMessage("玩家[" + name + "] 帳號ID[" + acid + "]是否被封鎖: " + (ACbanned ? "是" : "否") + (Systemban ? "(系統自動封鎖)" : "") + ", 原因: " + reason);
            c.getPlayer().dropMessage("IP: " + ip + " 是否在封鎖IP名單: " + (IPbanned ? "是" : "否"));
            for (String SingleMac : mac.split(", ")) {
                c.getPlayer().dropMessage("MAC: " + SingleMac + " 是否在封鎖MAC名單: " + (c.isBannedMac(SingleMac) ? "是" : "否"));
            }
            // c.getPlayer().dropMessage("MAC: " + mac + " 是否在封鎖MAC名單: " + (MACbanned ? "是" : "否"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!BanStatus <玩家名稱> - 查看玩家是否被封鎖及原因").toString();
        }
    }

    public static class ChangeChanel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int cc = Integer.parseInt(splitted[1]);
            if (c.getChannel() != cc) {
                c.getPlayer().changeChannel(cc);
            } else {
                c.getPlayer().dropMessage(5, "請輸入正確的頻道。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!changechannel <頻道> - 更換頻道").toString();
        }
    }

    public static class DC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {

            if (splitted.length < 1) {
                return false;
            }

            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);

            if (victim != null) {
                victim.getClient().disconnect(true, false);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dc <玩家> - 讓玩家斷線").toString();
        }
    }

    public static class spy extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "使用規則: ");
            } else {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);

                if (victim != null) {
                    if (c.getPlayer().getGMLevel() < victim.getGMLevel()) {
                        c.getPlayer().dropMessage(5, "你不能查看比你高權限的人!");
                    } else {
                        c.getPlayer().dropMessage(5, "此玩家狀態:");
                        c.getPlayer().dropMessage(5, "IP:" + victim.getClient().getSessionIPAddress() + " MAC:" + victim.getClient().getMacs());
                        c.getPlayer().dropMessage(5, "帳號ID:" + victim.getAccountID() + " 角色ID:" + victim.getId());
                        c.getPlayer().dropMessage(5, "等級: " + victim.getLevel() + " 職業: " + victim.getJob() + " 名聲: " + victim.getFame());
                        c.getPlayer().dropMessage(5, "地圖: " + victim.getMapId() + " - " + victim.getMap().getMapName());
                        c.getPlayer().dropMessage(5, "目前HP: " + victim.getStat().getHp() + " 目前MP: " + victim.getStat().getMp());
                        c.getPlayer().dropMessage(5, "最大HP: " + victim.getStat().getMaxHp() + " 最大MP: " + victim.getStat().getMaxMp());
                        c.getPlayer().dropMessage(5, "力量: " + victim.getStat().getStr() + "  ||  敏捷: " + victim.getStat().getDex() + "  ||  智力: " + victim.getStat().getInt() + "  ||  幸運: " + victim.getStat().getLuk());
                        c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        c.getPlayer().dropMessage(5, "DPM: " + victim.getDPS());
                        c.getPlayer().dropMessage(5, "已使用:" + victim.getHpMpApUsed() + " 張能力重置捲");
                        c.getPlayer().dropMessage(5, "經驗倍率: " + victim.getStat().expBuff + " 金錢倍率: " + victim.getStat().mesoBuff + " 掉寶倍率: " + victim.getStat().dropBuff);
                        c.getPlayer().dropMessage(5, "擁有 " + victim.getCSPoints(1) + " GASH " + victim.getCSPoints(2) + " 楓葉點數 " + victim.getMeso() + " 楓幣　");
                        c.getPlayer().dropMessage(5, "對伺服器延遲: " + victim.getClient().getLatency());
                    }
                } else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spy <玩家名字> - 觀察玩家").toString();
        }
    }

    public static class 精靈商人訊息 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean hh = c.getPlayer().getSwitchHiredMerchant();
            if (hh) {
                c.getPlayer().getSwitchHiredMerchant(false);
            } else {
                c.getPlayer().getSwitchHiredMerchant(true);
            }
            hh = c.getPlayer().getSwitchHiredMerchant();
            c.getPlayer().dropMessage(6, "[精靈商人購買訊息] " + (hh ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!精靈商人訊息  - 商人購買訊息開關").toString();
        }
    }

    public static class 玩家私聊1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean hack1 = c.getPlayer().get玩家私聊1();
            if (hack1) {
                c.getPlayer().get玩家私聊1(false);
            } else {
                c.getPlayer().get玩家私聊1(true);
            }
            hack1 = c.getPlayer().get玩家私聊1();
            c.getPlayer().dropMessage(6, "[玩家私聊1] " + (hack1 ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!玩家私聊1  - 玩家普通.交易聊天偷聽開關").toString();
        }
    }

    public static class 玩家私聊2 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean hack2 = c.getPlayer().get玩家私聊2();
            if (hack2) {
                c.getPlayer().get玩家私聊2(false);
            } else {
                c.getPlayer().get玩家私聊2(true);
            }
            hack2 = c.getPlayer().get玩家私聊2();
            c.getPlayer().dropMessage(6, "[玩家私聊2] " + (hack2 ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!玩家私聊2  - 玩家好友.組隊.密語聊天偷聽開關").toString();
        }
    }

    public static class 玩家私聊3 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean hack2 = c.getPlayer().get玩家私聊3();
            if (hack2) {
                c.getPlayer().get玩家私聊3(false);
            } else {
                c.getPlayer().get玩家私聊3(true);
            }
            hack2 = c.getPlayer().get玩家私聊3();
            c.getPlayer().dropMessage(6, "[玩家私聊3] " + (hack2 ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!玩家私聊3  - 玩家公會.家族聊天偷聽開關").toString();
        }
    }

    public static class GMinfo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean GMinfo = c.getPlayer().getGMinfo();
            if (GMinfo) {
                c.getPlayer().getGMinfo(false);
            } else {
                c.getPlayer().getGMinfo(true);
            }
            GMinfo = c.getPlayer().getGMinfo();
            c.getPlayer().dropMessage(6, "[GMinfo] " + (GMinfo ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!GMinfo  - 讓普通人可以開GM個人資訊").toString();
        }
    }

    public static class GM聊天 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean GMChat = c.getPlayer().getGMChat();
            if (GMChat) {
                c.getPlayer().getGMChat(false);
            } else {
                c.getPlayer().getGMChat(true);
            }
            GMChat = c.getPlayer().getGMChat();
            c.getPlayer().dropMessage(6, "[GM聊天開關] " + (GMChat ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!GM聊天 - GM聊天").toString();
        }
    }

    public static class 聊天稱號開關 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean ChatTitle = c.getPlayer().getCTitle();
            if (ChatTitle) {
                c.getPlayer().getCTitle(false);
            } else {
                c.getPlayer().getCTitle(true);
            }
            ChatTitle = c.getPlayer().getCTitle();
            c.getPlayer().dropMessage(6, "[聊天稱號開關] " + (ChatTitle ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!聊天稱號開關  - 聊天稱號開關").toString();
        }
    }

    public static class 聊天稱號設定 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 1) {
                return false;
            }
            String ChatTtitle = "";
            ChatTtitle = splitted[1];
            c.getPlayer().setChatTitle(ChatTtitle);
            c.getPlayer().dropMessage(6, "[聊天稱號開關] 設定成功");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!聊天稱號設定  - 聊天稱號開關").toString();
        }
    }

    public static class online extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int total = 0;
            int curConnected = c.getChannelServer().getConnectedClients();
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            c.getPlayer().dropMessage(6, new StringBuilder().append("頻道: ").append(c.getChannelServer().getChannel()).append(" 線上人數: ").append(curConnected).toString());
            total += curConnected;
            for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (chr != null && c.getPlayer().getGMLevel() >= chr.getGMLevel()) {
                    StringBuilder ret = new StringBuilder();
                    ret.append(" 角色暱稱 ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                    ret.append(" ID: ");
                    ret.append(chr.getId());
                    ret.append(" 等級: ");
                    ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 3));
                    ret.append(" 職業: ");
                    ret.append(chr.getJob());
                    if (chr.getMap() != null) {
                        ret.append(" 地圖: ");
                        ret.append(chr.getMapId()).append(" - ").append(chr.getMap().getMapName());
                        c.getPlayer().dropMessage(6, ret.toString());
                    }
                }
            }
            c.getPlayer().dropMessage(6, new StringBuilder().append("當前頻道總計線上人數: ").append(total).toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            int channelOnline = c.getChannelServer().getConnectedClients();
            int totalOnline = 0;
            /*伺服器總人數*/
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                totalOnline += cserv.getConnectedClients();
            }
            c.getPlayer().dropMessage(6, new StringBuilder().append("當前伺服器總計線上人數: ").append(totalOnline).append("個").toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!online - 查看線上人數").toString();
        }
    }

    public static class WhereAmI extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(5, "目前地圖 " + c.getPlayer().getMap().getId() + "座標 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!whereami - 目前地圖").toString();
        }
    }

    public static class Warp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String input = splitted[1];
            int ch = World.Find.findChannel(input);
            if (ch < 0) {
                int mapid = -1;
                try {
                    mapid = Integer.parseInt(input);
                } catch (Exception ex) {
                    c.getPlayer().dropMessage(6, "出問題了 " + ex.getMessage());
                }
                doMap(c, mapid);
                return true;
            }

            MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    if (victim.getMapId() != c.getPlayer().getMapId()) {
                        final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                        c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                    }
                    if (victim.getClient().getChannel() != c.getChannel()) {
                        c.getPlayer().dropMessage(6, "正在改變頻道請等待");
                        c.getPlayer().changeChannel(victim.getClient().getChannel());
                    }
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                } else {
                    doMap(victim.getClient(), Integer.parseInt(splitted[2]));
                    return true;
                }
            } else {
                c.getPlayer().dropMessage(6, "角色不存在");
            }
            return true;
        }

        public boolean doMap(MapleClient c, int mapid) {
            MapleMap target = null;
            try {
                target = c.getChannelServer().getMapFactory().getMap(mapid);
            } catch (Exception ex) {
            }
            if (target == null) {
                c.getPlayer().dropMessage(6, "地圖不存在");
            } else {
                c.getPlayer().changeMap(target, target.getPortal(0));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!warp 玩家名稱 <地圖ID> - 移動到某個地圖或某個玩家所在的地方").toString();
        }
    }

    public static class CnGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.getErrorNotice( "<GM聊天視窗>" + "頻道" + c.getPlayer().getClient().getChannel() + " [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cngm <訊息> - GM聊天").toString();
        }
    }

    public static class 清地板 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "清除 " + c.getPlayer().getMap().getNumItems() + " 項物品");
            c.getPlayer().getMap().removeDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("").toString();
        }
    }

    public static class Hide extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "管理員隱藏 = 開啟 \r\n 解除請輸入!unhide");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hide - 隱藏").toString();
        }
    }

    public static class UnHide extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "管理員隱藏 = 關閉 \r\n 開啟請輸入!hide");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unhide - 解除隱藏").toString();
        }
    }
}
