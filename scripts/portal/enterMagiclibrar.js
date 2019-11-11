 /*
  * 很可疑的漢斯  任務代碼 : 20718
  * by:Kodan改
  */
function enter(pi) {
    if(pi.isQuestActive(20718) == true) {
		var em = pi.getEventManager("MagicQuest1");
        if (em == null) {
            pi.playerMessage(6,"找不到腳本請聯絡管理員");
        } else {
			var nextmap = pi.getC().getChannelServer().getMapFactory().getMap(910110000);
			if (nextmap.playerCount() == 0) {
                em.startInstance(pi.getPlayer());
				//pi.playerMessage(6,"test...");
                return true;
            } else {
                pi.playerMessage(6,"裡面有人正在挑戰，請稍後再嘗試。");
            }
        }
        //pi.warp(910110000, 0);
        //pi.getPlayer().startMapTimeLimitTask(10 * 60, pi.getMap(101000003));
    } else {
        pi.playPortalSE();
        pi.warp(101000003, 8);
    }
	return false;
}
