function start(mode, type, selection) {
	qm.forceStartQuest();
}

function end(mode, type, selection) {
	if (qm.getPlayer().getFame() >= 1000) {
		qm.gainItem(1142003, 1);
		qm.forceCompleteQuest(29002);
		qm.sendNext("恭喜您完成人氣王任務。");
		qm.worldMessage("『稱號挑戰』：恭喜 " + qm.getChar().getName() + "  成功挑戰人氣王看來不是個邊緣人！");
		qm.dispose();
	} else {
		qm.sendNext("名聲不足1000。");
		qm.dispose();
	}
}