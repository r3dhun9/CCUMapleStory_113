var status = 0;
var cost = 2000;

function start() {
	cm.sendYesNo("請問是否想去101大道2000楓幣一次??");
}

function action(mode, type, selection) {
	if (mode != 1) {
		if (mode == 0)
			cm.sendOk("既然你不要那就算了~~~");
		cm.dispose();
		return;
	}
	status++;
	if (status == 1) {
		if (cm.getMeso() < cost) {
			cm.sendOk("老哥你的楓幣好像不足。");
			cm.dispose();
		} else {
			cm.gainMeso(-cost);
			cm.warp(742000000);
			cm.dispose();
		}
	}
}
