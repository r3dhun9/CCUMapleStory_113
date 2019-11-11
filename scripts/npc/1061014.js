
/* Mu Young
	Boss Balrog
*/


var status = -1;

function action(mode, type, selection) {
    switch (status) {
        case -1:
            status = 0;
            switch (cm.getChannelNumber()) {
                default: cm.sendNext("目前模式為 #i3994116# 如果你想加入這個模式請按下一步  條件是 等級 50 ~ 等級 120 / 遠征隊人數 1 ~ 30 個");
                break;
            }
            break;
        case 0:
            var em = cm.getEventManager("BossBalrog");

            if (em == null) {
                cm.sendOk("目前副本出了一點問題，請聯繫GM！");
                cm.safeDispose();
                return;
            }

            var prop = em.getProperty("state");
            if (prop == null || prop.equals("0")) {
                var squadAvailability = cm.getSquadAvailability("BossBalrog");
                if (squadAvailability == -1) {
                    status = 1;
                    cm.sendYesNo("現在可以申請遠征隊，你想成為遠征隊隊長嗎？");

                } else if (squadAvailability == 1) {
                    // -1 = Cancelled, 0 = not, 1 = true
                    var type = cm.isSquadLeader("BossBalrog");
                    if (type == -1) {
                        cm.sendOk("已經結束了申請。");
                        cm.safeDispose();
                    } else if (type == 0) {
                        var memberType = cm.isSquadMember("BossBalrog");
                        if (memberType == 2) {
                            cm.sendOk("在遠征隊的制裁名單。");
                            cm.safeDispose();
                        } else if (memberType == 1) {
                            status = 5;
                            cm.sendSimple("你要做什麼? \r\n#b#L0#加入遠征隊#l \r\n#b#L1#退出遠征隊#l \r\n#b#L2#查看遠征隊名單#l");
                        } else if (memberType == -1) {
                            cm.sendOk("遠征隊員已經達到30名，請稍後再試。");
                            cm.safeDispose();
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
                    var eim = cm.getDisconnected("BossBalrog");
                    if (eim == null) {
                        cm.sendOk("遠征隊的挑戰已經開始.");
                        cm.safeDispose();
                    } else {
                        cm.sendYesNo("你要繼續進行遠征任務嗎？");
                        status = 2;
                    }
                }
            } else {
                var eim = cm.getDisconnected("BossBalrog");
                if (eim == null) {
                    cm.sendOk("遠征隊的挑戰已經開始.");
                    cm.safeDispose();
                } else {
                    cm.sendYesNo("你要繼續進行遠征任務嗎？");
                    status = 2;
                }
            }
            break;
        case 1:
            if (mode == 1) {
                var lvl = cm.getPlayerStat("LVL");
                if (lvl >= 50 && lvl <= 120) {
                    if (cm.registerSquad("BossBalrog", 5, " 已經成為了遠征隊隊長。如果你想加入遠征隊，請重新打開對話申請加入遠征隊。")) {
                        cm.sendOk("你已經成為了遠征隊隊長。接下來的5分鐘，請等待隊員們的申請。");
                    } else {
                        cm.sendOk("未知錯誤.");
                    }
                } else {
                    cm.sendNext("有一個遠征隊成員的等級不是50到120之間。");
                }
            } else {
                cm.sendOk("如果你想再次申請遠征隊的話請告訴我。")
            }
            cm.safeDispose();
            break;
        case 2:
            if (!cm.reAdd("BossBalrog", "BossBalrog")) {
                cm.sendOk("由於未知的錯誤，操作失敗。");
            }
            cm.safeDispose();
            break;
        case 5:
            if (selection == 0) {
                if (!cm.getSquadList("BossBalrog", 0)) {
                    cm.sendOk("由於未知的錯誤，操作失敗。");
                    cm.safeDispose();
                } else {
                    cm.dispose();
                }
            } else if (selection == 1) { // join
                var ba = cm.addMember("BossBalrog", true);
                if (ba == 2) {
                    cm.sendOk("遠征隊員已經達到30名，請稍後再試。");
                    cm.safeDispose();
                } else if (ba == 1) {
                    cm.sendOk("申請加入遠征隊成功，請等候隊長指示。");
                    cm.safeDispose();
                } else {
                    cm.sendOk("你已經參加了遠征隊，請等候隊長指示。");
                    cm.safeDispose();
                }
            } else { // withdraw
                var baa = cm.addMember("BossBalrog", false);
                if (baa == 1) {
                    cm.sendOk("成功退出遠征隊。");
                    cm.safeDispose();
                } else {
                    cm.sendOk("你沒有參加遠征隊。");
                    cm.safeDispose();
                }
            }
            break;
        case 10:
            if (selection == 0) {
                if (!cm.getSquadList("BossBalrog", 0)) {
                    cm.sendOk("由於未知的錯誤，操作失敗。");
                }
                cm.safeDispose();
            } else if (selection == 1) {
                status = 11;
                if (!cm.getSquadList("BossBalrog", 1)) {
                    cm.sendOk("由於未知的錯誤，操作失敗。");
                }
                cm.safeDispose();
            } else if (selection == 2) {
                status = 12;
                if (!cm.getSquadList("BossBalrog", 2)) {
                    cm.sendOk("由於未知的錯誤，操作失敗。");
                }
                cm.safeDispose();
            } else if (selection == 3) { // get insode
                if (cm.getSquad("BossBalrog") != null) {
                    var dd = cm.getEventManager("BossBalrog");
                    dd.startSquadInstance(cm.getSquad("BossBalrog"), cm.getMap());
                    cm.dispose();
                } else {
                    cm.sendOk("由於未知的錯誤，操作失敗。");
                    cm.safeDispose();
                }
            }
            break;
        case 11:
            cm.banMember("BossBalrog", selection);
            cm.dispose();
            break;
        case 12:
            if (selection != -1) {
                cm.acceptMember("BossBalrog", selection);
            }
            cm.dispose();
            break;
    }
}