/* 不夜城護膚NPC ID 9330025
*/
var status = 0;
var skin = Array(0, 1, 2, 3, 4);
var price;

function start() {
    cm.sendSimple("歡迎來到不夜城護膚中心! 是否有想要染的皮膚呢?? 就像我的健康皮膚??  如果你有 #b#t5153004##k, 就可以隨意染的想到的皮膚~~~\r\n\#L2#我已經有#t5153004#!#l");
}

function action(mode, type, selection) {
    if (mode < 1)
        cm.dispose();
    else {
        status++;
        if (status == 1)
            cm.sendStyle("選一個想要的風格.", skin);
        else {
            if (cm.haveItem(5153004)){
                cm.gainItem(5153004, -1);
                cm.setSkin(selection);
                cm.sendOk("享受!");
            } else
                cm.sendOk("您貌似沒有#b#t5153004##k..");
            cm.dispose();
        }
    }
}
