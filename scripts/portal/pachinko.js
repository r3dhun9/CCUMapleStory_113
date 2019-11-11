function enter(pi) {
	var returnMap = pi.getSavedLocation("PACHINKO");
	pi.clearSavedLocation("PACHINKO");
	if (returnMap < 0) {
		returnMap = 100000000;
	}
	var target = pi.getMap(returnMap);
	var portal;
	if (portal == null) {
		portal = target.getPortal(0);
	}
	if (pi.getMapId() != target) {
		pi.playPortalSE();
		pi.getPlayer().changeMap(target, portal);
	}
}
