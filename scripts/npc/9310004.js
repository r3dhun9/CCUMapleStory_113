function start() {
    if (cm.getQuestStatus(8512) == 1) {
		if (cm.haveItem(4031289)) {
			cm.warp(701010321);
			cm.removeAll(4031289);
		} else {
			cm.sendNext("貌似沒有#t4031289#0.0");
		}
    } else {
        cm.sendOk("你沒有完成性口的造反!");
    }
	cm.dispose();
}