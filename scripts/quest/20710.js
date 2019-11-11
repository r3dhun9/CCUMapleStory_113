var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            qm.dispose();
            return;

        } else {
            qm.dispose();
            return;

        }

    }

    if (status == 0) {
        qm.sendAcceptDecline("其實…我真不敢相信但是你說是騎士團員那就沒辦法了。又沒有其他人可以調查…那就說明這次的任務吧。");

    } else if (status == 1) {
        qm.forceStartQuest();
        qm.dispose();

    }

}
