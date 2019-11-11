/* 惡魔文件兌換 */

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
		cm.sendSimple("您看起來有一個血光之災...\r\n#L0#我解除惡魔文件#l\r\n#L1#沒事了..#l");
	} else if (status == 1) {
		if (selection == 0) {
			if (cm.canHold(2000004, 10)) {
				cm.sendNext("請確認背包是否有足夠的空間。");
			}
			if (cm.haveItem(4031019, 1)) {
				cm.gainItem(4031019, -1);
				cm.gainItem(2000004, 10);
				cm.sendNext("我已經幫你解除血光之災了，請查看背包欄位！");
			} else {
				cm.sendNext("您看起來沒有血光之災阿....");
			}
		} else if (selection == 1) {
			cm.sendNext("沒事別來打擾我。");
		}
		cm.dispose();
	}
}
