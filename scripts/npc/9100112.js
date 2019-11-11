/*
 尋找組隊發廣播npc
 by:Kodan
 */

var status = -1;
var msg = "";
var edit = false;
var pt = 0;
var selected = 0;
var ppl = -1;
var next = true;

function start() {
    if (edit && !cm.getPlayer().isGM()) {
        msg = "本NPC#r維修中#k，請稍後再試。";
        cm.sendNext(msg);
        cm.dispose();
        return;
    }
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }
    if (cm.getParty() == null) {
        msg = "請組隊再來找我....";
        next = false;
    } else if (!cm.isLeader()) {
        msg = "請叫你的隊長來找我!";
        next = false;
	}
	if (!next) {
        cm.sendNext(msg);
        cm.dispose();
        return;
    }
    if (status == 0) {
        msg = "本NPC可以提供給您招收組隊人數\r\n#L1##r是，我要徵收組隊#l";
        cm.sendSimple(msg);
    } else if (status == 1) {
        if (mode == 1) {
            pt = selection;
        }
        msg = "你要徵收幾個人??#b\r\n" +
                "#L1#1\r\n" +
                "#L2#2\r\n" +
                "#L3#3\r\n" +
                "#L4#4\r\n" +
                "#L5#5\r\n";
        cm.sendSimple(msg);
    } else if (status == 2) {
        ppl = selection;
        pqname = getPQMap(cm.getPlayer());
        cm.sendNext("已發出去公告了，請等人來吧。");
        cm.worldMessage("『組隊招募公告<頻道: " + cm.getClient().getChannel() + ">』：玩家: " + cm.getChar().getName() + " 組隊任務:" + pqname + " 缺了:" + ppl + " 人");
        cm.dispose();
    }
}

function getPQMap(chr) {
    switch (chr.getMapId()) {
        case 100:
            pqname = "";
            break;
		case 103000000:
			pqname ="超級綠水靈";
			break;
		case 200080101:
			pqname ="女神的痕跡";
			break;
		case 221024500:
			pqname ="101時空的裂縫";
			break;
		case 300030100:
			pqname ="毒物森林";
			break;
		case 261000011:
		case 261000021:
			pqname ="羅密歐&&茱麗葉";
			break;
		case 251010404:
			pqname ="金勾海賊王";
			break;
        case 910000000:
            pqname = "BOSSPQ";
            break;
    }
    return pqname;
}