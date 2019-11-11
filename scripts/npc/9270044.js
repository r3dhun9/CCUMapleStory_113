var status = 0;

function start() {
	cm.sendYesNo("您想要離開這裡??");
}

function action(mode, type, selection) {
	if (mode != 1) {
		if (mode == 0)
			cm.sendOk("需要的話再來找我。");
		cm.dispose();
		return;
	}
	status++;
	if (status == 1) {
		cm.warp(541020700, 6);
		cm.dispose();
	}
}
