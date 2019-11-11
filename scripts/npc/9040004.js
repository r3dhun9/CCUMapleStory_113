// 排行系統switch case 改寫
var status = -1;
var edit = true;
function start() {
    if (edit && !cm.getPlayer().isGM()) {
        msg = "本NPC#r維修中#k，請稍後再試。";
        cm.sendNext(msg);
        cm.dispose();
        return;
    }	
	action (1, 0, 0);
}

function action(mode, type, selection) {
	switch (mode) {
		case 1:
			status++;
		break;
		case 0:
			status--;
		break;
		default:
			cm.dispose();
		return;
	}
	switch (status) {
		case 0:
			cm.sendSimple("#b你好 #k#h  ##e  #b我是排名系統.#k\r\n#L0##r工會排名\n\#l\r\n#L1##g玩家排名\n\#l\r\n#L2##b楓幣排名#l\n\#l\r\n#L3##dDPM排名#l");
		break;
		case 1:
		gg = selection;
		switch(gg) {
			case 0:
				cm.displayGuildRanks();
				cm.dispose();
			break;
			case 1:
				cm.sendSimple("親愛的 #k#h ##e #b您現在要查看哪個職業的排行榜#k \r\n#L0##b全部\n\#l\r\n#L1##g冒險家\n\#l\r\n#L2##r貴族#l\n\#l\r\n#L3##d傳說#l\n\#l\r\n#L4##k回上一頁#l");
			break;
			case 2:
				cm.showmeso();
				cm.dispose();
			break;
			case 3:
				cm.showdpm();
				cm.dispose();
			break;
		}
		break;
		case 2:
		gg = selection;
		switch (gg) {
			case 0:
				cm.showlvl();
				cm.dispose();
			break;
			case 1:
				cm.sendSimple("親愛的 #k#h ##e #b您現在要查看哪個職業的排行榜#k \r\n#L0##b劍士\n\#l\r\n#L1##g法師\n\#l\r\n#L2##r弓箭手#l\n\#l\r\n#L3##d盜賊#l\n\#l\r\n#L4##b海盜#l\n\#l\r\n#L5##k回上一頁#l");
			break;
			case 2:
				status = 3;
				cm.sendSimple("親愛的 #k#h ##e #b您現在要查看哪個職業的排行榜#k \r\n#L0##b聖魂劍士\n\#l\r\n#L1##g烈焰巫師\n\#l\r\n#L2##r破風使者#l\n\#l\r\n#L3##d暗夜行者#l\n\#l\r\n#L4##b閃雷悍將#l\n\#l\r\n#L5##k回上一頁#l");
			break;
			case 3:
				cm.sendOk("傳說");
			break;
			default:
				status = 0;
				cm.sendSimple("#b你好 #k#h  ##e  #b我是排名系統.#k\r\n#L0##r工會排名\n\#l\r\n#L1##g玩家排名\n\#l\r\n#L2##b楓幣排名#l\n\#l\r\n#L3##dDPM排名#l");
			return;
		}
		break;
		case 3:
		gg = selection;
		switch (gg) {
			case 0:
				cm.sendOk("劍士");
				cm.dispose();
			break;
			case 1:
				cm.sendOk("法師");
				cm.dispose();
			break;
			case 2:
				cm.sendOk("弓箭手");
				cm.dispose();
			break;
			case 3:
				cm.sendOk("盜賊");
				cm.dispose();
			break;
			case 4:
				cm.sendOk("海盜");
				cm.dispose();
			break;
			default:
				status = 1;
				cm.sendSimple("親愛的 #k#h ##e #b您現在要查看哪個職業的排行榜#k \r\n#L0##b全部\n\#l\r\n#L1##g冒險家\n\#l\r\n#L2##r貴族#l\n\#l\r\n#L3##d傳說#l\n\#l\r\n#L4##k回上一頁#l");
			return;
		}
		break;
		case 4:
		gg = selection;
		switch (gg) {
			case 0:
				cm.sendOk("聖魂劍士");
				cm.dispose();
			break;
			case 1:
				cm.sendOk("烈焰巫師");
				cm.dispose();
			break;
			case 2:
				cm.sendOk("破風使者");
				cm.dispose();
			break;
			case 3:
				cm.sendOk("暗夜行者");
				cm.dispose();
			break;
			case 4:
				cm.sendOk("閃雷悍將");
				cm.dispose();
			break;
			default:
				status = 1;
				cm.sendSimple("親愛的 #k#h ##e #b您現在要查看哪個職業的排行榜#k \r\n#L0##b全部\n\#l\r\n#L1##g冒險家\n\#l\r\n#L2##r貴族#l\n\#l\r\n#L3##d傳說#l\n\#l\r\n#L4##k回上一頁#l");
			return;
		}
		break;
	}

}