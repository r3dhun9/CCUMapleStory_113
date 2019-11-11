// 飼養提提歐任務。
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    } else if (mode == 0 && status == 2) {
        cm.sendNext("想好在告訴我。");
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendSimple("找我有什麼事情嗎??\r\n#L1#其他/製作製作濃縮離乳食");
    } else if (status == 1) {
        if (selection == 1) {
            if (cm.getQuestStatus(20528) == 1) {
                cm.sendSimple("那麼要做哪一階的呢??\r\n#L1##t4032196##i4032196##l\r\n#L2##t4032197##i4032197##l\r\n#L3##t4032198##i4032198##l");
            } else {
                cm.sendNext("不覺得自己很不適合？");
                cm.dispose();
            }
        }
    } else if (status == 2) {
        if (selection == 1) {
            status = 3;
            cm.sendYesNo("確定是否準備好了這些材料了?? \r\n #t4000236##i4000236# x30 \r\n #t4000237##i4000237# x 30 \r\n #t4000238##i4000238# x30 \r\n #b楓幣:9,000,000");
        } else if (selection == 2) {
            status = 4;
            cm.sendYesNo("確定是否準備好了這些材料了?? \r\n #t4000236##i4000236# x30 \r\n #t4000237##i4000237# x 30 \r\n #b楓幣:9,000,000");
        } else if (selection == 3) {
            status = 5;
            cm.sendYesNo("確定是否準備好了這些材料了?? \r\n #t4032196##i4032196# x3 \r\n #t4032197##i4032197# x3");
        }
    } else if (status == 4) {
        if (cm.haveItem(4000236, 30) && cm.haveItem(4000237, 30) && cm.haveItem(4000238, 30) && cm.getMeso() >= 9000000) {
            cm.gainItem(4000236, -30);
            cm.gainItem(4000237, -30);
            cm.gainItem(4000238, -30);
            cm.gainMeso(-9000000);
            cm.gainItem(4032196, 1);
            cm.sendNext("完成了");
        } else {
            cm.sendNext("貌似沒有收集到我要的材料。");
        }
        cm.dispose();
    } else if (status == 5) {
        if (cm.haveItem(4000236, 30) && cm.haveItem(4000237, 30) && cm.getMeso() >= 9000000) {
            cm.gainItem(4000236, -30);
            cm.gainItem(4000237, -30);
            cm.gainMeso(-9000000);
            cm.gainItem(4032197, 1);
            cm.sendNext("完成了");
        } else {
            cm.sendNext("貌似沒有收集到我要的材料。");
        }
        cm.dispose();
    } else if (status == 6) {
        if (cm.haveItem(4032196, 3) && cm.haveItem(4032197, 3)) {
            cm.gainItem(4032198, 3);
            cm.sendNext("完成了");
        } else {
            cm.sendNext("貌似沒有收集到我要的材料。");
        }
        cm.dispose();
    }
}