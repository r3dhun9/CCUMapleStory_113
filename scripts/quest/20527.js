/*
耶雷弗100等坐騎的任務
*/
var status = -1;

function start(mode, type, selection) {
    qm.sendNext("請去找奇里督。");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
	qm.forceCompleteQuest();
    qm.dispose();
}