function act() {
	rm.mapMessage(5, "小心！魔王六手邪神因為太陽火花的召喚而出現了！");
    rm.changeMusic("Bgm09/TimeAttack");
    rm.spawnMonster(9420014, -202, 479);
    rm.getMap(501030104).setReactorState();
	rm.closePortal(501030104,"sp");
}