/**
  NPC : 黑影
  任務:肥肥農場的黑影 20716
 */
function start() {
    var status = cm.getQuestStatus(20706);

    if (status == 0) {
        cm.sendNext("它看起來像有什麼可疑的地方.");
    } else if (status == 1) {
        cm.forceCompleteQuest(20706);
        cm.sendNext("你已經發現的影子！最好是報告給 #p1103001#.");
    } else if (status == 2) {
        cm.sendNext("陰影已經被發現。最好是報告給 #p1103001#.");
    }
    cm.dispose();
}

function action(mode, type, selection) {
}
