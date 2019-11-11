/* Author: Xterminator
	NPC Name: 		Trainer Frod
	Map(s): 		Victoria Road : Pet-Walking Road (100000202)
	Description: 		Pet Trainer
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        if (cm.haveItem(4031035)) {
            cm.sendNext("嗯，這是我哥哥的信！也許罵我想我不工作和東西......嗯？唉唉......你跟著我哥哥的意見和訓練有素的寵物，站起身來這裡，是吧？不錯！既然你辛辛苦苦來到這裡，我會提高你的親密水平與您的寵物。");
        } else {
            cm.sendOk("我哥哥告訴我，照顧寵物的障礙，當然，但是......既然我這麼遠從他身上，我不禁想遊手好閒......呵呵，因為我沒有看到他在眼前，還不如乾脆放鬆幾分鐘.");
            cm.dispose();
        }
    } else if (status == 1) {
        if (cm.getPlayer().getPet(0) == null) {
            cm.sendNextPrev("嗯...你真的沒有帶寵物來？快離開這兒！");
        } else {
            cm.gainItem(4031035, -1);
            cm.gainClosenessAll(2);
            cm.sendNextPrev("你怎麼看？難道你不認為你已經得到了你的寵物更接近？如果你有時間，再訓練你的寵物在這個障礙......當然當然，我的兄弟的許可。");
        }
        cm.dispose();
    }
}