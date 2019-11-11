var status = -1;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		cm.sendSimple("不同的魚類組合可以換取不同的獎勵哦~~\r\n#b#L0#銀魚3cm、銀魚5cm、銀魚、6.5cm、銀魚10cm各10隻#l#k\r\n#b#L1#鯉魚30cm、鯉魚53cm、鯉魚60cm、鯉魚113cm各10隻#l#k\r\n#L2##b旗魚120cm、旗魚128cm、旗魚140cm、旗魚148cm各10隻#l#k\r\n#L3##b鮭魚150cm、鮭魚166cm、鮭魚183cm、鮭魚227cm各10隻#l#k");
	} else if (status == 1) {
		switch (selection) {
		case 3:
			if (cm.canHold(2000004, 4)) {
				if (cm.haveItem(4031631, 10) && cm.haveItem(4031645, 10) && cm.haveItem(4031646, 10) && cm.haveItem(4031647, 10)) {
					cm.gainItem(4031631, -10);
					cm.gainItem(4031645, -10);
					cm.gainItem(4031646, -10);
					cm.gainItem(4031647, -10);
					cm.gainItem(2000004, 4);
					cm.sendNext("給您一點獎勵吧！");
				} else {
					cm.sendNext("你似乎少了什麼我需要的材料....");
				}
			} else {
				cm.sendNext("請確認消耗欄位有沒有滿！");
			}
			break;
		case 2:
			if (cm.canHold(2000004, 3)) {
				if (cm.haveItem(4031628, 10) && cm.haveItem(4031641, 10) && cm.haveItem(4031643, 10) && cm.haveItem(4031644, 10)) {
					cm.gainItem(4031628, -10);
					cm.gainItem(4031641, -10);
					cm.gainItem(4031643, -10);
					cm.gainItem(4031644, -10);
					cm.gainItem(2000004, 3);
					cm.sendNext("給您一點獎勵吧！");
				} else {
					cm.sendNext("你似乎少了什麼我需要的材料....");
				}
			} else {
				cm.sendNext("請確認消耗欄位有沒有滿！");
			}
			break;
		case 1:
			if (cm.canHold(2000004, 2)) {
				if (cm.haveItem(4031630, 10) && cm.haveItem(4031637, 10) && cm.haveItem(4031638, 10) && cm.haveItem(4031640, 10)) {
					cm.gainItem(4031630, -10);
					cm.gainItem(4031637, -10);
					cm.gainItem(4031638, -10);
					cm.gainItem(4031640, -10);
					cm.gainItem(2000004, 2);
					cm.sendNext("給您一點獎勵吧！");
				} else {
					cm.sendNext("你似乎少了什麼我需要的材料....");
				}
			} else {
				cm.sendNext("請確認消耗欄位有沒有滿！");
			}
			break;
		default:
			if (cm.canHold(2000004)) {
				if (cm.haveItem(4031627, 10) && cm.haveItem(4031634, 10) && cm.haveItem(4031635, 10) && cm.haveItem(4031636, 10)) {
					cm.gainItem(4031627, -10);
					cm.gainItem(4031634, -10);
					cm.gainItem(4031635, -10);
					cm.gainItem(4031636, -10);
					cm.gainItem(2000004, 1);
					cm.sendNext("給您一點獎勵吧！");
				} else {
					cm.sendNext("你似乎少了什麼我需要的材料....");
				}
			} else {
				cm.sendNext("請確認消耗欄位有沒有滿！");
			}
			break;
		}
		cm.dispose();
	}
}
