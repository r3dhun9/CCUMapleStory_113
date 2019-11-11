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
package handling.login.handler;

import java.util.List;
import java.util.Calendar;

import client.inventory.IItem;
import client.inventory.Item;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;
import tools.KoreanDateUtil;
import tools.StringUtil;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharLoginHandler {

    private static boolean loginFailed(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 5;
    }

    public static final void handleWelcome(final MapleClient c) {
        c.sendPing();
    }

    public static final void handleLogout(final SeekableLittleEndianAccessor slea, MapleClient c) {
        String account = slea.readMapleAsciiString();
        String IpAddress = c.getSessionIPAddress();
        c.setAccountName(account);
        c.logout();
    }

    private static String readMacAddress(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        int[] bytes = new int[6];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = slea.readByteAsInt();
        }
        StringBuilder sps = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sps.append(StringUtil.getLeftPaddedStr(Integer.toHexString(bytes[i]).toUpperCase(), '0', 2));
            sps.append("-");
        }
        return sps.toString().substring(0, sps.toString().length() - 1);
    }

    public static final void handleLogin(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        String account = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();

        if (account == null || password == null) {
            c.getSession().close(true);
        }

        String macData = readMacAddress(slea, c);
        c.setMacs(macData);
        c.setLoginMacs(macData);
        c.setAccountName(account);

        LoginResponse loginResponse = c.login(account, password);

        final Calendar tempBannedTill = c.getTempBanCalendar();
        String errorInfo = null;

        if (loginResponse != LoginResponse.LOGIN_SUCCESS && loginFailed(c)) { // Left-to-right evaluation
            c.getSession().close(true);
            return;
        }
        switch (loginResponse) {

            case LOGIN_SUCCESS:
                if (!c.isSetSecondPassword()) {
                    c.sendPacket(LoginPacket.getGenderNeeded(c));
                    return;
                }

                if (tempBannedTill.getTimeInMillis() != 0) {
                    if (!loginFailed(c)) {
                        c.sendPacket(LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempBannedTill.getTimeInMillis()), c.getBanReason()));
                    } else {
                        c.getSession().close(true);
                    }
                } else {
                    c.loginAttempt = 0;
                    c.updateMacs(macData);
                    ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());
                    LoginWorker.registerClient(c);
                }
                return;
            case NOT_REGISTERED:
                if (LoginServer.AutoRegister) {
                    if (account.length() >= 12) {
                        errorInfo = "您的帳號長度太長了唷!\r\n請重新輸入.";
                    } else {
                        AutoRegister.createAccount(account, password, c.getSession().getRemoteAddress().toString(), macData);
                        if (AutoRegister.success && AutoRegister.macAllowed) {
                            c.setAccID(AutoRegister.registeredId);
                            c.sendPacket(LoginPacket.getGenderNeeded(c));
                            return;
                        } else if (!AutoRegister.macAllowed) {
                            errorInfo = "無法註冊過多的帳號密碼唷!";
                            AutoRegister.success = false;
                            AutoRegister.macAllowed = true;
                        }
                    }
                }
                break;
            case ALREADY_LOGGED_IN:
                if(LoginServer.getClientStorage().getClientByName(c.getAccountName()) != null)
                    LoginServer.getClientStorage().getClientByName(c.getAccountName()).getSession().close(true);
                ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());
                c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
                errorInfo = "解卡成功，重新登入";
                break;
            case SYSTEM_ERROR:
                errorInfo = "系統錯誤(錯誤代碼:0)";
                break;
            case SYSTEM_ERROR2:
                errorInfo = "系統錯誤(錯誤代碼:1)";
                break;
        }

        if (errorInfo != null) {
            c.getSession().write(MaplePacketCreator.getPopupMsg(errorInfo));
            c.sendPacket(LoginPacket.getLoginFailed(LoginResponse.NOP.getValue()));
        } else {
            c.sendPacket(LoginPacket.getLoginFailed(loginResponse.getValue()));
        }
    }

    public static final void handleGenderSet(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        String username = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();
        if (c.getAccountName().equals(username)) {
            c.setGender(slea.readByte());
            c.setSecondPassword(password);
            c.update2ndPassword();
            c.updateGender();
            c.sendPacket(LoginPacket.getGenderChanged(c));
        } else {
            c.getSession().close(true);
        }
    }

    public static final void handleServerList(final MapleClient c) {
        c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
        c.sendPacket(LoginPacket.getEndOfServerList());
    }

    public static final void handleServerStatus(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(1));
        } else {
            c.sendPacket(LoginPacket.getServerStatus(0));
        }
    }

    public static final void handleCharacterList(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        slea.readByte();
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        final int userLimit = LoginServer.getUserLimit();

        c.setWorld(server);
        //System.out.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        c.setChannel(channel);
        c.setWorld(server);

        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null) {
            c.sendPacket(LoginPacket.getCharList(c.getSecondPassword() != null, chars, c.getCharacterSlots()));
        } else {
            c.getSession().close(true);
        }
    }

    public static final void handleCheckCharacterName(final String name, final MapleClient c) {
        c.sendPacket(LoginPacket.charNameResponse(name,
                !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void handleCreateCharacter(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final String name = slea.readMapleAsciiString();
        if (name.contains("Admin") || name.contains("admin") || name.contains("GameMaster") || name.contains("gamemaster")) {
            c.sendPacket(MaplePacketCreator.getPopupMsg("這個名字是非法的喔，請在想一個新名字。"));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        final int JobType = slea.readInt(); // 1 = Adventurer, 0 = Cygnus, 2 = Aran

        final int face = slea.readInt();
        final int hair = slea.readInt();
        final int hairColor = 0;
        final byte skinColor = 0;
        final int top = slea.readInt();
        final int bottom = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getGender();

        if (gender == 0 && (JobType == 1 || JobType == 0)) {
            if (face != 20100 && face != 20401 && face != 20402) {
                return;
            }
            if (hair != 30030 && hair != 30027 && hair != 30000) {
                return;
            }
            if (top != 1040002 && top != 1040006 && top != 1040010) {
                return;
            }
            if (bottom != 1060002 && bottom != 1060006) {
                return;
            }
            if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038) {
                return;
            }
            if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004) {
                return;
            }

        } else if (gender == 1 && (JobType == 1 || JobType == 0)) {
            if (face != 21002 && face != 21700 && face != 21201) {
                return;
            }
            if (hair != 31002 && hair != 31047 && hair != 31057) {
                return;
            }
            if (top != 1041002 && top != 1041006 && top != 1041010 && top != 1041011) {
                return;
            }
            if (bottom != 1061002 && bottom != 1061008) {
                return;
            }
            if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038) {
                return;
            }
            if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004) {
                return;
            }

        } else if (JobType == 2) {

            if (gender == 0) {
                if (face != 20100 && face != 20401 && face != 20402) {
                    return;
                }
                if (hair != 30030 && hair != 30027 && hair != 30000) {
                    return;
                }
            } else if (gender == 1) {
                if (face != 21002 && face != 21700 && face != 21201) {
                    return;
                }
                if (hair != 31002 && hair != 31047 && hair != 31057) {
                    return;
                }
            }
            if (top != 1042167) {
                return;
            }
            if (bottom != 1062115) {
                return;
            }
            if (shoes != 1072383) {
                return;
            }
            if (weapon != 1442079) {
                return;
            }
        }

        MapleCharacter newchar = MapleCharacter.getDefault(c, JobType);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(face);
        newchar.setHair(hair + hairColor);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);

        MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        IItem item = li.getEquipById(top);
        item.setPosition((byte) -5);
        equip.addFromDB(item);

        item = li.getEquipById(bottom);
        item.setPosition((byte) -6);
        equip.addFromDB(item);

        item = li.getEquipById(shoes);
        item.setPosition((byte) -7);
        equip.addFromDB(item);

        item = li.getEquipById(weapon);
        item.setPosition((byte) -11);
        equip.addFromDB(item);

        //blue/red pots
        switch (JobType) {
            case 0: // 皇家騎士團
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null); //>_>_>_> ugh
                newchar.setQuestAdd(MapleQuest.getInstance(20000), (byte) 1, null); //>_>_>_> ugh
                newchar.setQuestAdd(MapleQuest.getInstance(20015), (byte) 1, null); //>_>_>_> ugh
                newchar.setQuestAdd(MapleQuest.getInstance(20020), (byte) 1, null); //>_>_>_> ugh

                newchar.getInventory(MapleInventoryType.ETC).addItem1(new Item(4161047, (byte) 0, (short) 1, (byte) 0));
                break;
            case 1: // 冒險者
                newchar.getInventory(MapleInventoryType.ETC).addItem1(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case 2: // 狂狼勇士
                newchar.setSkinColor((byte) 11);
                newchar.getInventory(MapleInventoryType.ETC).addItem1(new Item(4161048, (byte) 0, (short) 1, (byte) 0));
                break;
        }

        if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
            MapleCharacter.saveNewCharToDB(newchar, JobType, JobType == 1);
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static final void handleDeleteCharacter(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        slea.readByte();

        String _2ndPassword;
        _2ndPassword = slea.readMapleAsciiString();

        final int characterId = slea.readInt();
        if (!c.login_Auth(characterId)) {
            c.sendPacket(LoginPacket.secondPwError((byte) 0x14));
            return;
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (_2ndPassword == null) { // Client's hacking
                c.getSession().close(true);
                return;
            } else if (!c.check2ndPassword(_2ndPassword)) { // Wrong Password
                //state = 12;
                state = 16;
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(characterId);
        }

        c.sendPacket(LoginPacket.deleteCharResponse(characterId, state));
    }

    public static final void handleSelectCharacter(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        final int charId = slea.readInt();

        try {
            PreparedStatement ps = null;
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            ps = con.prepareStatement("select accountid from characters where id = ?");
            ps.setInt(1, charId);
            rs = ps.executeQuery();
            if (!rs.next() || rs.getInt("accountid") != c.getAccID()) {
                ps.close();
                rs.close();
                return;
            }
            ps.close();
            rs.close();
        } catch (Exception ex) {
        }

        LoginServer.addLoginMac(c);
        LoginServer.removeClient(c);
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }

        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        byte[] ip = {127, 0, 0, 1};
        try {
            ip = InetAddress.getByName(ChannelServer.getInstance(c.getChannel()).getGatewayIP()).getAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CharLoginHandler.class.getName()).log(Level.SEVERE, "getIP Error", ex);
        }
        int port = ChannelServer.getInstance(c.getChannel()).getPort();
        c.sendPacket(MaplePacketCreator.getServerIP(ip, port, charId));
    }

}
