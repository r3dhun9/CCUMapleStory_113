/*
耶雷弗100等坐騎的任務
*/
var status = -1;

function start(mode, type, selection) {
    qm.sendNext("不要再弄丟了喔。");
	qm.gainItem(1902006, 1);
	qm.forceCompleteQuest();
	qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}