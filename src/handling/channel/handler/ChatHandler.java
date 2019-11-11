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

import client.MapleClient;
import client.MapleCharacter;
import client.messages.CommandProcessor;
import constants.ServerConstants.CommandType;
import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import static handling.world.World.Alliance.getAlliance;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import server.ServerConfig;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class ChatHandler {

    public static final void GeneralChat(final String text, final byte unk, final MapleClient c, final MapleCharacter chr) {
        if (chr != null && !CommandProcessor.processCommand(c, text, CommandType.NORMAL)) {
            if (!chr.isGM() && text.length() >= 80) {
                return;
            }

            if (chr.getCanTalk() || chr.isStaff()) {
                MapleMap map = c.getPlayer().getMap();
                String gg = "";
                if (c.getPlayer().getGMChat()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
                    return;
                }
                if (c.getPlayer().gmLevel() == 5 && !chr.isHidden()) {
                    if (c.getPlayer().getCTitle()) {
                        gg = c.getPlayer().getChatTitle();
                    } else {
                        gg = "<超級管理員>";
                    }
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat(gg + " " + c.getPlayer().getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
                } else if (c.getPlayer().gmLevel() == 4 && !chr.isHidden()) {
                    if (c.getPlayer().getCTitle()) {
                        gg = c.getPlayer().getChatTitle();
                    } else {
                        gg = "<領導者>";
                    }
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat(gg + " " + c.getPlayer().getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
                } else if (c.getPlayer().gmLevel() == 3 && !chr.isHidden()) {
                    if (c.getPlayer().getCTitle()) {
                        gg = c.getPlayer().getChatTitle();
                    } else {
                        gg = "<管理員>";
                    }
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat(gg + " " + c.getPlayer().getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
                } else if (c.getPlayer().gmLevel() == 2 && !chr.isHidden()) {
                    if (c.getPlayer().getCTitle()) {
                        gg = c.getPlayer().getChatTitle();
                    } else {
                        gg = "<巡察員>";
                    }
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat(gg + " " + c.getPlayer().getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
                } else if (c.getPlayer().gmLevel() == 1 && !chr.isHidden()) {
                    if (c.getPlayer().getCTitle()) {
                        gg = c.getPlayer().getChatTitle();
                    } else {
                        gg = "<新實習生>";
                    }
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat(gg + " " + c.getPlayer().getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
                } else {
                    StringBuilder sb;
                    if (!c.getPlayer().isGM()) {
                        chr.getCheatTracker().checkMsg();
                        map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), c.getPlayer().getPosition());
                        sb = new StringBuilder("[普通聊天偷聽] 玩家：" + chr.getName() + " 地圖：" + chr.getMapId() + "：  " + text);
                        if (ServerConfig.isLogChat()) {
                            FilePrinter.print(FilePrinter.GeneralChatLog + chr.getName() + ".txt", sb.toString());
                        }
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                                if (chr_.get玩家私聊1() && chr_.isGM()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } else {
                        map.broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
                    }
                }
            } else {
                c.sendPacket(MaplePacketCreator.getItemNotice( "在這個地方不能說話。"));
            }
        }
    }

    public static final void Others(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        final byte numRecipients = slea.readByte();
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = slea.readInt();
        }
        final String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.sendPacket(MaplePacketCreator.getItemNotice( "在這個地方不能說話。"));
            return;
        }
        if (CommandProcessor.processCommand(c, chattext, CommandType.NORMAL)) {
            return;
        }
        chr.getCheatTracker().checkMsg();
        StringBuilder sb;
        switch (type) {
            case 0:
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                sb = new StringBuilder("[好友聊天偷聽] 玩家：" + chr.getName() + " 地圖：" + chr.getMapId() + " ：  " + chattext);
                if (ServerConfig.isLogChat()) {
                    FilePrinter.print(FilePrinter.GeneralChatLog + chr.getName() + ".txt", sb.toString());
                }
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                        if (chr_.get玩家私聊2() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
            case 1:
                if (chr.getParty() == null) {
                    break;
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                sb = new StringBuilder("[組隊聊天偷聽] 玩家：" + chr.getName() + " 地圖：" + chr.getMapId() + " ：  " + chattext);
                if (ServerConfig.isLogChat()) {
                    FilePrinter.print(FilePrinter.GeneralChatLog + chr.getName() + ".txt", sb.toString());
                }
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr_.get玩家私聊2() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
            case 2:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                sb = new StringBuilder("[公會聊天偷聽] " + "公會：" + chr.getGuild().getName() + "玩家：" + chr.getName() + " 地圖：" + chr.getMapId() + " ：  " + chattext);
                if (ServerConfig.isLogChat()) {
                    FilePrinter.print(FilePrinter.GuildChatLog + chr.getName() + ".txt", sb.toString());
                }
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr_.get玩家私聊3() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
            case 3:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                sb = new StringBuilder("[聯盟聊天偷聽] " + "公會：" + chr.getGuild().getName() + " 玩家：" + chr.getName() + " 地圖：" + chr.getMapId() + " ：  " + chattext);
                if (ServerConfig.isLogChat()) {
                    FilePrinter.print(FilePrinter.GuildChatLog + chr.getName() + ".txt", sb.toString());
                }
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr_.get玩家私聊3() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
        }
    }

    public static final void Messenger(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        String input;
        MapleMessenger messenger = c.getPlayer().getMessenger();
        byte mode = slea.readByte();
        switch (mode) {
            case 0x00: // open
                if (messenger == null) {
                    int messengerid = slea.readInt();
                    if (messengerid == 0) { // create
                        c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else { // join
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case 0x03: // invite

                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 4) {
                        return;
                    }
                    input = slea.readMapleAsciiString();
                    final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isGM() || c.getPlayer().isGM()) {
                                c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 1));
                                target.getClient().sendPacket(MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 0));
                            }
                        } else {
                            c.sendPacket(MaplePacketCreator.messengerChat(c.getPlayer().getName() + " : " + target.getName() + " 忙碌中."));
                        }
                    } else if (World.isConnected(input)) {
                        World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isGM());
                    } else {
                        c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 0));
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().sendPacket(MaplePacketCreator.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else // Other channel
                {
                    if (!c.getPlayer().isGM()) {
                        World.Messenger.declineChat(targeted, c.getPlayer().getName());
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    World.Messenger.messengerChat(messenger.getId(), slea.readMapleAsciiString(), c.getPlayer().getName());
                }
                break;
            default:
                System.out.println("Unhandled Messenger operation : " + String.valueOf(mode));

        }
    }

    public static final void WhisperFind(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();

        switch (mode) {
            case 68: //buddy
            case 5: { // Find

                final String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if (!player.isGM() || c.getPlayer().isGM() && player.isGM()) {

                        c.sendPacket(MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    } else {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }
                } else { // Not found
                    int ch = World.Find.findChannel(recipient);
                    if (ch > 0) {
                        player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                        if (player == null) {
                            break;
                        }

                        if (!player.isGM() || (c.getPlayer().isGM() && player.isGM())) {
                            c.sendPacket(MaplePacketCreator.getFindReply(recipient, (byte) ch, mode == 68));
                        } else {
                            c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                        }
                        return;

                    }
                    if (ch == -10) {
                        c.sendPacket(MaplePacketCreator.getFindReplyWithCS(recipient, mode == 68));
                    } else if (ch == -20) {
                        c.sendPacket(MaplePacketCreator.getFindReplyWithMTS(recipient, mode == 68));
                    } else {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }
                }
                break;
            }
            case 6: { // Whisper
                if (!c.getPlayer().getCanTalk()) {
                    c.sendPacket(MaplePacketCreator.getItemNotice( "在這個地方不能說話。"));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();
                final int ch = World.Find.findChannel(recipient);
                if (ch > 0) {
                    MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) {
                        break;
                    }
                    StringBuilder sb;
                    player.getClient().sendPacket(MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    sb = new StringBuilder("[密語聊天偷聽] 玩家：" + c.getPlayer().getName() + " -> " + player.getName() + " ：" + text);
                    if (ServerConfig.isLogChat()) {
                        FilePrinter.print(FilePrinter.WishperChatLog, sb.toString());
                    }
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                            if (chr_.get玩家私聊2() && chr_.isGM()) {
                                chr_.dropMessage(sb.toString());
                            }
                        }
                    }
                    if (!c.getPlayer().isGM() && player.isGM()) {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    } else {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
                    }
                } else {
                    c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                }
            }
            break;
        }
    }
}
