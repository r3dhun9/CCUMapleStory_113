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
package client.messages;

import java.util.ArrayList;
import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.*;
import client.messages.commands.AdminCommand;
import client.messages.commands.PlayerCommand;
import client.messages.commands.GMCommand;
import client.messages.commands.InternCommand;
import constants.ServerConstants.CommandType;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.world.World;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import tools.FilePrinter;
import tools.MaplePacketCreator;

public class CommandProcessor {

    private final static HashMap<String, CommandObject> commands = new HashMap<>();
    private final static HashMap<Integer, ArrayList<String>> NormalCommandList = new HashMap<>();

    static {
        DoNormalCommand();
    }

    public static void dropHelp(MapleClient c, int type) {
        final StringBuilder sb = new StringBuilder("指令列表:\r\n");
        HashMap<Integer, ArrayList<String>> commandList = new HashMap<>();
        int check = 0;
        if (type == 0) {
            commandList = NormalCommandList;
            check = c.getPlayer().getGMLevel();
        }
        for (int i = 0; i <= check; i++) {
            if (commandList.containsKey(i)) {
                sb.append("權限等級： ").append(i).append("\r\n");
                for (String s : commandList.get(i)) {
                    CommandObject co = commands.get(s);
                    sb.append(co.getMessage());
                    sb.append(" \r\n");
                }
            }
        }
        c.getPlayer().dropNPC(sb.toString());
    }

    private static void sendDisplayMessage(MapleClient c, String msg, CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL:
                c.getPlayer().dropMessage(6, msg);
                break;
        }
    }

    public static boolean processCommand(MapleClient c, String line, CommandType type) {

        char commandPrefix = line.charAt(0);
        for (PlayerGMRank prefix : PlayerGMRank.values()) {
            if (line.startsWith(String.valueOf(prefix.getCommandPrefix() + prefix.getCommandPrefix()))) {
                return false;
            }
        }
        // 偵測玩家指令
        if (commandPrefix == PlayerGMRank.NORMAL.getCommandPrefix()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            CommandObject co = commands.get(splitted[0]);
            if (co == null || co.getType() != type) {
                sendDisplayMessage(c, "沒有這個指令,可以使用 @help 來查看指令.", type);
                return false;
            }
            try {
                boolean ret = co.execute(c, splitted);
                if (!ret) {
                    c.getPlayer().dropMessage("指令錯誤，用法： " + co.getMessage());
                }
            } catch (Exception e) {
                sendDisplayMessage(c, "有錯誤.", type);
                if (c.getPlayer().isGM()) {
                    sendDisplayMessage(c, "錯誤: " + e, type);
                }
            }
            return true;
        } else if (c.getPlayer().getGMLevel() > PlayerGMRank.NORMAL.getLevel()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();
            if (line.charAt(0) == '!') { //GM Commands
                CommandObject co = commands.get(splitted[0]);
                if (co == null || co.getType() != type) {
                    if (splitted[0].equals(line.charAt(0) + "help")) {
                        dropHelp(c, 0);
                        return true;
                    }
                    sendDisplayMessage(c, "沒有這個指令.", type);
                    return true;
                }
                boolean CanUseCommand = false;
                if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                    CanUseCommand = true;
                }
                if (!CanUseCommand) {
                    sendDisplayMessage(c, "你沒有權限可以使用指令.", type);
                    return true;
                }
                // 開始處理指令(GM區)
                if (c.getPlayer() != null) {
                    boolean ret = false;
                    try {
                        //執行指令
                        ret = co.execute(c, splitted);
                        // return ret;

                        if (ret) {
                            //指令log到DB
                            logGMCommandToDB(c.getPlayer(), line);
                            // 訊息處理
                            ShowMsg(c, line, type);
                        } else {
                            c.getPlayer().dropMessage("指令錯誤，用法： " + co.getMessage());
                        }
                    } catch (Exception e) {
                    }
                    return true;
                }

            }
        }
        return false;
    }

    private static void logGMCommandToDB(MapleCharacter player, String command) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO gmlog (cid, command, mapid) VALUES (?, ?, ?)");
            ps.setInt(1, player.getId());
            ps.setString(2, command);
            ps.setInt(3, player.getMap().getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            FilePrinter.printError(FilePrinter.CommandProccessor, ex, "logGMCommandToDB");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {

                FilePrinter.printError(FilePrinter.CommandProccessor, ex, "logGMCommandToDB");

            }
        }
    }

    private static void DoNormalCommand() {
        Class<?>[] CommandFiles = {
            PlayerCommand.class, InternCommand.class, GMCommand.class, AdminCommand.class
        };
        for (Class<?> clasz : CommandFiles) {
            try {
                PlayerGMRank rankNeeded = (PlayerGMRank) clasz.getMethod("getPlayerLevelRequired", new Class<?>[]{}).invoke(null, (Object[]) null);
                Class<?>[] commandClasses = clasz.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<>();
                for (Class<?> c : commandClasses) {
                    try {
                        if (!Modifier.isAbstract(c.getModifiers()) && !c.isSynthetic()) {
                            Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            } catch (NoSuchFieldException ex) {
                                enabled = true; //Enable all coded commands by default.
                            }
                            if (o instanceof CommandExecute && enabled) {
                                cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                                commands.put(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), new CommandObject(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), (CommandExecute) o, rankNeeded.getLevel()));
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException ex) {
                        FilePrinter.printError(FilePrinter.CommandProccessor, ex);
                    }
                }
                Collections.sort(cL);
                NormalCommandList.put(rankNeeded.getLevel(), cL);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                FilePrinter.printError(FilePrinter.CommandProccessor, ex);
            }
        }
    }

    private static void ShowMsg(MapleClient c, String line, CommandType type) {
        if (c.getPlayer() != null) {
            if (!line.toLowerCase().startsWith("!cngm")) {
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + c.getPlayer().getName() + "(" + c.getPlayer().getId() + ")使用了指令 " + line + " ---在地圖「" + c.getPlayer().getMapId() + "」頻道：" + c.getChannel()).getBytes());
            }
        }
        switch (c.getPlayer().getGMLevel()) {
            case 5:
                System.out.println("＜超級管理員＞ " + c.getPlayer().getName() + " 使用了指令: " + line);
                break;
            case 4:
                System.out.println("＜領導者＞ " + c.getPlayer().getName() + " 使用了指令: " + line);
                break;
            case 3:
                System.out.println("＜巡邏者＞ " + c.getPlayer().getName() + " 使用了指令: " + line);
                break;
            case 2:
                System.out.println("＜老實習生＞ " + c.getPlayer().getName() + " 使用了指令: " + line);
                break;
            case 1:
                System.out.println("＜新實習生＞ " + c.getPlayer().getName() + " 使用了指令: " + line);
                break;
            default:
                sendDisplayMessage(c, "你沒有權限可以使用指令.", type);
                break;
        }
    }
}
