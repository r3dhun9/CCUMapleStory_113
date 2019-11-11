importPackage(Packages.tools);
importPackage(Packages.client);

var status = -1;
var sel;

function start() {
	if (cm.getPlayer().getLevel() < 19 || cm.getPlayer().getLevel() > 30 && !cm.getPlayer().isGM()) {
		cm.sendOk("你的等級需求不符合！");
        cm.dispose();
        return;
    }
    if(cm.getPlayer().getMapId() % 10 == 1)
        cm.sendSimple("Do you have a request for me?\r\n#b#L0# Give me #t2270002# and #t2100067#.#l\r\n#L1# What should I do?#l\r\n#L2# Get me out of here.#l");
    else
        cm.sendSimple(cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) == cm.getPlayer().getName() ? "你要開始進入競技大會嗎？#b\r\n#b#L3#準備進入競技場！#l\r\n#L1#我要踢出其他角色#l\r\n#L2#請讓我離開這裡#l" : "你想要做什麼？#b\r\n#L2#讓我離開這裡#l");
}

function action(mode, type, selection){
    status++;
    if (mode != 1) {
        if (mode == 0 && type == 0)
            status -= 2;
        else {
            cm.dispose();
            return;
        }
    }
    if (cm.getPlayer().getMapId() % 10 == 1) {
        if (status == 0) {
            if (sel == undefined)
                sel = selection;
            if (sel == 0) {
                if (cm.haveItem(2270002))
                    cm.sendNext("You already have #b#t2270002##k.");
                else if (cm.canHold(2270002) && cm.canHold(2100067)) {
                    if (cm.haveItem(2100067))
                        cm.removeAll(2100067);
                    cm.gainItem(2270002, 32);
                    cm.gainItem(2100067, 5);
                    cm.sendNext("Now lower the HP of the monsters, and use #b#t2270002##k to absorb their power!");
                } else
                    cm.sendNext("Check and see if your Use inventory is full or not");
                cm.dispose();
            } else if(sel == 1) {
				status = 1;
                cm.sendNext("What do you need to do? You must be new to this. Allow me explain in detail.");
            } else
                cm.sendYesNo("Are you sure you want to leave?");
        } else if (status == 1) {
            if (mode == 1) {
                cm.warp(980010020);
                cm.dispose();
                return;
            }
		} else if (status == 2) {
            cm.sendNextPrev("It's really simple, actually. You'll receive #b#t2270002##k from me, and your task is to eliminate a set amount of HP from the monster, then use #b#t2270002##k to absorb its monstrous power.");
        } else if (status == 3)
            cm.sendNextPrev("It's simple. If you absorb the power of the monster #b#t2270002##k, then you'll make #b#t4031868##k, which is something Queen Areda loves. The combatant with the most jewels wins the match. It's actually a smart idea to prevent others from absorbing in order to win.");
        else if (status == 4)
            cm.sendNextPrev("One thing. #rYou may not use pets for this.#k Understood?~!");
        else if (status == 5)
            cm.dispose();
    } else {
        var nextchar = cm.getMap(cm.getPlayer().getMapId()).getCharacters().iterator();
        if (status == 0) {
            if (sel == undefined)
                sel = selection;
            if (sel == 1)
                if (cm.getPlayerCount(cm.getPlayer().getMapId()) > 1) {
                    var text = "你想要踢除哪一個角色？";
                    var name;
                    for (var i = 0; nextchar.hasNext(); i++) {
                        name = nextchar.next().getName();
                        if (!cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1).equals(name))
                            text += "\r\n#b#L" + i + "#" + name + "#l";
                    }
                    cm.sendSimple(text);
                } else {
                    cm.sendNext("這裡沒有其他角色可以踢除！");
                    cm.dispose();
                }
            else if (sel == 2) {
                if (cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) == cm.getPlayer().getName())
                    cm.sendYesNo("你確定要離開沙漠競技場，由於你是房主所以如果離開的話，整個競技場就會關閉了！");
                else
                    cm.sendYesNo("你確定要離開沙漠競技場？"); //No GMS like.
            } else if (sel == 3)
                if (cm.getPlayerCount(cm.getPlayer().getMapId()) > 0)
                    cm.sendYesNo("房間內所有的設置，並沒有其他的角色想加入這場競賽。你想現在開始競賽？");
                else {
                    cm.sendNext("你需要兩個以上的角色才能開始這場競賽！");
                    cm.dispose();
                }
        } else if (status == 1) {
            if (sel == 1) {
                for (var i = 0; nextchar.hasNext(); i++)
                    if (i == selection) {
                        nextchar.next().changeMap(cm.getMap(980010000));
                        break;
                    } else
                        nextchar.next();
                cm.sendNext("角色已被踢除."); //Not GMS like
            } else if(sel == 2) {
                if (cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) != cm.getPlayer().getName())
                    cm.warp(980010000);
                else {
                    cm.getPlayer().removeAriantRoom((cm.getPlayer().getMapId() / 100) % 10);
                    cm.mapMessage(6, cm.getPlayer().getName() + " 已經離開了競技場，所以競技場將立即關閉。");
                    cm.warpMap(980010000, 0);
                }
            } else {
				cm.startAriantPQ(cm.getPlayer().getMapId() + 1);
            }
            cm.dispose();
        }
    }
}
