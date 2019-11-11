/*
    Mong from Kong - Victoria Road : Kerning City (103000000)
*/

function start() {
    cm.sendYesNo("你想進入網咖嗎？");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(193000000, 0);
    } else {
            cm.sendNext("你還不想去啊？好吧，等你想去時再跟我說。");
    }
    cm.dispose();
}