
/*
	Nella - Hidden Street : 1st Accompaniment
*/

var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        var mapId = cm.getMapId();
        if (mapId == 103000890) {
            cm.warp(103000000, "mid00");
            cm.removeAll(4001007);
            cm.removeAll(4001008);
            cm.dispose();
        } else {
            var outText;
            if (mapId == 103000805) {
                outText = "你確定要離開地圖？？";
            } else {
                outText = "一旦你離開地圖，你將不得不重新啟動整個任務，如果你想再次嘗試。你還是要離開這個地圖？";
            }
            if (status == 0) {
                cm.sendYesNo(outText);
            } else if (mode == 1) {
                cm.warp(103000890, "st00"); // Warp player
                cm.dispose();
            }
        }
    }
}