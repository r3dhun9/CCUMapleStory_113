/*
 金鉤海賊王作戰PQ
*/


function checkMap() {
    var map = [925100000, 925100100, 925100200, 925100201, 925100202, 925100300, 925100301, 925100302, 925100400, 925100400, 925100500];
    for (var i = 0; i < map.length; i++) {
        if (cm.getPlayerCount(map[i]))
            return false;
    }
    return true;
}


function action(mode, type, selection) {
    cm.removeAll(4001117);
    cm.removeAll(4001120);
    cm.removeAll(4001121);
    cm.removeAll(4001122);
    if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
        cm.sendOk("請找隊長來找我。");
    } else {
        var party = cm.getPlayer().getParty().getMembers();
        var mapId = cm.getPlayer().getMapId();
        var next = true;
        var size = 0;
        var it = party.iterator();
        while (it.hasNext()) {
            var cPlayer = it.next();
            var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
            if (ccPlayer == null || ccPlayer.getLevel() < 55 || ccPlayer.getLevel() > 200) {
                next = false;
                break;
            }
            size += (ccPlayer.isGM() ? 2 : 1);
        }
        if (next && size >= 3) {
            if (checkMap()) {
                var em = cm.getEventManager("Pirate");
                if (em == null) {
                    cm.sendOk("找不到腳本，請聯繫GM！！");
                } else {
                    em.startPartyInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap());
                }
            } else {
                cm.sendOk("目前有人在打囉～");
            }
        } else {
            cm.sendOk("需要3個人以上 等級必須是55到200級");
        }
    }
    cm.dispose();
}