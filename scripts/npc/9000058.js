
var status = -1;
var picked = 0;
var state = -1;
var item;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status >= 2 || status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }

    if (status == 0) {
        if (!cm.isQuestFinished(29934)) {
            NewPlayer();
        }
        cm.sendSimple("歡迎來到中正谷~\r\n#b#L2#我要打開藍色小箱子#l\r\n#b#L6#我要抽月光寶盒#l");
    } else if (status == 1) {
        if (selection == 2) {
            if (cm.haveItem(4031307, 1) == true) {
                cm.gainItem(4031307, -1);
                cm.gainItem(2020020, 100);
                cm.sendOk("#b蛋糕不要吃太多~旅遊愉快~");
                cm.dispose();
            } else {
                cm.sendOk("#b檢查一下背包有沒有藍色禮物盒哦");
                cm.dispose();
            }
		} else if (selection == 6) {
			cm.dispose();
			cm.openNpc(9330022);
        }
    }
}

function NewPlayer() {
    if (!cm.haveItem(5000007, 1, true, true) && cm.canHold(5000007, 1)) {
        cm.gainPet(5000007, "黑色小豬", 1, 0, 100, 45, 0);
    }
    if (!cm.haveItem(1002419, 1, true, true) && cm.canHold(1002419, 1)) {
        cm.gainItemPeriod(1002419, 1, 30);
    }
    if (!cm.haveItem(5030000, 1, true, true) && cm.canHold(5030000, 1)) {
        cm.gainItemPeriod(5030000, 1, 30);
    }
    if (!cm.haveItem(5100000, 1, true, true) && cm.canHold(5100000, 1)) {
        cm.gainItem(5100000, 1);
    }
    if (!cm.haveItem(5370000, 1, true, true) && cm.canHold(5370000, 1)) {
        cm.gainItemPeriod(5370000, 1, 7);
    }
    if (!cm.haveItem(5520000, 1, true, true) && cm.canHold(5520000, 1)) {
        cm.gainItem(5520000, 1);
    }
    if (!cm.haveItem(5180000, 1, true, true) && cm.canHold(5180000, 1)) {
        cm.gainItemPeriod(5180000, 1, 28);
    }
    if (!cm.haveItem(5170000, 1, true, true) && cm.canHold(5170000, 1)) {
        cm.gainItemPeriod(5170000, 1, 30);
    }
    cm.forceCompleteQuest(29934); //完成新手獎勵
    cm.sendOk("歡迎來到楓之谷 請使用 @help/@幫助 了解各式指令\r\n\r\n\r\n遊戲愉快^^");
    cm.dispose();
    return;
}
