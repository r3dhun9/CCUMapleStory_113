var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function getFieldResult() {
    var selStr = "";
    var found = false;
    for (var i = 0; i < 6; i++) {
        if (getCPQField(i + 1) != "") {
            selStr += "\r\n#b#L" + (i + 1) * 100 + "# " + getCPQField(i + 1) + "#l#k";
            found = true;
        }
    }
    return {
        found: found,
        selStr: selStr
    }
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        cm.dispose();

    if (status == 0 && mode == 1) {
        cm.sendSimple("歡迎來到怪物擂台，請問你找我要幹嘛呢?\r\n" +
            "#L1#楓葉黃金標誌兌換#l\r\n" +
            "#L2#進行怪物擂台賽#l");
    } else if (status == 1) {
        if (selection == 1) {
            cm.sendSimple("#b#L0#50個楓葉黃金標誌#b#i4001129##k = #b休菲凱曼的混亂項鍊#p1122007:##l#k\r\n" +
                "#b#L1#30個楓葉黃金標誌#b#i4001129##k = #b休菲凱曼的珠子#i2041211:##l#k\r\n" +
                "#b#L2#50個閃亮的楓葉黃金標誌#b#i4001254##k = #b休菲凱曼的混亂項鍊#i1122058:##l#k");
        } else if (selection == 2) {
            if (cm.getParty() == null) {
                cm.sendSimple("請找好組隊再來找我。");
            } else {
                if (cm.isLeader()) {
                    var pt = cm.getPlayer().getParty();
                    if (pt.getMembers().size() < 2) {
                        cm.sendOk("需要 2 人以上才可以擂台！！");
                        cm.dispose();
                    }
                    if (checkLevelsAndMap(30, 50) == 1) {
                        cm.sendOk("隊伍裡有人等級不符合。");
                        cm.dispose();
                    } else if (checkLevelsAndMap(30, 50) == 2) {
                        cm.sendOk("在地圖上找不到您的隊友。");
                        cm.dispose();
                    }
                    var fields = getFieldResult();
                    if (fields.found) {
                        cm.sendSimple(fields.selStr);
                    } else {
                        cm.sendSimple("目前沒有房間.");
                        cm.dispose();
                    }
                } else {
                    cm.sendSimple("請叫你的隊長來找我");
                    cm.dispose();
                }
            }
        }
    } else if (status == 2) {

        if (selection < 2) {

            var requiedCount = [50, 30];
            var gainItems = [1122007, 2041211];

            if (!cm.haveItem(4001129, requiedCount[selection])) {
                cm.sendOk("很抱歉您並沒有#b#i4001129##k #b" + requiedCount[selection] + "#k個");
            } else if (!cm.canHold(gainItems[selection], 1)) {
                cm.sendOk("背包請清出空間.");
            } else {
                cm.gainItem(gainItems[selection], 1, true);
                cm.gainItem(gainItem[selection], requiedCount[selection] * -1);
            }
            cm.dispose();

        } else if (selection < 3) {
            if (!cm.haveItem(4001254, 50)) {
                cm.sendOk("很抱歉您並沒有#b#i4001254##k #b50#k個");
            } else if (!cm.canHold(1122058, 1)) {
                cm.sendOk("背包請清出空間.");
            } else {
                cm.gainItem(1122058, 1, true);
                cm.gainItem(4001254, -50);
            }
            cm.dispose();
        } else {
            var mapid = 980000000 + selection;
            var eim = cm.getEventManager("cpq").getInstance("cpq" + mapid);
            if (eim == null) {
                cm.getEventManager("cpq").startCarnivalInstance(mapid, cm.getPlayer());
            } else {
                var owner = cm.getChannelServer().getPlayerStorage().getCharacterByName(cm.getEventManager("cpq").getInstance("cpq" + mapid).getPlayers().get(0).getParty().getLeader().getName());
                owner.addCarnivalRequest(cm.getCarnivalChallenge(cm.getChar()));
                cm.openNpc(owner.getClient(), 2042001);
                cm.sendOk("您的挑戰已經發送。");
            }
            cm.dispose();

        }

    } else {
        cm.dispose();
    }
}

function checkLevelsAndMap(lowestlevel, highestlevel) {
    var party = cm.getParty().getMembers();
    var mapId = cm.getMapId();
    var valid = 0;
    var inMap = 0;

    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        if (!(cPlayer.getLevel() >= lowestlevel && cPlayer.getLevel() <= highestlevel) && cPlayer.getJobId() != 900) {
            valid = 1;
        }
        if (cPlayer.getMapid() != mapId) {
            valid = 2;
        }
    }
    return valid;
}

function getCPQField(fieldnumber) {
    var status = "";
    var event1 = cm.getEventManager("cpq");
    if (event1 != null) {
        var event = event1.getInstance("cpq" + (980000000 + (fieldnumber * 100)));
        if (event == null && fieldnumber != 5 && fieldnumber != 6 && fieldnumber != 9) {
            status = "擂台賽場地 " + fieldnumber + "(2v2)";
        } else if (event == null) {
            status = "擂台賽場地 " + fieldnumber + "(3v3)";
        } else if (event != null && (event.getProperty("started").equals("false"))) {
            var averagelevel = 0;
            for (i = 0; i < event.getPlayerCount(); i++) {
                averagelevel += event.getPlayers().get(i).getLevel();
            }
            averagelevel /= event.getPlayerCount();
            status = event.getPlayers().get(0).getParty().getLeader().getName() + "/" + event.getPlayerCount() + "人/平均等級 " + averagelevel;
        }
    }
    return status;
}
