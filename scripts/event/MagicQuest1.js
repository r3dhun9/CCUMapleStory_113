 /*
  * 很可疑的漢斯  任務代碼 : 20718
  * by:Kodan改
  */

function init() {}

function monsterValue(eim, mobId) {
    return 1;
}

function setup() {
    var eim = em.newInstance("MagicQuest1");
    var map = eim.setInstanceMap(910110000);
    map.respawn(true);
    map.resetReactors();
    map.shuffleReactors();
    eim.startEventTimer(600000);

    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(910110000);
    player.changeMap(map, map.getPortal(0));
}

function playerDead(eim, player) {}

function playerRevive(eim, player) {}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 101000003);
}

function changedMap(eim, player, mapid) {
    if (mapid != 910110000) {
        eim.unregisterPlayer(player);

        eim.disposeIfPlayerBelow(0, 0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function leftParty(eim, player) {
    // If only 2 players are left, uncompletable:
    playerExit(eim, player);
}

function disbandParty(eim) {
    eim.disposeIfPlayerBelow(100, 101000003);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    var map = eim.getMapFactory().getMap(101000003);
    player.changeMap(map, map.getPortal(0));
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 101000003);
}

function allMonstersDead(eim) {}

function cancelSchedule() {}