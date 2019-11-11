/*
NPC- 杜亞詩9330021
地點：不夜城
*/

var status = 0;
var beauty = 0;
var mhair = Array(30030, 30020, 30000, 30310, 30330, 30060, 30150, 30410, 30210, 30140, 30120, 30200);
var fhair = Array(31050, 31040, 31000, 31150, 31310, 31300, 31160, 31100, 31410, 31030, 31080, 31070, 31340);
var hairnew = Array();

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status >= 0) {
			cm.sendNext("如果有需要再來找我唷。");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("嗨，我是#p9330021# 如果你有 #b#t5150017##k 或者 #b#t5151013##k 就可以來找我唷！ 選擇一個服務: \r\n#L0#使用:#b#t5150017##k \r\n#L1#使用:#b#t5151013##k");
		} else if (status == 1) {
			if (selection == 0) {
				beauty = 1;
				hairnew = Array();
                if (cm.getPlayer().getGender() == 0)
                    for(var i = 0; i < mhair.length; i++)
                        hairnew.push(mhair[i] + parseInt(cm.getPlayer().getHair()% 10));
                if (cm.getPlayer().getGender() == 1)
                    for(var i = 0; i < fhair.length; i++)
                        hairnew.push(fhair[i] + parseInt(cm.getPlayer().getHair() % 10));
                cm.sendStyle("選擇一個想要的.", hairnew);
			} else if (selection == 1) {
                beauty = 2;
                haircolor = Array();
                var current = parseInt(cm.getPlayer().getHair()/10)*10;
                for(var i = 0; i < 8; i++)
                    haircolor.push(current + i);
                cm.sendStyle("選擇一個想要的", haircolor);
			}
		}
		else if (status == 2){
			cm.dispose();
			if (beauty == 1){
				if (cm.haveItem(5150017) == true){
					cm.gainItem(5150017, -1);
					cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length)]);
					cm.sendOk("你照鏡子看看吧～！");
				} else {
					cm.sendNext("痾.... 貌似沒有#t5150017#。");
				}
            }
			if (beauty == 2){
				if (cm.haveItem(5151013) == true){
					cm.gainItem(5151013, -1);
					cm.setHair(haircolor[Math.floor(Math.random() * haircolor.length)]);
					cm.sendOk("你照鏡子看看吧～！");
				} else {
					cm.sendNext("痾.... 貌似沒有#t5151013#。");
				}
			}
		}
	}
}
