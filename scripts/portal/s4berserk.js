function enter(pi) {
	if (pi.getQuestStatus(6153) == 1) {
		if (!pi.haveItem(4031471)) {
			if (pi.haveItem(4031475)) {
				var em = pi.getEventManager("4jberserk");
				if (em == null) {
					pi.playerMessage("找不到副本請聯繫管理員。");
				} else {
					em.startInstance(pi.getPlayer());
					return true;
				}
				// start event here
				// if ( ret != 0 ) target.message( "Other character is on the quest currently. Please try again later." );
			} else {
				pi.playerMessage("你需要一把鑰匙才能進入此地方。");
			}
		} else {
			pi.playerMessage("你需要一把鑰匙才能進入此地方。");
		}
	} else {
		pi.playerMessage("你不能進入這裡。");
	}
	return false;
}
