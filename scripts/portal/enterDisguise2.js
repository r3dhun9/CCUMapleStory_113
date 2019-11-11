function enter(pi) {
    if (pi.getQuestStatus(20301) == 1 ||
        pi.getQuestStatus(20302) == 1 ||
        pi.getQuestStatus(20303) == 1 ||
        pi.getQuestStatus(20304) == 1 ||
        pi.getQuestStatus(20305) == 1) {
        if (pi.getPlayerCount(108010610) == 0) {
            if (pi.haveItem(4032179, 1)) {
                pi.removeNpc(108010610, 1104102);
                var map = pi.getMap(108010610);
                map.killAllMonsters(false);
                map.spawnNpc(1104102, new java.awt.Point(3307, 88));
                pi.warp(108010610, 0);
            } else {
                pi.playerMessage("你沒有耶雷佛搜查證是進步來這個強大的地方的。");
            }
        } else {
            pi.playerMessage("已經有人在裡面調查了，請稍後再嘗試。");
        }
    } else {
        pi.warp(130010020, "out00");
    }
}