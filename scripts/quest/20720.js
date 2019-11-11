var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
		if(type == 1 && mode == 0) {
			qm.dispose();
			return;
		}else{
			qm.dispose();
			return;
		}
	}
	if(status == 0) {
		qm.sendNext("這段時間升級還順利嗎？現在應該在#m103000000#進行組隊任務吧。 雖然升級重要，但是還是要暫時接受騎士團的任務了 。 因為有新的情報來了。 ");
	}	
	else if (status == 1) {
		qm.sendAcceptDecline("這次任務雖然與 #m102000000#相關…但是前往 #m102000000#之後先回到 #b耶雷弗#k吧。因為要把這次任務相關的物品交給 #m102000000# 情報員。那麼就在耶雷弗見吧。 ");
	} else if (status == 2) {
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
