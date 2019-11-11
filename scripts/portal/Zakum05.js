
/*
    Zakum Entrance
*/

function enter(pi) {
    if (pi.getQuestStatus(100200) != 2) {
        pi.playerMessage(5, "您好像還沒準備好面對BOSS。");
        return false;

    } else if (!pi.haveItem(4001017)) {
        pi.playerMessage(5, "由於你沒有火眼之眼，所以不能挑戰殘暴炎魔。");
        return false;
    }
    if (pi.getPlayerCount(280030000) <= 0) { // 炎魔
        var zakMap = pi.getMap(280030000);
	if (pi.getMonsterCount(280030000) > 0 && pi.getPlayerCount(280030000) <= 0) {
		pi.getMap(211042300).resetReactors();
		zakMap.resetFully();
	}
        pi.playPortalSE();
        pi.warp(pi.getPlayer().getMapId() + 100, "west00");
        return true;
    } else {
        if (pi.getMap(280030000).getSpeedRunStart() == 0 && (pi.getMonsterCount(280030000) <= 0 || pi.getMap(280030000).isDisconnected(pi.getPlayer().getId()))) {
            pi.playPortalSE();
			zakMap.resetFully();
			pi.getMap(211042300).resetReactors();
            pi.warp(pi.getPlayer().getMapId() + 100, "west00");
            return true;
        } else {
            pi.playerMessage(5, "裡面的戰鬥已經開始，請稍後再嘗試。");
            return false;
        }
    }
}