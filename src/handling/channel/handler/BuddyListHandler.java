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

import static client.BuddyList.BuddyOperation.ADDED;
import static client.BuddyList.BuddyOperation.DELETED;

import client.BuddyList;
import client.BuddyEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.BuddyList.BuddyAddResult;
import client.BuddyList.BuddyOperation;
import handling.channel.ChannelServer;
import handling.world.World;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class BuddyListHandler {

    private static void nextPendingRequest(final MapleClient c) {
        BuddyEntry pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            c.sendPacket(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getCharacterId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
    }

    public static final void BuddyOperationHandler(final SeekableLittleEndianAccessor slea,
            final MapleClient client) {

        final MapleCharacter player = client.getPlayer();

        final int mode = slea.readByte();

        final BuddyList buddyList = player.getBuddylist();

        switch (mode) {
            case 0: {
                final int unknow1 = slea.readInt();
                final int unknow2 = slea.readInt();
                client.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
                break;
            }
            case 1: {
                final String buddyName = slea.readMapleAsciiString();
                final String buddyGroup = slea.readMapleAsciiString();
                final BuddyEntry oldBuddy = buddyList.get(buddyName);

                if (buddyName.length() > 13 || buddyGroup.length() > 16) {
                    nextPendingRequest(client);
                    return;
                }

                /* 檢查好友是否存在 */
                if (oldBuddy != null) {
                    if (oldBuddy.getGroup().equals(buddyGroup)) {
                        client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                    } else {
                        /* 如果存在，群組不一樣則改群組*/
                        oldBuddy.setGroup(buddyGroup);
                        client.sendPacket(MaplePacketCreator.updateBuddylist(buddyList.getBuddies()));
                    }
                    nextPendingRequest(client);
                    return;
                }

                /* 檢查好友是否滿了 */
                if (buddyList.isFull()) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                    return;
                }

                /* 從整個遊戲找這個名字的角色所在的頻道 */
                int buddyChannel = World.Find.findChannel(buddyName);
                MapleCharacter buddyChar;
                BuddyEntry buddyEntry = null;
                BuddyAddResult reqRes = null;

                if (buddyChannel > 0) {
                    buddyChar = ChannelServer.getInstance(buddyChannel)
                            .getPlayerStorage().getCharacterByName(buddyName);
                    /* 如果是GM則無法被普通玩家加入 */
                    if (!buddyChar.isGM() || player.isGM()) {
                        buddyEntry = new BuddyEntry(buddyChar.getName(),
                                buddyChar.getId(),
                                buddyGroup,
                                buddyChannel,
                                false,
                                buddyChar.getLevel(),
                                buddyChar.getJob());
                    }
                } else {
                    buddyEntry = BuddyEntry.getByNameFromDB(buddyName);
                }

                /* 無此角色*/
                if (buddyEntry == null) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 15));
                    nextPendingRequest(client);
                    return;
                }

                /* 傳給對方好友邀請 */
                if (buddyChannel > 0) {
                    reqRes = World.Buddy.requestBuddyAdd(buddyName,
                            player.getClient().getChannel(),
                            player.getId(),
                            player.getName(),
                            player.getLevel(),
                            player.getJob());
                } else {

                    final int buddyCount = BuddyList.getBuddyCount(buddyEntry.getCharacterId(), 0);

                    if (buddyCount == -1) {
                        throw new RuntimeException("Result set expected");
                    } else if (buddyCount >= BuddyList.getBuddyCapacity(buddyEntry.getCharacterId())) {
                        reqRes = BuddyAddResult.BUDDYLIST_FULL;
                    }
                    int pending = BuddyList.getBuddyPending(buddyEntry.getCharacterId(), player.getId());
                    if (pending > -1) {
                        reqRes = BuddyAddResult.ALREADY_ON_LIST;
                    }
                }

                if (reqRes == BuddyAddResult.BUDDYLIST_FULL) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 12));
                    break;
                } else {
                    if (reqRes == BuddyAddResult.ALREADY_ON_LIST && buddyChannel > 0) {
                        notifyRemoteChannel(client, buddyChannel, buddyEntry.getCharacterId(), buddyGroup, ADDED);
                    } else {
                        BuddyList.addBuddyToDB(player, buddyEntry);
                    }
                    buddyList.put(buddyEntry);
                    client.sendPacket(MaplePacketCreator.updateBuddylist(buddyList.getBuddies()));
                }
                nextPendingRequest(client);
                break;
            }

            case 2: {
                final int buddyCharId = slea.readInt();

                if (buddyList.isFull()) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                    nextPendingRequest(client);
                    return;
                }

                final int buddyChannel = World.Find.findChannel(buddyCharId);
                BuddyEntry buddy;

                if (buddyChannel < 0) {
                    buddy = BuddyEntry.getByIdfFromDB(buddyCharId);
                } else {
                    final MapleCharacter buddyChar = ChannelServer.getInstance(buddyChannel).getPlayerStorage().getCharacterById(buddyCharId);
                    buddy = new BuddyEntry(
                            buddyChar.getName(),
                            buddyChar.getId(),
                            BuddyList.DEFAULT_GROUP,
                            buddyChannel,
                            false,
                            buddyChar.getLevel(),
                            buddyChar.getJob()
                    );

                }

                if (buddy == null) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                } else {
                    buddyList.put(buddy);
                    client.sendPacket(MaplePacketCreator.updateBuddylist(buddyList.getBuddies()));
                    notifyRemoteChannel(client, buddyChannel, buddyCharId, "其他", ADDED);
                }
                nextPendingRequest(client);
                break;
            }

            case 3: {
                final int buddyCharId = slea.readInt();
                final BuddyEntry buddy = buddyList.get(buddyCharId);
                if (buddy != null && buddy.isVisible()) {
                    notifyRemoteChannel(client, World.Find.findChannel(buddyCharId), buddyCharId, buddy.getGroup(), DELETED);
                }
                buddyList.remove(buddyCharId);
                client.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
                nextPendingRequest(client);
                break;
            }
            case 82: {
                final int unknow1 = slea.readShort();
                final int unknow2 = slea.readByte();
                client.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
                break;
            }
            default: {
                FilePrinter.printError("BuddyListHandler.txt", "Unknown Buddylist Operation " + String.valueOf(mode) + " " + slea.toString());
                break;
            }
        }

    }

    private static void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final String group, final BuddyOperation operation) {
        final MapleCharacter player = c.getPlayer();

        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, player.getLevel(), player.getJob(), group);
        }
    }
}
