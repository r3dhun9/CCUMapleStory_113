package client.messages.commands;

import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.World;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Map.Entry;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.MapleMap;
import tools.ArrayMap;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;

/**
 *
 * @author Emilyx3
 */
public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.GM;
    }

    public static class Job extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().changeJob(Integer.parseInt(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!job <職業代碼> - 更換職業").toString();
        }
    }

    public static class maxmeso extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!maxmeso - 楓幣滿").toString();
        }
    }

    public static class mesos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().gainMeso(Integer.parseInt(splitted[1]), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesos <需要的數量> - 得到楓幣").toString();
        }
    }

    public static class Drop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.寵物(itemId)) {
                c.getPlayer().dropMessage(5, "寵物請到購物商城購買.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - 物品不存在");
            } else {
                IItem toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                toDrop.setOwner(c.getPlayer().getName());
                toDrop.setGMLog(c.getPlayer().getName());

                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dropitem <道具ID> - 掉落道具").toString();
        }
    }

    public static class Notice extends CommandExecute {

        private static int getNoticeType(String typestring) {
            switch (typestring) {
                case "n":
                    return 0;
                case "p":
                    return 1;
                case "l":
                    return 2;
                case "nv":
                    return 5;
                case "v":
                    return 5;
                case "b":
                    return 6;
            }
            return -1;
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int joinmod = 1;
            int range = -1;
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            if (splitted.length < tfrom + 1) {
                return false;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[注意事項]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            if (splitted.length < joinmod + 1) {
                return false;
            }
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            MaplePacket packet = MaplePacketCreator.broadcastMessage(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet.getBytes());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!notice <n|p|l|nv|v|b> <m|c|w> <message> - 公告").toString();
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int range = -1;
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 2;
            }
            MaplePacket packet = MaplePacketCreator.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet.getBytes());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!yellow <m|c|w> <message> - 黃色公告").toString();
        }
    }

    public static class Y extends Yellow {
    }

    public static class NpcNotice extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length <= 2) {
                return false;
            }
            String msg = splitted[2];
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if(npc != null) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getNPCTalk(npcId, (byte) 0, msg, "00 00", (byte) 0).getBytes());
            } else {
                c.getPlayer().dropMessage(5, "很抱歉，此NPC "+ splitted[1] +" 不存在.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!NpcNotice <npcid> <message> - 用NPC發訊息").toString();
        }
    }

    public static class Item extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);

            if (!c.getPlayer().isAdmin()) {
                for (int i : GameConstants.itemBlock) {
                    if (itemId == i) {
                        c.getPlayer().dropMessage(5, "很抱歉，此物品您的GM等級無法呼叫.");
                        return true;
                    }
                }
            }

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.寵物(itemId)) {
                MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    MapleInventoryManipulator.addById(c, itemId, (short) 1, c.getPlayer().getName(), pet, ii.getPetLife(itemId));
                }
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - 物品不存在");
            } else {
                IItem item;
                byte flag = 0;
                flag |= ItemFlag.LOCK.getValue();

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                    item.setFlag(flag);

                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                    if (GameConstants.getInventoryType(itemId) != MapleInventoryType.USE) {
                        item.setFlag(flag);
                    }
                }
                item.setOwner(c.getPlayer().getName());
                item.setGMLog(c.getPlayer().getName());

                MapleInventoryManipulator.addbyItem(c, item);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!item <道具ID> - 取得道具").toString();
        }
    }

    public static class WarpHere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
            } else {
                int ch = World.Find.findChannel(splitted[1]);
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "找不到");

                } else {
                    victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                    c.getPlayer().dropMessage(5, "正在把玩家傳到這來");
                    victim.dropMessage(5, "正在傳送到GM那邊");
                    if (victim.getMapId() != c.getPlayer().getMapId()) {
                        final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                        victim.changeMap(mapp, mapp.getPortal(0));
                    }
                    victim.changeChannel(c.getChannel());
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!warphere 把玩家傳送到這裡").toString();
        }
    }

    public static class WarpMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "地圖不存在。");
                    return false;
                }
                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!WarpMap [地圖代碼] - 把地圖上的人全部傳到那張地圖").toString();
        }
    }

    public static class Level extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setLevel(Short.parseShort(splitted[1]));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!level [等級] - 更改等級").toString();
        }
    }

    public static class LevelUp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (c.getPlayer().getLevel() < 200) {
                c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) + 1, true, false, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!levelup - 等級上升").toString();
        }
    }

    public static class LevelUpTo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            while (c.getPlayer().getLevel() < Integer.parseInt(splitted[1])) {
                if (c.getPlayer().getLevel() < 255) {
                    c.getPlayer().levelUp();
                    c.getPlayer().setExp(0);
                    c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!levelupto [等級數量] - 等級上升").toString();
        }
    }

    public static class 清理背包 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            java.util.Map<Pair<Short, Short>, MapleInventoryType> eqs = new ArrayMap<>();
            switch (splitted[1]) {
                case "全部":
                    for (MapleInventoryType type : MapleInventoryType.values()) {
                        for (IItem item : c.getPlayer().getInventory(type)) {
                            eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), type);
                        }
                    }
                    break;
                case "身上裝備":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIPPED);
                    }
                    break;
                case "裝備":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIP);
                    }
                    break;
                case "消耗":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.USE);
                    }
                    break;
                case "裝飾":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                        MapleInventoryType put = eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.SETUP);
                    }
                    break;
                case "其他":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.ETC);
                    }
                    break;
                case "現金":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.CASH);
                    }
                    break;
                default:
                    return false;
            }
            for (Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), eq.getKey().left, eq.getKey().right, false, false);
            }
            c.getPlayer().dropMessage(5, "已經清除" + splitted[1] + "欄。");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!清理背包 <全部/身上裝備/裝備/消耗/裝飾/其他/現金> - 清理道具欄").toString();
        }
    }
}
