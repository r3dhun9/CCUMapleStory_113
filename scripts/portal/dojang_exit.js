function enter(pi) {
    pi.playPortalSE();
    var map = pi.getSavedLocation("MULUNG_TC");
    if( map == -1 )
        map = 100000000;
    pi.warp(map, 0);
    pi.clearSavedLocation("MULUNG_TC");
}