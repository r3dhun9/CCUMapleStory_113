function enter(pi) {
    if (pi.getMonsterCount(910110000) <= 0 && pi.getPlayerVariable("summ") != null) {
        pi.deletePlayerVariable("summ");
        pi.warp(101000000, "jobin00");
//        pi.hideNpc(910110000, 1032109);
//        pi.hideNpc(910110000, 1032110);
        pi.openNpc(1103003);
        return true;
    } else if (pi.getMonsterCount(910110000) != 0 && pi.getPlayerVariable("summ") != null) {
		pi.playerMessage(6,"請清除地圖上的怪物。");
	} else if (pi.getPlayerVariable("summ") == null) {
		pi.playerMessage(6,"請開始任務。");
	}
    return false;
}
