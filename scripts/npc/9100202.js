var status = -1;

function start() {
	cm.sendYesNo("您要開始進行小鋼珠遊戲嗎？");
}

function action(mode, type, selection) {
	if (mode == -1)
		cm.dispose();
	else {
		if (mode == 1)
			status++;
		else {
			cm.sendNext("等您想玩的時候再來找我。");
			cm.dispose();
			return;
		}
		if (status == 0) {
			cm.showPachinko();
			cm.dispose();
		}
	}
}
