function enter(pi) {
	var returnMap = pi.getSavedLocation("AMORIA");
	if (returnMap < 0) {
		returnMap = 100000000;
	}
    pi.playPortalSE();
	pi.clearSavedLocation("AMORIA");
    pi.warp(returnMap, 0);
    return true;
}