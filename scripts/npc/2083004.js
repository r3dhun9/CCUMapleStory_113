var dt = new Date();
var year = dt.getFullYear();
var month = (dt.getMonth() + 1);
var new_ = ((year == 2016 && month >= 7) || year >= 2017);
var status = -1;
var horntail = 2;

function start() {
	if (cm.getPlayer().getLevel() < 80) {
		cm.sendNext("必須80等以上才可以挑戰#b闇黑龍王#k");
		cm.dispose();
		return;
	}

	if (cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 15) {
		cm.sendNext("闇黑龍王只有在頻道 13 、 14 或15才可以挑戰");
		cm.dispose();
		return;
	}
	
	if (cm.getPlayer().getName() == "神秘挑戰者") {
		cm.setBossLog("new龍王次數1");
		cm.setBossLog("new龍王次數2");
	}
	
	var em = cm.getEventManager("HorntailBattle");

	if (em == null) {
		cm.sendNext("找不到腳本，請聯繫GM！！");
		cm.dispose();
		return;
	}
	var prop = em.getProperty("state");
	if (prop == null || prop.equals("0")) {
		var squadAvailability = cm.getSquadAvailability("Horntail");
		var check1 = cm.getMapFactory().getMap(240060100);
		var check2 = cm.getMapFactory().getMap(240060200);
		if (check1.playerCount() != 0 || check2.playerCount() != 0) {
			cm.sendNext("其它遠征隊，正在對戰中。");
			cm.safeDispose();
		}
		if (squadAvailability == -1) {
			if (cm.getBossLog("new龍王次數1") >= 1 && cm.getBossLog("new龍王次數2") >= 1) {
				cm.sendNext("很抱歉每天只能打兩次..");
				cm.dispose();
				return;
			}
			status = 0;
			cm.sendYesNo("你有興趣成為遠征隊隊長？？");
		} else if (squadAvailability == 1) {
			// -1 = Cancelled, 0 = not, 1 = true
			var type = cm.isSquadLeader("Horntail");
			if (type == -1) {
				cm.sendNext("你被踢除所以不得再申請遠征隊。");
				cm.dispose();
			} else if (type == 0) {
				var memberType = cm.isSquadMember("Horntail");
				if (memberType == 2) {
					cm.sendNext("你已經被黑名單了。");
					cm.dispose();
				} else if (memberType == 1) {
					status = 5;
					cm.sendSimple("你要做什麼? \r\n#b#L0#查看遠征隊名單#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
				} else if (memberType == -1) {
					cm.sendNext("由於遠征隊時間流逝，所以必須重新再申請一次遠征隊。");
					cm.dispose();
				} else {
					status = 5;
					cm.sendSimple("你要做什麼? \r\n#b#L0#查看遠征隊名單#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
				}
			} else { // Is leader
				status = 10;
				cm.sendSimple("你現在想做什麼？\r\n#b#L0#查看遠征隊成員。#l \r\n#b#L1#管理遠征隊成員。#l \r\n#b#L2#編輯限制列表。#l \r\n#r#L3#進入地圖。#l");
				// TODO viewing!
			}
		} else {
			var props = em.getProperty("leader");
			if (props != null && props.equals("true")) {
				var eim = cm.getDisconnected("HorntailBattle");
				if (eim == null) {
					cm.sendNext("其它遠征隊，正在對戰中。");
					cm.safeDispose();
				} else {
					cm.sendNext("其它遠征隊，正在對戰中。");
					cm.safeDispose();
				}
			} else {
				cm.sendNext("很抱歉你的遠征隊隊長離開了現場，所以你不能再返回戰場。");
				cm.safeDispose();
			}
		}
	} else {
		var props = em.getProperty("leader");
		if (props != null && props.equals("true")) {
			var eim = cm.getDisconnected("HorntailBattle");
			if (eim == null) {
				cm.sendNext("其它遠征隊，正在對戰中。");
				cm.safeDispose();
			} else {
				cm.sendNext("其它遠征隊，正在對戰中。");
				cm.safeDispose();
			}
		} else {
			cm.sendNext("很抱歉你的遠征隊隊長離開了現場，所以你不能再返回戰場。");
			cm.safeDispose();
		}
	}
}

function action(mode, type, selection) {
	switch (status) {
	case 0:
		if (mode == 1) {
			if (cm.getBossLog("new龍王次數1") >= 1 && cm.getBossLog("new龍王次數2") >= 1) {
				cm.sendNext("很抱歉每天只能打兩次..");
				cm.dispose();
				return;
			}

			if (cm.registerSquad("Horntail", 5, "已成為闇黑龍王遠征隊長，想要參加遠征隊的玩家請開始進行申請。")) {
				cm.sendNext("你成功申請了遠征隊隊長，你必須在接下來的五分鐘召集玩家申請遠征隊，然後開始戰鬥。");
				if (new_) {
					if (cm.getPlayer().getName() == "神秘挑戰者") {
						cm.setBossLog("new龍王次數1");
						cm.setBossLog("new龍王次數2");
					}
					if (cm.getBossLog("new龍王次數1") == 0 && cm.getBossLog("new龍王次數2") == 0) {
						cm.setBossLog("new龍王次數1");
					} else if (cm.getBossLog("new龍王次數1") >= 1 && cm.getBossLog("new龍王次數2") == 0) {
						cm.setBossLog("new龍王次數2");
					} else {
						cm.sendNext("龍王挑戰次數(1) : " + cm.getBossLog("new龍王次數1") + " 龍王挑戰次數(2) : " + cm.getBossLog("new龍王次數2"));
						cm.dispose();
						return;
					}
				} else {
					cm.setBossLog("龍王次數");
				}
			} else {
				cm.sendNext("申請遠征隊失敗，發生了未知錯誤。");
			}
		}
		cm.dispose();
		break;
	case 1:
		if (!cm.reAdd("HorntailBattle", "Horntail")) {
			cm.sendNext("錯誤.... 請重新在試一次。");
		}
		cm.safeDispose();
		break;
	case 5:
		if (selection == 0) {
			if (cm.getPlayer().getName() == "神秘挑戰者") {
				cm.setBossLog("new龍王次數1");
				cm.setBossLog("new龍王次數2");
			}
			if (!cm.getSquadList("Horntail", 0)) {
				cm.sendNext("錯誤.... 請重新在試一次。");
			}
		} else if (selection == 1) { // join
			if (new_) {
				if (cm.getBossLog("new龍王次數1") >= 1 && cm.getBossLog("new龍王次數2") >= 1) {
					cm.sendNext("很抱歉每天只能打兩次..");
					cm.dispose();
					return;
				}
			} else {
				if (cm.getBossLog("龍王次數") == horntail) {
					cm.sendNext("很抱歉每天只能打兩次..");
					cm.dispose();
					return;
				}
			}
			var ba = cm.addMember("Horntail", true);
			if (ba == 2) {
				if (new_) {
					if (cm.getBossLog("new龍王次數1") >= 1 && cm.getBossLog("new龍王次數2") >= 1) {
						cm.sendNext("很抱歉每天只能打兩次..");
						cm.dispose();
						return;
					}
				} else {
					if (cm.getBossLog("龍王次數") == horntail) {
						cm.sendNext("很抱歉每天只能打兩次..");
						cm.dispose();
						return;
					}
				}
				cm.sendNext("遠征隊人數已滿，請稍後再嘗試。");
			} else if (ba == 1) {
				if (new_) {
					if (cm.getBossLog("new龍王次數1") >= 1 && cm.getBossLog("new龍王次數2") >= 1) {
						cm.sendNext("很抱歉每天只能打兩次..");
						cm.dispose();
						return;
					}
				} else {
					if (cm.getBossLog("龍王次數") == horntail) {
						cm.sendNext("很抱歉每天只能打兩次..");
						cm.dispose();
						return;
					}
				}
				if (new_) {
					if (cm.getPlayer().getName() == "神秘挑戰者") {
						cm.setBossLog("new龍王次數1");
						cm.setBossLog("new龍王次數2");
					}
					if (cm.getBossLog("new龍王次數1") == 0 && cm.getBossLog("new龍王次數2") == 0) {
						cm.setBossLog("new龍王次數1");
					} else if (cm.getBossLog("new龍王次數1") >= 1 && cm.getBossLog("new龍王次數2") == 0) {
						cm.setBossLog("new龍王次數2");
					} else {
						cm.sendNext("龍王挑戰次數(1) : " + cm.getBossLog("new龍王次數1") + " 龍王挑戰次數(2) : " + cm.getBossLog("new龍王次數2"));
						cm.dispose();
						return;
					}
				} else {
					cm.setBossLog("龍王次數");
				}
				cm.sendNext("申請遠征隊成功。");
			} else {
				cm.sendNext("你已經在遠征隊裡面了。");
			}
		} else { // withdraw
			var baa = cm.addMember("Horntail", false);
			if (baa == 1) {
				cm.sendNext("離開遠征隊成功。");
			} else {
				cm.sendNext("你不再遠征隊裡面。");
			}
		}
		cm.dispose();
		break;
	case 10:
		if (mode == 1) {
			if (selection == 0) {
				if (!cm.getSquadList("Horntail", 0)) {
					cm.sendNext("由於未知的錯誤，遠征隊的請求被拒絕了。");
				}
				cm.dispose();
			} else if (selection == 1) {
				status = 11;
				if (!cm.getSquadList("Horntail", 1)) {
					cm.sendNext("由於未知的錯誤，遠征隊的請求被拒絕了。");
					cm.dispose();
				}
			} else if (selection == 2) {
				status = 12;
				if (!cm.getSquadList("Horntail", 2)) {
					cm.sendNext("由於未知的錯誤，遠征隊的請求被拒絕了。");
					cm.dispose();
				}
			} else if (selection == 3) { // get insode
				if (cm.getSquad("Horntail") != null) {
					var dd = cm.getEventManager("HorntailBattle");
					dd.startSquadInstance(cm.getSquad("Horntail"), cm.getMap(), 160100);
				} else {
					cm.sendNext("由於未知的錯誤，遠征隊的請求被拒絕了。");
				}
				cm.dispose();
			}
		} else {
			cm.dispose();
		}
		break;
	case 11:
		cm.banMember("Horntail", selection);
		cm.dispose();
		break;
	case 12:
		if (selection != -1) {
			cm.acceptMember("Horntail", selection);
		}
		cm.dispose();
		break;
	default:
		cm.dispose();
		break;
	}
}
