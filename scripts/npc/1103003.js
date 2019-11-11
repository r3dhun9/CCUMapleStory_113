/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
	Author: Biscuit
*/

var status = 0;

function start() {
    action();
}

function action() {
    status++;
    if(status == 1) {
        if(cm.isQuestActive(20718)) {
            cm.sendOk("…黑影出現攻擊你嗎？#p1032001#的家裡會有這種事情發生…這應該是陰謀吧。\r\n\r\n#fUI/UIWindow.img/Quest/reward#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 4000 exp");
        } else {
            cm.dispose();
        }
    } else if (status == 2) {
        cm.forceCompleteQuest(20718);
        cm.gainExp(4000);
        cm.dispose();
    } else {
        cm.dispose();
    }
}
