var status = 0;
var banMap = Array(109080000, 109080010, 109040000, 109030001, 109060000, 109010000);

function start() {
    cm.sendNext("嗨 我是 #b江#k. 我在等待我的兄弟 #b保羅#k. 他應該現在在這裡...");
}


function action(mode, type, selection) {
    for (var i = 0; i < banMap.length; i++) {
        if (cm.getPlayer().getMapId() == banMap[i]) {
            cm.sendOk("幹！想要偷跑吃屎去吧！");
            cm.dispose();
        }
    }
	if (cm.getPlayer().getJob() == 2000) {
		cm.sendNext("幹！想使用BUG吃屎去吧！");
		cm.dispose();
	}
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 2 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 1) {
            cm.sendNextPrev("嗯......我該怎麼辦？本次活動將開始，很快......很多人去參與這項活動，所以我們最好快點來......");
        } else if (status == 2) {
            cm.sendSimple("嗨... 你為什麼不跟我走？我想我的兄弟會與其他人...\r\n#L0##e1.#n#b 什麼樣的活動內容??#k#l\r\n#L1##e2.#n#b 介紹活動內容讓我認識..#k#l\r\n#L2##e3.#n#b 好了，我們走吧！#k#l");
        } else if (status == 3) {
            if (selection == 0) {
                cm.sendNext("所有本月初，楓之谷環球慶祝其三週年！全球機制將舉行驚喜GM活動在整個活動期間，所以留在你的腳趾，並確保參與活動的至少一個為偉大的獎品！");
                cm.dispose();
            } else if (selection == 1) {
                cm.sendSimple("有許多活動官則。這將幫助在你開始活動之前。好了...選擇你想了解的活動.. #b\r\n#L0# 爬繩子#l\r\n#L1# 終極忍耐#l\r\n#L2# 滾雪球#l\r\n#L3# 打果子#l\r\n#L6# 打瓶蓋#l\r\n#L4# 是非題大考驗#l\r\n#L5# 尋寶#l#k");
            } else if (selection == 2) {
                if (!cm.canHold()) {
                    cm.sendNext("請確認是否身上有空位。");
                } else if (cm.getChannelServer().getEvent() > -1) {
                    if (cm.haveItem(4031017)) {
                        cm.removeAll(4031017);
                    }
					cm.cancelChalkboard();
                    cm.saveReturnLocation("EVENT");
                    cm.getPlayer().setChalkboard(null);
                    cm.warp(cm.getChannelServer().getEvent(), cm.getChannelServer().getEvent() == 109080000 || cm.getChannelServer().getEvent() == 109080010 ? 0 : "join00");
                } else {
                    cm.sendNext("活動尚未開放，請確認是否你有在24小時內參加過一個活動。請稍後在試！");
                }
                cm.dispose();
            }
        } else if (status == 4) {
            if (selection == 0) {
                cm.sendNext("#b[爬繩子]#k 自己#e#rGoogle#k!");
                cm.dispose();
            } else if (selection == 1) {
                cm.sendNext("#b[終極忍耐] 自己#e#rGoogle#k!");
                cm.dispose();
            } else if (selection == 2) {
                cm.sendNext("#b[滾雪球]#k 自己#e#rGoogle#k!");
                cm.dispose();
            } else if (selection == 3) {
                cm.sendNext("#b[打果子]#k 自己#e#rGoogle#k!");
                cm.dispose();
            } else if (selection == 6) {
                cm.sendNext("#b[打瓶蓋]#k 自己#e#rGoogle#k!");
                cm.dispose();
            } else if (selection == 4) {
                cm.sendNext("#b[是非題大考驗]#k 自己#e#rGoogle#k!");
                cm.dispose();
            } else if (selection == 5) {
                cm.sendNext("#b[尋寶]#k 自己#e#rGoogle#k!");
                cm.dispose();
            }
        }
    }
}