var status = 0;

function start() {
    if (cm.getPlayer().getMapId() >= 749020000
     && cm.getPlayer().getMapId() <= 749020800) {
        status = 1;
        cm.sendYesNo("恭喜你完成了，我給了你一個叉子接下來請加油~");
    } else {
        cm.sendYesNo("嗨，我是#p9330079# 活動體驗結束了，想要離開了嗎???");
    }
}

function action(mode, type, selection) {
    if (mode != 1) {
        if (mode == 0)
            cm.sendOk("需要的時候，再來找我吧。");
        cm.dispose();
        return;
    }
    status++;
    if (status == 1) {
        var returnMap = cm.getSavedLocation("BIRTHDAY");
        if (returnMap < 0) {
            cm.warp(100000000, 0);
        } else {
            cm.clearSavedLocation("BIRTHDAY");
            cm.warp(returnMap, 0);
        }
        cm.dispose();
    } else if (status == 2) {
    	cm.gainItem(1302085, 1);
    	cm.warpBack(749020900, 749020920, 60);
        cm.dispose();
    }
}
