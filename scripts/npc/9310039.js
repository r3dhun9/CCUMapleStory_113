/*
 少林妖僧 -- 入口NPC
 */

var shaoling = 5;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple("#b親愛的 #k#h  ##e\r\n#b是否要挑戰武林妖僧副本??#k \r\n#L0##r我要挑戰武林妖僧#k#l");
        } else if (status == 1) {
            if (selection == 0) {
                var pt = cm.getPlayer().getParty();
                if (cm.getQuestStatus(8534) == 0) {
                    cm.sendOk("你似乎不夠資格挑戰武林妖僧！");
                    cm.dispose();
                } /*else if (cm.getBossLog('shaoling') >= 5) {
                    cm.sendOk("每天只能打5次妖僧！");
                    cm.dispose();
                }*/ else if (cm.getParty() == null) {
                    cm.sendOk("請組隊再來找我....");
                    cm.dispose();
                } else if (!cm.isLeader()) {
                    cm.sendOk("請叫你的隊長來找我!");
                    cm.dispose();
                } else if (pt.getMembers().size() < 1) {
                    cm.sendOk("需要 3 人以上的組隊才能進入！!");
                    cm.dispose();
                } else {
                    var party = cm.getParty().getMembers();
                    var mapId = cm.getMapId();
                    var next = true;
                    var levelValid = 0;
                    var inMap = 0;

                    var it = party.iterator();
                    while (it.hasNext()) {
                        var cPlayer = it.next();
                        if ((cPlayer.getLevel() >= 50 && cPlayer.getLevel() <= 80) || cPlayer.getJobId() == 900) {
                            levelValid += 1;
                        } else {
                            next = false;
                        }
                        if (cPlayer.getMapid() == mapId) {
                            inMap += (cPlayer.getJobId() == 900 ? 3 : 1);
                        }
                    }
                    if (inMap < 1) {
                        next = false;
                    }
                    if (next) {
                        var em = cm.getEventManager("shaoling");
                        if (em == null) {
                            cm.sendOk("當前副本有問題，請聯絡管理員....");
                        } else {
                            var prop = em.getProperty("state");
                            if (prop.equals("0") || prop == null) {
                                em.startPartyInstance(cm.getParty(), cm.getMap());
                                cm.setPartyBossLog("shaoling");
                                cm.dispose();
                                return;
                            } else {
                                cm.sendOk("裡面已經有人在挑戰...");
                            }
                        }
                    } else {
                        cm.sendOk("等級尚未達到 #r50#k 或者已經超過 #r80#k");
                    }
                }
                cm.dispose();
            }
        }
    }
}