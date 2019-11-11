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
package handling.channel.handler;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.MapleClient;
import client.MapleCharacter;
import constants.GameConstants;
import client.inventory.ItemLoader;
import database.DatabaseConnection;
import handling.world.World;
import java.util.Map;
import server.MapleInventoryManipulator;
import server.MerchItemPackage;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.packet.PlayerShopPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class HiredMerchantHandler {

    public static final void UseHiredMerchant(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        if (c.getPlayer().getMap().allowPersonalShop() && c.getPlayer().getMap() != null) {
            final byte state = checkExistance(c.getPlayer().getAccountID(), c.getPlayer().getId());
            final int HMCH = MapleMap.getMerchantChannel(c.getPlayer());

            switch (state) {
                case 1:
                    c.getPlayer().dropMessage(1, "請先去找富蘭德里領取你之前擺的東西。");
                    break;
                case 0:
                    boolean merch = World.hasMerchant(c.getPlayer().getAccountID());
                    if (!merch) {
                        c.sendPacket(PlayerShopPacket.sendTitleBox());
                    } else {
                        c.getPlayer().dropMessage(1, "請先關閉在" + HMCH + "頻的精靈商人。");
                    }
                    break;
                default:
                    c.getPlayer().dropMessage(1, "未知的錯誤");
                    break;
            }
        } else {
            c.disconnect(true, false);
        }
    }

    private static byte checkExistance(final int accid, final int charid) {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?")) {
            ps.setInt(1, accid);
            ps.setInt(2, charid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ps.close();
                    rs.close();
                    return 1;
                }
            }
            return 0;
        } catch (SQLException se) {
            return -1;
        }
    }

    public static final void handleRemote(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        //尚未處理精靈商人遠端遙控器
    }

    public static final void MerchantItemStore(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        final byte operation = slea.readByte();

        switch (operation) {
            case 20: {
                final String _2ndpw = slea.readMapleAsciiString();
                if (c.getSecondPassword() != null) {
                    if (_2ndpw == null) { // 確認是否封包掛
                        c.getPlayer().dropMessage(1, "請輸入密碼。");
                        c.getPlayer().setConversation(0);
                        return;
                    } else {
                        if (!c.check2ndPassword(_2ndpw)) { // 錯誤密碼
                            c.getPlayer().dropMessage(1, "密碼錯誤。");
                            c.getPlayer().setConversation(0);
                            return;
                        }
                        final int conv = c.getPlayer().getConversation();
                        final int HMMap = MapleMap.getMerchantMap(c.getPlayer());
                        final int HMChannel = MapleMap.getMerchantChannel(c.getPlayer());
                        boolean merch = World.hasMerchant(c.getPlayer().getAccountID());
                        if (merch) {
                            //c.getPlayer().dropMessage(1, "請關閉商店後在試一次.");
                            c.sendPacket(PlayerShopPacket.ShowMerchItemStore(9030000, HMMap, HMChannel));
                            c.getPlayer().setConversation(0);
                        } else if (conv == 3) { // Hired Merch
                            final MerchItemPackage pack = loadItemFromDatabase(c.getPlayer().getId(), c.getPlayer().getAccountID());
                            if (pack == null) {
                                c.getPlayer().dropMessage(1, "你沒有在這邊置放道具!");
                                c.getPlayer().setConversation(0);
                            } else if ((c.getPlayer().getMeso() + pack.getMesos()) >= 2147483647) {
                                c.getPlayer().dropMessage(1, "由於你身上的楓幣可能滿了，請將多餘的楓幣存入倉庫。");
                                c.getPlayer().setConversation(0);
                            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                                if (!check(c.getPlayer(), pack)) {
                                    c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 0x21));
                                    return;
                                }
                                for (IItem item : pack.getItems()) {
                                    MapleInventoryManipulator.addFromDrop(c, item, true);
                                }
                                if (deletePackage(c.getPlayer().getId(), c.getPlayer().getAccountID(), pack.getPackageid())) {
                                    c.getPlayer().gainMeso(pack.getMesos(), true);
                                    c.getPlayer().dropMessage(1, "你已經從精靈商人領取了" + pack.getMesos() + "楓幣");
                                    FilePrinter.print("HiredMerchantLog.txt", "角色名字:" + c.getPlayer().getName() + " 從精靈商人取回楓幣: " + pack.getMesos() + " 取回時間:" + FilePrinter.getLocalDateString());
                                    //c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 0x1d));
                                    c.getPlayer().setConversation(0);
                                } else {
                                    c.getPlayer().dropMessage(1, "未知的錯誤");
                                }
                            } else {
                                c.sendPacket(PlayerShopPacket.merchItemStore_ItemData(pack));
                                FilePrinter.print("HiredMerchantLog.txt", "角色名字:" + c.getPlayer().getName() + " 從精靈商人取回楓幣: " + pack.getMesos() + " 和" + pack.getItems().size() + "件物品 取回時間:" + FilePrinter.getLocalDateString());
                            }
                        }
                        break;
                    }
                }
            }
            case 25: { // Request take out iteme
                if (c.getPlayer().getConversation() != 3) {
                    return;
                }
                c.sendPacket(PlayerShopPacket.merchItemStore((byte) 0x24));
                break;
            }
            case 26: { // Take out item
                if (c.getPlayer().getConversation() != 3) {
                    return;
                }
                final MerchItemPackage pack = loadItemFromDatabase(c.getPlayer().getId(), c.getPlayer().getAccountID());

                if (pack == null) {
                    c.getPlayer().dropMessage(1, "未知的錯誤");
                    return;
                }
                if (!check(c.getPlayer(), pack)) {
                    c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 0x21));
                    return;
                }
                if (deletePackage(c.getPlayer().getId(), c.getPlayer().getAccountID(), pack.getPackageid())) {
                    String output = "";
                    c.getPlayer().gainMeso(pack.getMesos(), true);
                    for (IItem item : pack.getItems()) {
                        MapleInventoryManipulator.addFromDrop(c, item, true);
                        output += item.getItemId() + "(" + item.getQuantity() + "), ";
                    }
                    c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 0x1d));
                    FilePrinter.print("HiredMerchantLog.txt", "角色名字:" + c.getPlayer().getName() + " 從精靈商人取回楓幣: " + pack.getMesos() + " 和" + pack.getItems().size() + "件物品 取回時間:" + FilePrinter.getLocalDateString());
                } else {
                    c.getPlayer().dropMessage(1, "未知的錯誤");
                }
                break;
            }
            case 27: { // Exit
                c.getPlayer().setConversation(0);
                break;
            }
        }
    }

    private static final boolean check(final MapleCharacter chr, final MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (IItem item : pack.getItems()) {
            final MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == MapleInventoryType.EQUIP) {
                eq++;
            } else if (invtype == MapleInventoryType.USE) {
                use++;
            } else if (invtype == MapleInventoryType.SETUP) {
                setup++;
            } else if (invtype == MapleInventoryType.ETC) {
                etc++;
            } else if (invtype == MapleInventoryType.CASH) {
                cash++;
            }
            /* if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1)) {
             return false;
             }*/
        }
        boolean slot = true;
        if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() <= eq && eq != 0) {
            slot = false;
        }
        if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() <= use && use != 0) {
            slot = false;
        }
        if (chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() <= setup && setup != 0) {
            slot = false;
        }
        if (chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() <= etc && etc != 0) {
            slot = false;
        }
        if (chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() <= cash && cash != 0) {
            slot = false;
        }
        return slot;
    }

    private static boolean deletePackage(final int charid, final int accid, final int packageid) {
        final Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where characterid = ? OR accountid = ? OR packageid = ?")) {
            ps.setInt(1, charid);
            ps.setInt(2, accid);
            ps.setInt(3, packageid);
            ps.execute();
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid, accid, charid);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static MerchItemPackage loadItemFromDatabase(final int charid, final int accountid) {
        final Connection con = DatabaseConnection.getConnection();

        ResultSet rs;
        final int packageid;
        final MerchItemPackage pack;
        try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ? OR accountid = ?")) {
            ps.setInt(1, charid);
            ps.setInt(2, accountid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                rs.close();
                return null;
            }
            packageid = rs.getInt("PackageId");
            pack = new MerchItemPackage();
            pack.setPackageid(packageid);
            pack.setMesos(rs.getInt("Mesos"));
            pack.setSentTime(rs.getLong("time"));
            rs.close();

            Map<Integer, Pair<IItem, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid, accountid);
            if (items != null) {
                List<IItem> iters = new ArrayList<>();
                for (Pair<IItem, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }

            return pack;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
