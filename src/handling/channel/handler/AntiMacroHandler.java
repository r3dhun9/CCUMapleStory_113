/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import handling.world.MapleAntiMacro;
import server.MapleInventoryManipulator;
import tools.MaplePacketCreator;
import tools.data.input.LittleEndianAccessor;

/**
 *
 * @author Weber
 */
public class AntiMacroHandler {

    public static void AntiMacro(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, boolean isItem) {
        if (c == null || chr == null || chr.getMap() == null) {
            return;
        }
        if (!isItem && !chr.isGM()) {
            return;
        }

        // 偵測角色可測謊狀態處理
        String toAntiChrName = slea.readMapleAsciiString();
        MapleCharacter victim = chr.getMap().getCharacterByName(toAntiChrName);
        if (victim == null || chr.getGMLevel() < victim.getGMLevel()) {
            // 找不到測謊角色
            c.sendPacket(MaplePacketCreator.AntiMacro.cantFindPlayer());
            return;
        }

        short slot = 0;
        // 使用測謊機道具處理
        if (isItem) {
            slot = slea.readShort();
            IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
            int itemId = slea.readInt();

            // 偵測使用的測謊機道具是否合理
            switch (itemId) {
                case 2190000: {
                    if (toUse.getItemId() != itemId) {
                        return;
                    }
                    break;
                }
                default: {
                    chr.dropMessage("這個測謊機道具暫時不能用,請回報給管理員。");
                    return;
                }
            }
        }

        if (MapleAntiMacro.startAntiMacro(chr, victim, (byte) (isItem ? MapleAntiMacro.ITEM_ANTI : MapleAntiMacro.GM_SKILL_ANTI)) && isItem) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
    }

    public static void OldAntiMacroQuestion(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (c == null || chr == null) {
            return;
        }
        if (MapleAntiMacro.getCharacterState(chr) != MapleAntiMacro.ANTI_NOW) {
            return;
        }
        String inputCode = slea.readMapleAsciiString();
        if (MapleAntiMacro.verifyCode(chr.getName(), inputCode)) {
            MapleAntiMacro.antiSuccess(chr);
        } else {
            MapleAntiMacro.antiReduce(chr);
        }
    }
}
