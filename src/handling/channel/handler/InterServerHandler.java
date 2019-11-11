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

import client.BuddyEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import handling.MaplePacket;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.CharacterIdChannelPair;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.util.Collection;
import server.ServerProperties;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.packet.FamilyPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class InterServerHandler {

    private static final boolean isCSOpen = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.cashshop.enable", "false"));

    public static final void EnterCashShop(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        if (World.isShutDown && chr.isGM() == false) {
            c.sendPacket(MaplePacketCreator.serverBlocked(2));
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (!isCSOpen && chr.isGM() == false) {
            c.sendPacket(MaplePacketCreator.serverBlocked(2));
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        int res = chr.saveToDB(false, false);

        if (res == 1) {
            chr.dropMessage(5, "角色保存成功！");
        }
        if (chr.isTestingDPS()) {
            final MapleMonster mm = MapleLifeFactory.getMonster(9001007);
            if (chr.getMap() != null) {
                chr.getMap().killMonster1(mm);
            }
            chr.toggleTestingDPS();
            chr.dropMessage(5, "已停止當前的DPM測試。");
        }
        final ChannelServer ch = ChannelServer.getInstance(c.getChannel());

        chr.changeRemoval();

        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.channelChangeData(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
        ch.removePlayer(chr);
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());

        chr.getMap().removePlayer(chr);
        c.sendPacket(MaplePacketCreator.getChannelChange(CashShopServer.getGatewayIP().split(":")[0], Integer.parseInt(CashShopServer.getGatewayIP().split(":")[1])));
        c.getPlayer().expirationTask(true, false);
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static final void LoggedIn(final int playerid, final MapleClient c) {

        final ChannelServer channelServer = c.getChannelServer();
        MapleCharacter player;
        final CharacterTransfer transfer = channelServer.getPlayerStorage().getPendingCharacter(playerid);

        if (transfer == null) { // Player isn't in storage, probably isn't CC
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
        } else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }

        MapleClient oldClient = World.Client.getClient(player.getAccountID());

        if (oldClient != null) {
            oldClient.disconnect(true, false);
        }
        
        World.Client.addClient(player.getAccountID(), c);
        c.setAccID(player.getAccountID());
        c.loadAccountData(player.getAccountID());

        String LoginMac = LoginServer.getLoginMac(c);
        if (LoginMac != null) {
            c.setLoginMacs(LoginMac);
            LoginServer.removeLoginMac(c);
        }

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close(true);
            return;
        }

        final int state = c.getLoginState();

        if (state != MapleClient.LOGIN_SERVER_TRANSITION && state != MapleClient.CHANGE_CHANNEL) {
            c.getSession().close(true);
            return;
        }

        //對在線上角色做斷線
        ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());

        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        c.setPlayer(player);
        channelServer.addPlayer(player);

        c.sendPacket(MaplePacketCreator.getCharInfo(player));
        if (player.isGM()) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(player);
        }
        c.sendPacket(MaplePacketCreator.temporaryStats_Reset()); // .
        player.getMap().addPlayer(player);

        try {
            player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
            player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
            player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));

            // Start of buddylist
            final Collection<Integer> buddyIds = player.getBuddylist().getBuddiesIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds, player.getGMLevel(), player.isHidden());
            if (player.getParty() != null) {
                //channelServer.getWorldInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
                World.Party.updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            /* 讀取好友 */
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                final BuddyEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() <= 0) {
                c.sendPacket(MaplePacketCreator.fuckGuildInfo(player));
            }

            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.sendPacket(MaplePacketCreator.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<MaplePacket> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (MaplePacket pack : packetList) {
                            if (pack != null) {
                                c.sendPacket(pack);
                            }
                        }
                    }
                } else {
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }
            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.sendPacket(FamilyPacket.getFamilyInfo(player));
        } catch (Exception e) {
            FilePrinter.printError(FilePrinter.LoginError, e);
        }
        c.sendPacket(FamilyPacket.getFamilyData());

        player.sendMacros();
        player.showNote();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        player.baseSkills();

        c.sendPacket(MaplePacketCreator.getKeymap(player.getKeyLayout()));

        for (MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.sendPacket(MaplePacketCreator.updateQuestMobKills(status));
            }
        }
        final BuddyEntry pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddyEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getCharacterId(), "ETC", -1, false, pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
            c.sendPacket(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getCharacterId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
        //player.expirationTask();
        if (player.getJob() == 132) { // DARKKNIGHT
            player.checkBerserk();
        }
        player.spawnClones();
        player.spawnSavedPets();
    }

    public static final void ChangeChannel(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive() || chr.getEventInstance() != null || chr.getMap() == null || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        chr.changeChannel(slea.readByte() + 1);
    }
}
