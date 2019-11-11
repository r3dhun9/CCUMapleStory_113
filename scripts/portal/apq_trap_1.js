importPackage(Packages.tools.MaplePacketCreator);

function enter(pi) {
    var map = pi.getPlayer().getMap();
    var reactor = map.getReactorByName("gate01");
    var state = reactor.getState();
    if (state >= 4) {
        pi.warp(670010600, 4);
        return true;
    } else {
        pi.getClient().getSession().write(MaplePacketCreator.getErrorNotice("The gate is closed."));
        return false;
    }
}
