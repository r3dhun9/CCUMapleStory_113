/*
 * NPC: 潔淨的樹根
 * 任務: 很可疑的愛溫
 */
function start() {
    if (cm.isQuestActive(20716) && !cm.haveItem(4032142)) {
        cm.gainItem(4032142, 1);
        cm.sendOk("你從樹根中找到了#b清澈的樹脂#k");
        cm.dispose();
    }
}
