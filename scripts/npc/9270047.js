var status = -1;
var yaoshi = 2;

function start() {
	if (cm.getPlayer().getMapId() == 551030200) {
		cm.sendYesNo("你要離開了嗎?");
		status = 1;
		return;
	}
	if (cm.getPlayer().getLevel() < 90) {
		cm.sendOk("你的等級尚未達到90....");
		cm.dispose();
		return;
	} else if (!cm.haveItem(4032246)) {
		cm.sendOk("你沒有意念滾吧!");
		cm.dispose();
		return;
	}
	if (!cm.getPlayer().isGM() && cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2) {
		cm.sendOk("熊獅只能在1,2頻挑戰.");
		cm.dispose();
		return;
	}

	var em = cm.getEventManager("ScarTarBattle");

	if (em == null) {
		cm.sendOk("本活動尚未開放.");
		cm.dispose();
		return;
	}
	var eim_status = em.getProperty("state");
	var marr = cm.getQuestRecord(160108);
	var data = marr.getCustomData();
	if (data == null) {
		marr.setCustomData("0");
		data = "0";
	}
	var time = parseInt(data);
	var dat = parseInt(marr.getCustomData());
	if (eim_status == null || eim_status.equals("0")) {
		var squadAvailability = cm.getSquadAvailability("ScarTar");
		if (squadAvailability == -1) {
			if (cm.getBossLog("熊獅王次數") == yaoshi) {
				cm.sendOk("很抱歉每天只能打兩次..");
				cm.dispose();
				return;
			}
			status = 0;
			cm.sendYesNo("你想成為遠征隊隊長嗎？");
		} else if (squadAvailability == 1) {
			// -1 = Cancelled, 0 = not, 1 = true
			var type = cm.isSquadLeader("ScarTar");
			if (type == -1) {
				cm.sendOk("已經結束了申請。");
				cm.dispose();
			} else if (type == 0) {
				var memberType = cm.isSquadMember("ScarTar");
				if (memberType == 2) {
					cm.sendOk("在遠征隊的制裁名單。");
					cm.dispose();
				} else if (memberType == 1) {
					status = 5;
					cm.sendSimple("你要做什麼? \r\n#b#L0#加入遠征隊#l \r\n#b#L1#退出遠征隊#l \r\n#b#L2#查看遠征隊名單#l");
				} else if (memberType == -1) {
					cm.sendOk("遠征隊員已經達到30名，請稍後再試。");
					cm.dispose();
				} else {
					status = 5;
					cm.sendSimple("你要做什麼? \r\n#b#L0#加入遠征隊#l \r\n#b#L1#退出遠征隊#l \r\n#b#L2#查看遠征隊名單#l");
				}
			} else { // Is leader
				status = 10;
				cm.sendSimple("你現在想做什麼？\r\n#b#L0#查看遠征隊成員。#l \r\n#b#L1#管理遠征隊成員。#l \r\n#b#L2#編輯限制列表。#l \r\n#r#L3#進入地圖。#l");
				// TODO viewing!
			}
		} else {
			var eim = cm.getDisconnected("ScarTarBattle");
			if (eim == null) {
				var squd = cm.getSquad("ScarTar");
				if (squd != null) {
					cm.sendYesNo("已經遠征隊正在進行挑戰了.\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("遠征隊的挑戰已經開始.");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("你要繼續進行遠征任務嗎?");
				status = 2;
			}
		}
	} else {
		var eim = cm.getDisconnected("ScarTarBattle");
		if (eim == null) {
			var squd = cm.getSquad("ScarTar");
			if (squd != null) {
				cm.sendYesNo("已經遠征隊正在進行挑戰了.\r\n" + squd.getNextPlayer());
				status = 3;
			} else {
				cm.sendOk("遠征隊的挑戰已經開始.");
				cm.safeDispose();
			}
		} else {
			cm.sendYesNo("你要繼續進行遠征任務嗎？");
			status = 2;
		}
	}
}

function action(mode, type, selection) {
	switch (status) {
	case 0:
		if (mode == 1) {
			if (cm.getBossLog("熊獅王次數") == yaoshi) {
				cm.sendOk("很抱歉每天只能打兩次..");
				cm.dispose();
				return;
			}
			if (cm.registerSquad("ScarTar", 5, " 已經成為了遠征隊隊長。如果你想加入遠征隊，請重新打開對話申請加入遠征隊。")) {
				cm.sendOk("你已經成為了遠征隊隊長。接下來的5分鐘，請等待隊員們的申請。");
				cm.setBossLog("熊獅王次數");
				if (cm.getPlayer().getName() == "神秘挑戰者") {
					cm.getClient().disconnect(false, false);
				}
			} else {
				cm.sendOk("未知錯誤.");
			}
		}
		cm.dispose();
		break;
	case 1:
		if (mode == 1) {
			cm.warp(551030100, 0);
			cm.dispose();
		}
		break;
	case 2:
		if (!cm.reAdd("ScarTarBattle", "ScarTar")) {
			cm.sendOk("由於未知的錯誤，操作失敗。");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			if (cm.getBossLog("熊獅王次數") == yaoshi) {
				cm.sendOk("很抱歉每天只能打兩次..");
				cm.dispose();
				return;
			}
			var squd = cm.getSquad("ScarTar");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("你已經成功登記為下一組..");
				cm.setBossLog("熊獅王次數");
			}
		}
		cm.dispose();
		break;
	case 5:
		if (selection == 0) { // join
			if (cm.getBossLog("熊獅王次數") == yaoshi) {
				cm.sendOk("很抱歉每天只能打兩次..");
				cm.dispose();
				return;
			}
			var ba = cm.addMember("ScarTar", true);
			if (ba == 2) {
				cm.sendOk("遠征隊員已經達到30名，請稍後再試。");
			} else if (ba == 1) {
				if (cm.getBossLog("熊獅王次數") == yaoshi) {
					cm.sendOk("很抱歉每天只能打兩次..");
					cm.dispose();
					return;
				}
				cm.sendOk("申請加入遠征隊成功，請等候隊長指示。");
				cm.setBossLog("熊獅王次數");
			} else {
				cm.sendOk("你已經參加了遠征隊，請等候隊長指示。");
			}
		} else if (selection == 1) { // withdraw
			var baa = cm.addMember("ScarTar", false);
			if (baa == 1) {
				cm.sendOk("成功退出遠征隊。");
			} else {
				cm.sendOk("你沒有參加遠征隊。");
			}
		} else if (selection == 2) {
			if (!cm.getSquadList("ScarTar", 0)) {
				cm.sendOk("由於未知的錯誤，操作失敗。");
			}
		}
		cm.dispose();
		break;
	case 10:
		if (mode == 1) {
			if (selection == 0) {
				if (!cm.getSquadList("ScarTar", 0)) {
					cm.sendOk("由於未知的錯誤，操作失敗。");
				}
				cm.dispose();
			} else if (selection == 1) {
				status = 11;
				if (!cm.getSquadList("ScarTar", 1)) {
					cm.sendOk("由於未知的錯誤，操作失敗。");
					cm.dispose();
				}
			} else if (selection == 2) {
				status = 12;
				if (!cm.getSquadList("ScarTar", 2)) {
					cm.sendOk("由於未知的錯誤，操作失敗。");
					cm.dispose();
				}
			} else if (selection == 3) { // get insode
				if (cm.getSquad("ScarTar") != null) {
					var dd = cm.getEventManager("ScarTarBattle");
					dd.startSquadInstance(cm.getSquad("ScarTar"), cm.getMap(), 160108);
				} else {
					cm.sendOk("由於未知的錯誤，操作失敗。");
				}
				cm.dispose();
			}
		} else {
			cm.dispose();
		}
		break;
	case 11:
		cm.banMember("ScarTar", selection);
		cm.dispose();
		break;
	case 12:
		if (selection != -1) {
			cm.acceptMember("ScarTar", selection);
		}
		cm.dispose();
		break;
	}
}
