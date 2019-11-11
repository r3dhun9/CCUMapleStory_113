var status = -1;

function start(mode, type, selection) {
    qm.dispose();
}

function end(mode, type, selection) {
    if (qm.getPlayer().getMarriageId() > 0 && qm.getPlayer().getGuildId() > 0 && qm.getPlayer().getJunior1() > 0 && qm.canHold(1142081, 1)) {
        qm.sendNext("恭喜你完成社會人士任務。");
        qm.forceCompleteQuest();
        qm.gainItem(1142081, 1);
    } else {
        qm.sendNext("請完成我要的條件再來找我。");
    }
    qm.dispose();
}