
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
// Jane the Alchemist
var status = -1;
var amount = -1;
var items = [
    [2000002, 310],
    [2022003, 1060],
    [2022000, 1600],
    [2001000, 3120]
];
var item;

function start() {
    if (cm.getQuestStatus(2013))
        cm.sendNext("這是你...謝謝你，我能得到很多完成。現在我已經做了一堆物品。如果你需要什麼，讓我知道.");
    else {
        if (cm.getQuestStatus(2010))
            cm.sendNext("你似乎沒有強大到足以能夠購買我的藥水......");
        else
            cm.sendOk("需要完成任務才可以跟我買藥水喔!");
        cm.dispose();
    }
}

function action(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (mode == 0 && type == 1)
            cm.sendNext("我仍然有不少你以前把我的材料。這些項目都存在這樣把你的時間選擇...");
        cm.dispose();
        return;
    }
    if (status == 0) {
        var selStr = "你想購買那些藥水??#b";
        for (var i = 0; i < items.length; i++)
            selStr += "\r\n#L" + i + "##i" + items[i][0] + "# (價格 : " + items[i][1] + " 楓幣)#l";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        item = items[selection];
        var recHpMp = ["300 HP.", "1000 HP.", "800 MP", "1000 HP and MP."];
        cm.sendGetNumber("你想買 #b#t" + item[0] + "##k? #t" + item[0] + "# 允許您恢復 " + recHpMp[selection] + " 你想買多少個??", 1, 1, 100);
    } else if (status == 2) {
        cm.sendYesNo("你購買這些 #r" + selection + "#k #b#t" + item[0] + "#(s)#k? #t" + item[0] + "# 費用為 " + item[1] + " 楓幣 為一體，所以總出來是 #r" + (item[1] * selection) + "#k 楓幣.");
        amount = selection;
    } else if (status == 3) {
        if (cm.getMeso() < item[1] * amount)
            cm.sendNext("確認你的楓幣是否足夠,和檢查你的消耗攔是否足夠,如果有你至少 #r" + (item[1] * selectedItem) + "#k 楓幣.");
        else {
            if (cm.canHold(item[0])) {
                cm.gainMeso(-item[1] * amount);
                cm.gainItem(item[0], amount);
                cm.sendNext("謝謝你的到來。東西在這裡可以隨時進行，所以如果你需要的東西，歡迎再來.");
            } else
                cm.sendNext("確認你的楓幣是否足夠,和檢查你的消耗攔是否足夠..");
        }
        cm.dispose();
    }
}