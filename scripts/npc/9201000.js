var status = -1;
var firstSelection = -1;
var secondSelection = -1;
var ingredients_0 = Array(4011004, 4021007);
var ingredients_1 = Array(4011006, 4021007);
var ingredients_2 = Array(4011007, 4021007);
var ingredients_3 = Array(4021009, 4021007);
var num = Array(1, 2, 3);
var mats = Array();
var mesos = Array(1000000, 2000000, 3000000);

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (cm.getPlayer().getGender() != 0) {
            cm.sendOk("很抱歉，我只能幫男性做戒指。");
            cm.dispose();
        }
        if (cm.getPlayer().getMarriageId() > 0) {
            cm.sendNext("想要訂婚??那請把你另外一半給甩掉吧！！");
            cm.dispose();
        } else {
            cm.sendSimple("我販賣的戒指總共有4種。你想要買哪種戒指呢？ 挑選她喜歡的戒指吧！。\r\n#b#L0##i2240004##t2240004##l\r\n#L1##i2240007##t2240007##l\r\n#L2##i2240010##t2240010##l\r\n#L3##i2240013##t2240013##l#k");
        }
    } else if (status == 1) {
        firstSelection = selection;
        cm.sendSimple("我明白，你需要幾克拉？？\r\n#b#L0#1 克拉#l\r\n#L1#2 克拉#l\r\n#L2#3 克拉#l#k");
    } else if (status == 2) {
        secondSelection = selection;
        var prompt = "在這種情況下，為了要做出好品質的裝備。請確保您有空間在您的裝備欄！#b";
        switch (firstSelection) {
            case 0:
                mats = ingredients_0;
                break;
            case 1:
                mats = ingredients_1;
                break;
            case 2:
                mats = ingredients_2;
                break;
            case 3:
                mats = ingredients_3;
                break;
            default:
                cm.dispose();
                return;
        }
        for (var i = 0; i < mats.length; i++) {
            prompt += "\r\n#i" + mats[i] + "##t" + mats[i] + "#";
        }
		prompt += "\r\n 以上材料各x" + num[secondSelection] + "個";
        prompt += "\r\n#i4031138# " + mesos[secondSelection]; + " 楓幣";
        cm.sendYesNo(prompt);
    } else if (status == 3) {
        if (cm.getMeso() < mesos[secondSelection]) {
            cm.sendOk("你沒有錢，滾好嘛！？");
        } else {
            var complete = true;
            for (var i = 0; i < mats.length; i++) {
                if (!cm.haveItem(mats[i], num[secondSelection])) {
                    complete = false;
                    break;
                }
            }
            if (!complete) {
                cm.sendOk("沒有材料，滾好嘛！？");
            } else {
                cm.sendOk("做完了，趕快去找你心愛的人求婚吧！！");
                cm.gainMeso(-mesos[secondSelection]);
                for (var i = 0; i < mats.length; i++) {
                    cm.gainItem(mats[i], -num[secondSelection]);
                }
                cm.gainItem(2240004 + (firstSelection * 3) + secondSelection, 1);
            }
        }
        cm.dispose();
    }
}