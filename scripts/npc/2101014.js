/**
 * @author: Eric
 * @npc: Cesar
 * @func: Ariant PQ
*/

var status = 0;
var sel;
var empty = [false, false, false];
var closed = false;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection){
    (mode == 1 ? status++ : status--);
    if (status == 0) {
		cm.sendSimple("#e<沙漠場競賽:挑戰>#n\r\n歡迎來到這裡，勇士你可以與其他玩家一決高下！！#b\r\n#L0#準備進入競技場！\r\n#L1#說明沙漠競技場需求\r\n#L2#什麼是沙漠競技場\r\n#L3#查看今天剩餘的挑戰次數\r\n#L4#領取沙漠競技場的獎勵");
	} else if (status == 1) {
		if (selection == 0) {
			if (closed || (cm.getPlayer().getLevel() < 19 || cm.getPlayer().getLevel() > 30 && !cm.getPlayer().isGM())) {
				cm.sendOk(closed ? "沙漠競技場暫時沒有開放！" : "你的等級需求不符合！");
				cm.dispose();
				return;
			}
			var text = "你想要做什麼呢？？#b";
			for(var i = 0; i < 3; i += 1)
				if (cm.getPlayerCount(980010100 + (i * 100)) > 0)
					if (cm.getPlayerCount(980010101 + (i * 100)) > 0)
						continue;
					else
						text += "\r\n#L" + i + "# 房間 " + (i + 1) + " (" + cm.getPlayerCount(980010100 + (i * 100)) + "/" + cm.getPlayer().getAriantSlotsRoom(i) + " 玩家 房主: " + cm.getPlayer().getAriantRoomLeaderName(i) + ")#l";
				else {
					empty[i] = true;
					text += "\r\n#L" + i + "# 房間 " + (i + 1) + " (空)#l";
					if (cm.getPlayer().getAriantRoomLeaderName(i) != "")
						cm.getPlayer().removeAriantRoom(i);
				}
			cm.sendSimple(text);
		} else if (selection == 1) {
			cm.sendNext("沙漠競技場遊玩規則很簡單的！\r\n - #e等級限制#n : #r(需求 : 21 - 30 )#k\r\n - #e時間限制#n : 8 分鐘\r\n - #e人數限制#n : 2-6\r\n");
			cm.dispose();
		} else if (selection == 2) {
			status = 9;
			cm.sendNext("你想知道如何成為 #r優秀的冠軍#k 得到 #b王者#k的稱號嗎? 我會慢慢的解釋給你聽的。");
		} else if (selection == 3) {
			var ariant = cm.getQuestRecord(150139);
			var data = ariant.getCustomData();
			if (data == null) {
				ariant.setCustomData("10");
				data = "10";
			}
			cm.sendNext("#r#h ##k, 你今天還可以挑戰 #b" + parseInt(data) + "#k 次沙漠競技場");
			cm.dispose();
		} else if (selection == 4) {
			status = 4;
			cm.sendNext("沙漠競技場點數高達150分以上可以領取 : #b冠軍戒指#k.\r\n這是真正的鬥士的象徵.");
		}
	} else if (status == 2) {
		var sel = selection;
		if(cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
            empty[sel] = false;
        else if(cm.getPlayer().getAriantRoomLeaderName(sel) != "") {
			cm.warp(980010100 + (sel * 100));
            cm.dispose();
            return;
        }
        if (!empty[sel]) {
            cm.sendNext("另一個玩家已經比你先創立這個房間了，我建議你要麼加入其他人，要麼再找新的空房間創建！");
            cm.dispose();
            return;
        }
		cm.getPlayer().setApprentice(sel);
        cm.sendGetNumber("設置這局競技場人數 (2~6 人)", 0, 2, 6);
	} else if (status == 3) {
		var sel = cm.getPlayer().getApprentice();
		if (cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
			empty[sel] = false;
        if (!empty[sel]) {
            cm.sendNext("另一個玩家已經比你先創立這個房間了，我建議你要麼加入其他人，要麼再找新的空房間創建！");
            cm.dispose();
            return;
        }
        cm.getPlayer().setAriantRoomLeader(sel, cm.getPlayer().getName());
        cm.getPlayer().setAriantSlotRoom(sel, selection);
        cm.warp(980010100 + (sel * 100));
		cm.getPlayer().setApprentice(0);
        cm.dispose();
	} else if (status == 5) {
		cm.sendNextPrev("問題是你目前只有 #b0#k 分 你現在需要 #b150#k 分才能得到 #b冠軍戒指#k");
	} else if (status == 6) { 
		cm.dispose();
	} else if (status == 10) {
		cm.sendNextPrev("讓我告訴你 第一個簡單的規則那就是...#r加油#k。");
	} else if (status == 11) {
		cm.sendOk("#d那麼接下來的時間就交給你了！#k");
		cm.dispose();
	}
}
