/*
 每日領取點數NPC 魯迪
 author: Redhung
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == -1)
        cm.dispose();
    else
        status++;
    if(status == 0) {
        cm.sendSimple("#b親愛的 #e#k#h # #n#b請問需要什麼幫助?#k \r\n#L0##r領取每日點數#k#l")
    }
    else if(status == 1) {
        if(selection == 0) {
            if(cm.getGainCash() == 0) {
                cm.getPlayer().modifyCSPoints(1, 3000, true);
                cm.setGainCash(1);
                cm.sendSimple("點數領取完畢，請確認是否成功領取，祝您楓之谷生活愉快！");
            }
            else {
                cm.sendSimple("您今日已經領取過了，請明日再來領取。");
                cm.dispose();
            }
        }
        else
            cm.dispose();
    }
}