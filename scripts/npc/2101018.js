function action(mode, type, selection) {
    cm.sendOk("競技場暫時不開放。");
    cm.dispose();
}
/*status = -1;
function start() {
    if((cm.getPlayer().getLevel() < 19 || cm.getPlayer().getLevel() > 30) && !cm.getPlayer().isGM()){
        cm.sendNext("你的等級不符合，不能參加沙漠競技場！！");
        cm.dispose();
        return;
    }
    action(1,0,0);
}

function action(mode, type, selection){
    status++;
    if (status == 4){
        cm.saveLocation("ARIANT");
        cm.warp(980010000, 3);
        cm.dispose();
    }
    if(mode != 1){
        if(mode == 0 && type == 0)
            status -= 2;
        else{
            cm.dispose();
            return;
        }
    }
    if (status == 0)
        cm.sendNext("納西沙漠國王在楓之谷的世界中，為人們做了一種有趣的競賽. 它叫做 #b沙漠競技場競賽#k.");
    else if (status == 1)
        cm.sendNextPrev("沙漠競技場的競賽規則是看誰的寶珠量最多則就是勝利者！！");
    else if (status == 2)
        cm.sendSimple("如果你認為你是一個勇敢的聖戰士 那麼就來挑戰吧！！\r\n#b#L0# 我已經閱讀以上規則了，我要參加沙漠競技場競賽.#l");
    else if (status == 3)
        cm.sendNext("好吧，現在開始展現你的真功夫，我會很期待的！！");
}*/