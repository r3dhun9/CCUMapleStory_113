
/*
	Name: 微微安御守轉蛋NPC
	Place: 各大村莊
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (cm.haveItem(5310000)) {
            cm.sendYesNo("你有一些 #b#t5310000##k\r\n你想要嘗試運氣！？");
        } else {
            cm.sendOk("很抱歉由於你沒有#b#t5310000##k所以不能嘗試。");
            cm.safeDispose();
        }
    } else if (status == 1) {
        var item;
        if (Math.floor(Math.random() * 300) == 0) {
            var rareList = new Array(2022217, 2022221, 2022222, 2022223);

            item = cm.gainGachaponItem(rareList[Math.floor(Math.random() * rareList.length)], 1, "薇薇安御守");
            /*cm.sendNext("#b已經給您#r#v" + id + ":##t" + id + ":##b x" + gain + " 了唷,請去背包查收吧。");
        cm.WorldMessage("	恭喜 " + cm.getPlayer().getName() + "從正義轉蛋抽到了" + MapleItemInformationProvider.getInstance().getName(id) + "x" + gain);
        cm.dispose();*/
        } else {
            var itemList = new Array(2022216, 2022218, 2022219, 2022220);

            item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
        }

        if (item != -1) {
            cm.gainItem(5310000, -1);
            cm.sendOk("您已獲得 #b#t" + item + "##k.");
        } else {
            cm.sendOk("請檢查看看您是否有#t5310000##k，或者道具攔已滿。");
        }
        cm.safeDispose();
    }
}