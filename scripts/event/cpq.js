load('nashorn:mozilla_compat.js');
importPackage(Packages.tools);

var exitMap = 0;
var waitingMap = 1;
var reviveMap = 2;
var fieldMap = 3;
var winnerMap = 4;
var loserMap = 5;

function init() {}

function monsterValue(eim, mobId) {
    return 1;
}

function setup(mapid) {
    var map = parseInt(mapid);
    var eim = em.newInstance("cpq" + mapid);
    eim.setInstanceMap(980000000); // <exit>
    eim.setInstanceMap(map);
    eim.setInstanceMap(map + 2);
    eim.setInstanceMap(map + 1).resetFully();
    eim.setInstanceMap(map + 3);
    eim.setInstanceMap(map + 4);
    eim.setProperty("forfeit", "false");
    eim.setProperty("blue", "-1");
    eim.setProperty("red", "-1");
    eim.setProperty("step", "0");
    var portal = eim.getMapInstance(reviveMap).getPortal("pt00");
    portal.setScriptName("MCrevive1");
    eim.setProperty("started", "false");
    return eim;
}

function playerEntry(eim, player) {
    player.disposeClones();
    player.changeMap(eim.getMapInstance(waitingMap), eim.getMapInstance(waitingMap).getPortal(0));
    player.tryPartyQuest(1301);
}


function registerCarnivalParty(eim, carnivalParty) {
    if (eim.getProperty("red").equals("-1")) {
        eim.setProperty("step", "1");
        eim.setProperty("red", "" + carnivalParty.getLeader().getId());
        eim.startEventTimer(3 * 60 * 1000);
        eim.broadcastPlayerMsg(5, "接下來的三分鐘您的隊伍可以找尋其他人挑戰。");
    } else {
        eim.setProperty("step", "2");
        eim.setProperty("blue", "" + carnivalParty.getLeader().getId());
        eim.broadcastPlayerMsg(5, "正在檢測是否有偷渡者...");
        check();
    }
}

function playerDead(eim, player) {}

function leftParty(eim, player) {
    disbandParty(eim);
}

function disbandParty(eim) {
    dispose(eim);
}


function dispose(eim) {
    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
        var player = iter.next();
        eim.unregisterPlayer(player);
        player.changeMap(eim.getMapInstance(exitMap), eim.getMapInstance(exitMap).getPortal(0));
        player.getCarnivalParty().removeMember(player);
    }
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.getCarnivalParty().removeMember(player);
    player.changeMap(eim.getMapInstance(exitMap), eim.getMapInstance(exitMap).getPortal(0));
    eim.disposeIfPlayerBelow(0, 0);
}

//for offline players
function removePlayer(eim, player) {
    eim.unregisterPlayer(player);
    player.getCarnivalParty().removeMember(player);
    player.getMap().removePlayer(player);
    player.setMap(eim.getMapInstance(exitMap));
    eim.disposeIfPlayerBelow(0, 0);
}


function getParty(eim, property) {
    var chr = em.getChannelServer().getPlayerStorage().getCharacterById(parseInt(eim.getProperty(property)));
    if (chr == null) {
        eim.broadcastPlayerMsg(5, "隊伍的隊長 " + property + " 找不到。");
        dispose(eim);
        return null;
    } else {
        return chr.getCarnivalParty();
    }
}

function start(eim) {
    eim.setProperty("started", "true");
    eim.startEventTimer(10 * 60 * 1000);
    var blueP = getParty(eim, "blue");
    if (blueP != null)
        blueP.warp(eim.getMapInstance(fieldMap), "blue00");
    var redP = getParty(eim, "red");
    if (redP != null)
        redP.warp(eim.getMapInstance(fieldMap), "red00");
}

function check(eim) {
    var ck = eim.check();
    if (ck) {
        eim.setProperty("step", "3");
        eim.broadcastPlayerMsg(5, "檢測..目前無異常....!");
        eim.startEventTimer(10 * 1000);
        eim.broadcastPlayerMsg(5, "10秒後將開戰！！");
    } else {
        eim.broadcastPlayerMsg(5, "檢測..發現異常!! 即將傳回去");
        dispose(eim);
    }
}

function monsterKilled(eim, chr, cp) {
    chr.getCarnivalParty().addCP(chr, cp);
    chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
        iter.next().CPUpdate(true, chr.getCarnivalParty().getAvailableCP(), chr.getCarnivalParty().getTotalCP(), chr.getCarnivalParty().getTeam());
    }
}

function monsterValue(eim, mobId) {
    return 0;
}


function end(eim) {
    if (!eim.getProperty("started").equals("true")) {
        dispose(eim);
    }
}

function warpOut(eim) {
    if (!eim.getProperty("started").equals("true")) {
        if (eim.getProperty("blue").equals("-1")) {
            dispose(eim);
        }
    } else {
        var blueParty = getParty(eim, "blue");
        var redParty = getParty(eim, "red");
        if (blueParty.isWinner()) {
            blueParty.warp(eim.getMapInstance(winnerMap), 0);
            redParty.warp(eim.getMapInstance(loserMap), 0);
        } else {
            redParty.warp(eim.getMapInstance(winnerMap), 0);
            blueParty.warp(eim.getMapInstance(loserMap), 0);
        }
        eim.disposeIfPlayerBelow(100, 0);
    }
}

function scheduledTimeout(eim) {
    eim.stopEventTimer();

    if (!eim.getProperty("started").equals("true")) {
        // not start
        if (eim.getProperty("step").equals("3")) {
            start()
        } else {
            if (eim.getProperty("blue").equals("-1")) {
                dispose(eim);
            }
        }
    } else {
        var blueParty = getParty(eim, "blue");
        var redParty = getParty(eim, "red");
        if (blueParty != null && redParty != null) {
            if (blueParty.getTotalCP() > redParty.getTotalCP()) {
                blueParty.setWinner(true);
            } else if (redParty.getTotalCP() > blueParty.getTotalCP()) {
                redParty.setWinner(true);
            }
            blueParty.displayMatchResult();
            redParty.displayMatchResult();
        }
        eim.schedule("warpOut", 10000);
    }
}

function playerRevive(eim, player) {
    player.getCarnivalParty().useCP(player, 10);
    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
        iter.next().CPUpdate(true, player.getCarnivalParty().getAvailableCP(), player.getCarnivalParty().getTotalCP(), player.getCarnivalParty().getTeam());
    }
    player.addHP(50);
    player.changeMap(eim.getMapInstance(reviveMap), eim.getMapInstance(reviveMap).getPortal(0));
    return true;
}

function playerDisconnected(eim, player) {
    player.setMap(eim.getMapInstance(exitMap));
    eim.unregisterPlayer(player);
    player.getCarnivalParty().removeMember(player);
    eim.broadcastPlayerMsg(5, player.getName() + " 離開了怪物擂台1");
    dispose(eim);
}

function onMapLoad(eim, chr) {
    if (!eim.getProperty("started").equals("true")) {
        if(eim.getProperty("step").equals("3"))
            dispose(eim);
    } else if (chr.getCarnivalParty().getTeam() == 0) {
        var blueParty = getParty(eim, "blue");
        chr.startMonsterCarnival(blueParty.getAvailableCP(), blueParty.getTotalCP());
    } else {
        var redParty = getParty(eim, "red");
        chr.startMonsterCarnival(redParty.getAvailableCP(), redParty.getTotalCP());
    }
}

function cancelSchedule() {}

function clearPQ(eim) {}

function allMonstersDead(eim) {}

function changedMap(eim, chr, mapid) {}
