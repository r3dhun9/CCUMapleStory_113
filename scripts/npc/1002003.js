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
/* Author: Xterminator
	NPC Name: 		Mr. Goldstein
	Map(s): 		Victoria Road : Lith Harbour (104000000)
	Description:		Extends Buddy List
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendNext("我想你是邊緣人所以才不需要好友對吧??\r\n開玩笑假如你真的需要可以來找我的唷!");
            cm.dispose();
            return;
        } else if (status >= 1) {
            cm.sendNext("我不認為你沒有朋友，你只是不想花25萬楓幣來擴充自己的好友欄!");
            cm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        cm.sendYesNo("我希望我能盡可能昨天...嗯，你好！難道你不希望延長你的好友列表？你看起來像有人誰就會有一大堆的朋友......好了，你有什麼感想？隨著一些錢，我可以做到這一點你。但要記住，它僅適用於一個字符的時間，所以不會影響您的任何其他字符在您的帳戶。你想擴展您的好友列表？");
    } else if (status == 1) {
        cm.sendYesNo("好吧，良好的通話！這並不是說貴實際。 #b250,000 楓幣，我會添加5個插槽到你的好友列表#k中。不，我不會單獨出售。一旦你購買它，這將是永久你的好友列表上。所以，如果你是那些需要更多的空間有一個，那麼你還不如去做。你怎麼看？你會花25萬楓幣嗎？");
    } else if (status == 2) {
        var capacity = cm.getBuddyCapacity();
        if (capacity >= 100 || cm.getMeso() < 250000) {
            cm.sendNext("嘿 你確定你有 #b250,000 楓幣#k? 如果足夠確認是不是你的好友欄已經 #b100#k 格了..");
        } else {
            var newcapacity = capacity + 5;
            cm.gainMeso(-250000);
            cm.updateBuddyCapacity(newcapacity);
            cm.sendOk("好了已經多增加5個好友欄了..如果你還需要可以再來找我..當然他並不是免費的!");
        }
        cm.dispose();
    }
}