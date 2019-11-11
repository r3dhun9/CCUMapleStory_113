/* Author: Xterminator
	NPC Name: 		Heena
	Map(s): 		Maple Road : Lower level of the Training Camp (2)
	Description: 		Takes you outside of Training Camp
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
        cm.sendOk("沒完成新手訓驗嘛? 如果想要離開這裡, 請不要吝嗇的告訴我。.");
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendYesNo("你完成你的訓練了嘛? 如果你想要離開的話，我可以帶你離開。");
    } else if (status == 1) {
        cm.sendNext("那我要帶你離開這裡， 加油！");
    } else if (status == 2) {
        cm.warp(3, 0);
        cm.dispose();
    }
}