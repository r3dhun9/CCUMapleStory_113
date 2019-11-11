
/*
 名字: 		蓋瑞和莎媞
 地圖: 		結婚小鎮
 描述: 		
 */
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }
    if (!cm.haveItem(4213000)) {
        if (status == 0) {
            if (cm.getPlayer().getGender() == 1 && cm.getPlayer().getMarriageId() > 0 && cm.getMapId() == 680000000) {
                cm.sendGetText("妳是一個準備結婚的人嗎?? 快到這裡跟我們立下誓約吧!!\r\n#e#b我願意在此立下愛情的誓約#k#n");
            } else {
                cm.sendOk("你好像不需要#t4213000#啊，還是先去找個心愛的人吧，就像我們這樣~");
                cm.dispose();
            }
        } else if (status == 1) {
            if (cm.getText() == "我願意在此立下愛情的誓約") {
                cm.sendNext("誓約1完成！，那麼接下來就在立下另一個誓約2吧！");
            } else {
                cm.sendNext("妳好像誓約說錯了，請再找我嘗試一次！");
                cm.dispose();
            }
        } else if (status == 2) {
            cm.sendGetText("誓約2\r\n#e#b我會以真摯不變地心信奉愛情#k#n");
        } else if (status == 3) {
            if (cm.getText() == "我會以真摯不變地心信奉愛情") {
                cm.sendNext("恭喜完成以下的誓約這是小小的獎勵！#t4213000# x1。");
                cm.gainItem(4213000, 1);
                cm.dispose();
            } else {
                cm.sendNext("妳好像誓約說錯了，請再找我嘗試一次！");
                cm.dispose();
            }
        }
    } else {
        cm.sendOk("妳好像已經有了#t4213000# x1，不需要在立下愛情的誓約。");
        cm.dispose();
    }
}
