function enter(pi) {
	if (pi.getPlayer().isGM) {
		pi.saveLocation("PACHINKO");
		pi.playPortalSE();
		pi.warp(809030000, "out00");
	} else {
		pi.playerMessage(5, "很抱歉，小鋼珠暫時不開放!");
	}
	return true;
}
