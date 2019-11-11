
/*
	名字: 		傑伊克
	地圖: 		婚禮村小鎮
	描述: 		結婚戒指交換
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (cm.getPlayer().getMarriageId() <= 0) {
            cm.sendOk("你好像還沒結婚呢，婚都沒結就想要結婚戒指？你還是先找個心愛的人，結完婚再來吧~");
            cm.dispose();
        } else {
            cm.sendSimple("你好啊~ 我聞到了一股甜蜜蜜的新婚味道哦~ 哎喲，怎麼還戴著訂婚戒指啊？結了婚就要換漂亮的結婚戒指才行嘛！你願意的話，我可以給你們換，怎麼樣？\r\n\r\n#L0# 把訂婚戒指換成結婚戒指。#l");
        }
    } else if (status == 1) {
		if (cm.haveItem(4210000)) {
			cm.gainItem(4210000,-1);
			cm.gainItem(1112300,1);
		} else if (cm.haveItem(4210001)) {
			cm.gainItem(4210001,-1);
			cm.gainItem(1112301,1);
		} else if (cm.haveItem(4210002)) {
			cm.gainItem(4210002,-1);
			cm.gainItem(1112302,1);
		} else if (cm.haveItem(4210003)) {
			cm.gainItem(4210003,-1);
			cm.gainItem(1112303,1);
		} else if (cm.haveItem(4210004)) {
			cm.gainItem(4210004,-1);
			cm.gainItem(1112304,1);
		} else if (cm.haveItem(4210005)) {
			cm.gainItem(4210005,-1);
			cm.gainItem(1112305,1);
		} else if (cm.haveItem(4210006)) {
			cm.gainItem(4210006,-1);
			cm.gainItem(1112306,1);
		} else if (cm.haveItem(4210007)) {
			cm.gainItem(4210007,-1);
			cm.gainItem(1112307,1);
		} else if (cm.haveItem(4210008)) {
			cm.gainItem(4210008,-1);
			cm.gainItem(1112308,1);
		} else if (cm.haveItem(4210009)) {
			cm.gainItem(4210009,-1);
			cm.gainItem(1112309,1);
		} else if (cm.haveItem(4210010)) {
			cm.gainItem(4210010,-1);
			cm.gainItem(1112310,1);
		} else if (cm.haveItem(4210011)) {
			cm.gainItem(4210011,-1);
			cm.gainItem(1112311,1);
		} else {
			cm.sendOk("你好像什麼都沒有我不能免費換給你.....");
		}
		cm.dispose();
    }
}