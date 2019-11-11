var status = 0;

function start() {
	cm.sendYesNo("嗨，我是#p9330093# 您參加辛巴谷周年慶累了，是否想出去了???");
}

function action(mode, type, selection) {
	if (mode != 1) {
		if (mode == 0)
			cm.sendOk("需要的時候，再來找我吧。");
		cm.dispose();
		return;
	}
	status++;
	if (status == 1) {
		var returnMap = cm.getSavedLocation("BIRTHDAY");
		if (returnMap < 0) {
			returnMap = 100000000;
		}
		cm.clearSavedLocation("BIRTHDAY");
		cm.warp(returnMap, 0);
		cm.dispose();
	}
}
