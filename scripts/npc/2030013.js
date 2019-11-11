var status = -1;

function start() {
    if (cm.getPlayer().getClient().getChannel() != 4 && cm.getPlayer().getClient().getChannel() != 5) {
        cm.playerMessage(5, "只能在4頻或5頻打");
        cm.dispose();
        return false;
    }
    var marr = cm.getQuestRecord(160108);
    var data = marr.getCustomData();
    if (data == null) {
        marr.setCustomData("0");
        data = "0";
    }
    var dat = parseInt(marr.getCustomData());
    var level = cm.getPlayerStat("LVL");
    if (!cm.haveItem(4001017) && level < 50 && dat + 86400000 > cm.getCurrentTime()) {
        cm.playerMessage(5, "你沒有火焰之眼或者是你的等級尚未達到50 或者你24小時之內進入過了");
        cm.dispose();
    } else {
        if (cm.getPlayerCount(280030000) <= 0) { // Fant. Map
            var FantMap = cm.getMap(280030000);

            FantMap.resetFully();

            marr.setCustomData("" + cm.getCurrentTime());
            cm.warp(280030000, "sp");
            cm.dispose();
        } else {
            if (cm.getMap(280030000).getSpeedRunStart() == 0 && (cm.getMonsterCount(280030000) <= 0 || cm.getMap(280030000).isDisconnected(cm.getPlayer().getId()))) {
                cm.warp(280030000, "sp");
                cm.dispose();
            } else {
                cm.warp(280030000, 0);
            }
        }
    }
}

function action(mode, type, selection) {
    switch (status) {
        case 1:
            if (mode == 1) {
                cm.warp(211042300, 0);
            }
            cm.dispose();
            break;
    }
}