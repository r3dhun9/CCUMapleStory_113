var status = 0;
var cost = 3000;

function start() {
    cm.sendYesNo("請問是否想去日本神社?? 3000楓幣一次~~~~");
}

function action(mode, type, selection) {
    if (mode != 1) {
        if (mode == 0)
            cm.sendOk("既然你不要那就算了~~~");
        cm.dispose();
        return;
    }
    status++;
    if (status == 1) {
        if (cm.getMeso() < cost) {
            cm.sendOk("你的錢似乎不夠喔!");
            cm.dispose();
        } else {
            cm.gainMeso(-cost);
            cm.warp(800000000, 0);
            cm.dispose();
        }
    }
}
