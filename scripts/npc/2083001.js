/*
	Encrypted Slate of the Squad - Leafre Cave of life
*/

var status = -1;
var minLevel = 80; // 35
var maxLevel = 200; // 65
var minPartySize = 1;
var maxPartySize = 6;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }

    //    if (cm.getMapId() == 240050400) {
    if (status == 0) {
        if (cm.getParty() == null) { // No Party
            cm.sendOk("很抱歉，你好像沒有組隊。。。");
            cm.dispose();
        } else if (!cm.isLeader()) { // Not Party Leader
            cm.sendOk("如果你想嘗試任務請找你的 #b隊長#k 來和我說話。#b");
            cm.dispose();
        } else {
            // Check if all party members are within PQ levels
            var party = cm.getParty().getMembers();
            var mapId = cm.getMapId();
            var next = true;
            var levelValid = 0;
            var inMap = 0;
            var it = party.iterator();

            while (it.hasNext()) {
                var cPlayer = it.next();
                if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
                    levelValid += 1;
                } else {
                    next = false;
                }
                if (cPlayer.getMapid() == mapId) {
                    inMap += (cPlayer.getJobId() == 900 ? 6 : 1);
                }
            }
            if (party.size() > maxPartySize || inMap < minPartySize) {
                next = false;
            }
            if (next) {
                var em = cm.getEventManager("HontalePQ");
                if (em == null) {
                    cm.sendSimple("找不到腳本，請聯繫GM！！#b");
                } else {
                    var prop = em.getProperty("state");
                    if (prop.equals("0") || prop == null) {
                        em.startPartyInstance(cm.getParty(), cm.getMap());
                        cm.removeAll(4001022);
                        cm.removeAll(4001023);
                        cm.dispose();
                        return;
                    } else if (cm.getMapId() == 240050300) {
                        if (cm.isLeader() && cm.haveItem(4001093, 6)) {
                            cm.showEffect(true, "quest/party/clear");
                            cm.playSound(true, "Party1/Clear");
                            cm.gainItem(4001093, -6);
                            cm.warpParty(240050400);
                            cm.dispose();
                        } else {
                            cm.sendOk("請叫你的隊長帶著6個藍色鑰匙來找我");
                            cm.dispose();
                        }

                    } else {
                        cm.sendSimple("#b(一座石碑，上面寫著看不懂的文字……。)#b");
                        cm.dispose();
                    }
                }
            } else {
                cm.sendOk("很抱歉，你的組隊好像沒有符合需求:\r\n\r\n#r需求: " + minPartySize + " 需要六個人且等級都必須在 " + minLevel + " 到 " + maxLevel + ".#b");
                cm.dispose();
            }
        }
    }
}