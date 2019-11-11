var status = 0;

function start() {
	cm.sendYesNo("準備挑戰緊張又刺激的活動了嗎??");
}

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
		return;
	}
	status++;
	if (status == 1) {
		if (cm.getPlayer().hasEquipped(1302110)) {
			var em = cm.getEventManager("Cake");
			if (em == null) {
				cm.sendOk("找不到腳本，請聯繫GM！");
				cm.dispose();
				return;
			} else {
				var prop = em.getProperty("state");
				if (prop == null || prop.equals("0")) {
					em.startInstance(cm.getPlayer());
				} else {
					cm.sendOk("看來已經有人試圖點燃蠟燭耶！");
					cm.dispose();
					return;
				}
			}
		} else {
			cm.sendOk("請確認是否穿戴了#t1302110#。");
			cm.dispose();
			return;
		}
		cm.dispose();
	}
}
