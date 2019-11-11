var exit = 749020920;

function init() {
	// 0 = 	無人挑戰中, 1 = 正在挑戰中
	em.setProperty("state", "0");
	em.setProperty("leader", "true");
}

function setup(eim, leaderid) {
	em.setProperty("state", "1");
	em.setProperty("preheadCheck", "0");
	em.setProperty("leader", "true");

	var eim = em.newInstance("Cake");
	eim.startEventTimer(3 * 60 * 1000);
	return eim;
}

function playerEntry(eim, player) {
	var maps = Array(
			749020000, //三週年蛋糕地圖1-9
			749020100,
			749020200,
			749020300,
			749020400,
			749020500,
			749020600,
			749020700,
			749020800);
	var mapA = maps[Math.floor(Math.random() * maps.length)];
	var map = eim.getMapFactory().getMap(mapA);
	player.changeMap(map, map.getPortal(0));
	eim.broadcastPlayerMsg(6, "蛋糕上的蠟燭全數點燃後，所有怪物才會消失喔！");
}

function changedMap(eim, player, mapid) {
	if (mapid >= 749020000 && mapid <= 749020900) {
		return;
	}
	eim.unregisterPlayer(player);
	if (eim.disposeIfPlayerBelow(0, 0)) {
		em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
}

function playerDisconnected(eim, player) {
	return 0;
}

function scheduledTimeout(eim) {
	eim.disposeIfPlayerBelow(100, 749020920);
	em.setProperty("state", "0");
	em.setProperty("leader", "true");
}

function playerExit(eim, player) {
	eim.unregisterPlayer(player);
	if (eim.disposeIfPlayerBelow(0, 0)) {
		em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
}

function monsterValue(eim, mobId) {
	return 1;
}

function allMonstersDead(eim) {}

function playerRevive(eim, player) {
	return true;
}

function clearPQ(eim) {}
function leftParty(eim, player) {}
function disbandParty(eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}
