var status = -1;
function action(mode, type, selection) {
    if (cm.isQuestActive(2236)) {
		var fkmap1 = cm.getPlayer().getMapId() == 105050200;
		var fkmap2 = cm.getPlayer().getMapId() == 105060000;
		var fkmap3 = cm.getPlayer().getMapId() == 105070000;
		var fkmap4 = cm.getPlayer().getMapId() == 105090000;
		var fkmap5 = cm.getPlayer().getMapId() == 105090100;
		var fk1 = cm.getPlayerVariable('fk1') == null;
		var fk2 = cm.getPlayerVariable('fk2') == null;
		var fk3 = cm.getPlayerVariable('fk3') == null;
		var fk4 = cm.getPlayerVariable('fk4') == null;
		var fk5 = cm.getPlayerVariable('fk5') == null;
		var fk11 = cm.getPlayerVariable('fk1') != null;
		var fk22 = cm.getPlayerVariable('fk2') != null;
		var fk33 = cm.getPlayerVariable('fk3') != null;
		var fk44 = cm.getPlayerVariable('fk4') != null;
        if (fkmap1) {
            if (fk1) {
                cm.setPlayerVariable('fk1', '1');
                cm.gainItem(4032263, -1);
				cm.sendNext("已經完成了5/1。");
            } else {
                cm.sendOk("有種強力的魔咒，無法接近。");
            }
        } else if (fkmap2) {
            if (fk2 && fk11) {
                cm.setPlayerVariable('fk2', '1');
                cm.gainItem(4032263, -1);
				cm.sendNext("已經完成了5/2。");
            } else {
                cm.sendOk("有種強力的魔咒，無法接近。");
            }
        } else if (fkmap3) {
            if (fk3 && fk11 && fk22) {
                cm.setPlayerVariable('fk3', '1');
                cm.gainItem(4032263, -1);
				cm.sendNext("已經完成了5/3。");
            } else {
                cm.sendOk("有種強力的魔咒，無法接近。");
            }
        } else if (fkmap4) {
            if (fk4 && fk11 && fk22 && fk33) {
                cm.setPlayerVariable('fk4', '1');
                cm.gainItem(4032263, -1);
				cm.sendNext("已經完成了5/4。");
            } else {
                cm.sendOk("有種強力的魔咒，無法接近。");
            }
        } else if (fkmap5) {
            if (fk5 && fk11 && fk22 && fk33 && fk44) {
                cm.setPlayerVariable('fk5', '1');
                cm.gainItem(4032263, -2);
                cm.forceCompleteQuest(2236);
                cm.gainExp(30000);
                cm.sendNext("完成任務。 經驗值:30000");
            } else {
                cm.sendOk("有種強力的魔咒，無法接近。");
            }
        }
    }
    cm.dispose();
}