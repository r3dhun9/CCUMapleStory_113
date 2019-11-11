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
import client.MapleCharacterUtil;
import constants.ServerConstants;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class UserInterfaceHandler {

    public static final void CygnusSummonNPCRequest(final MapleClient c) {
        if (c.getPlayer().getJob() == 2000) {
            NPCScriptManager.getInstance().start(c, 1202000);
        } else if (c.getPlayer().getJob() == 1000) {
            NPCScriptManager.getInstance().start(c, 1101008);
        }
    }

    public static final void XMASSurprise(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().dropMessage(1,"月光寶盒尚未開放。");
    }
}
