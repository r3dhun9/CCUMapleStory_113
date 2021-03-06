load('nashorn:mozilla_compat.js');
importPackage(Packages.tools.MaplePacketCreator);

function enter(pi) {
    var map = pi.getPlayer().getMap();
    var reactor = map.getReactorByName("gate02");
    var state = reactor.getState();
    if (state >= 4) {
        pi.warp(670010600, 6);
        return true;
    } else {
        pi.getClient().getSession().write(MaplePacketCreator.getErrorNotice("The gate is closed."));
        return false;
    }
}
