
/* Kedrick
	Fishking King NPC
*/

var status = -1;
var sel;

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

    if (status == 0) {
        cm.sendSimple("我能為您做什麼嗎？？\n\r #L4#教我怎麼釣魚。#l \n\r #L5#使用500個鮭魚換取 #i1142071#標準國字勳章 [期限 : 30 天]#l");
    } else if (status == 1) {
        sel = selection;
        if (sel == 4) {
            cm.sendOk("買著釣竿然後做釣魚椅子每1分鐘就會有東西。");
            cm.safeDispose();
        } else if (sel == 5) {
            if (cm.haveItem(4031648, 500)) {
                if (cm.canHold(1142071)) {
                    cm.gainItem(4031648, -500);
                    cm.gainItemPeriod(1142071, 1, 30);
                    cm.sendOk("恭喜拿到了 #b#i1142071##k!")
                } else {
                    cm.sendOk("請確認裝備欄是否有足夠。");
                }
            } else {
                cm.sendOk("請給我 500個 #i4031648:# 我才能給你 #i1142071#")
            }
            cm.safeDispose();
        }
    }
}