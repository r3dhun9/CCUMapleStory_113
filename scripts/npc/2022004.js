/*
 * Tylus
 */

var status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		if (cm.getQuestStatus(6192) == 1) {
			cm.sendOk("謝謝你保護了我，現在該是我報答你的時候了。");
		} else {
			cm.warp(211000001, 0);
			cm.dispose();
		}
	} else if (status == 1) {
		if (!cm.haveItem(4031495)) {
			if (cm.canHold(4031495)) {
				cm.gainItem(4031495, 1);
				cm.warp(211000001, 0);
				cm.dispose();
			} else {
				cm.sendOk("請確認背包欄位有沒有空格。");
				cm.safeDispose();
			}
		} else {
			cm.warp(211000001, 0);
			cm.dispose();
		}
	}
}
