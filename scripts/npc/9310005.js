function start() {
    if (cm.haveItem(4000194, 50)) {
        cm.gainItem(4000194, -50);
        cm.warp(701010322, "sp");
        cm.dispose();
    } else {
        cm.sendOk("你沒有黑羊毛50個!");
        cm.dispose();
    }
}